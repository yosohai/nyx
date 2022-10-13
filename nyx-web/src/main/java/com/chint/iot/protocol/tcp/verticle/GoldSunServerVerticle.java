package com.chint.iot.protocol.tcp.verticle;

import com.alibaba.fastjson.JSON;
import com.chint.iot.protocol.tcp.protocol.goldsun.GoldsunMessageService;
import com.chint.protocol.goldsun.struct.*;
import com.chint.protocol.goldsun.util.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.UUID;

/**
 * 金太阳协议
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-07-23 10:13
 */
@Slf4j
@Component
public class GoldSunServerVerticle {

	@Resource
	private Vertx vertx;

	@Value("${chint.goldsun.listenPort}")
	private String goldsunServerListenPort;

	@Resource
	private GoldsunMessageService goldsunMessageService;

	@PostConstruct
	public void start(){
		//创建goldsunServer并监听服务端口
		int goldsunServerPort = Integer.parseInt(goldsunServerListenPort);
		createTcpServer(goldsunServerPort);
	}

	/**
	 * RecordParser.newFixed(8);
	 * 8:协议的头8个字节
	 * @param goldsunServerPort
	 */
	private void createTcpServer(int goldsunServerPort) {
		NetServer tcpServer = vertx.createNetServer();
		// 处理连接请求
		tcpServer.connectHandler(connection -> {
			String endpointIp = connection.remoteAddress().hostAddress();

			//connectionId:unitSn
			String[] connectionId = { UUID.randomUUID().toString() };
			String[] connectionPwd = { UUID.randomUUID().toString() };
			// 构造parser
			RecordParser parser = RecordParser.newFixed(8);
			parser.setOutput(new Handler<Buffer>() {
				int size = -1;
				@Override
				public void handle(Buffer buffer) {
					//电站单元序列号
					if (-1 == size) {
						ByteBuf buf = buffer.getByteBuf();
						//head 6969
						byte[] headBytes = NettyUtil.readBytes2(buf);
						String head = RadixUtil.bytes2String16(headBytes);
						//package version
						byte ver1 = buf.readByte();
						byte ver2 = buf.readByte();
						String ver = ver1 + "." + ver2;
						//长度
						int len = buf.readIntLE();
						//2 代表CRC16效验
						size = len + 2;
						//log.info("FFFFFFFFFFFFFFF.head={},ver={},len={},size={}", head, ver, len,size);
						parser.fixedSizeMode(size);
					} else {
						//String hexStr = ByteBufUtil.hexDump(buffer.getByteBuf());
						//log.info("解析请求:"+  hexStr);
						try {
							Long currentTime = System.currentTimeMillis();
							StructCommon structCommon = GoldSunMessageDecoder.decode2(buffer.getByteBuf());
							//数据类型3-设备信息数据，4-实时数据
							//int dataType = structCommon.getType();
							//log.info("tcp structCommon.type:{}",structCommon.getType());
							/**
							 * 1.认证
							 */
							Object obj = structCommon.getData();
							if (obj instanceof StructAuthReq1) {
								/**
								 * TODO:需要对sn和密码进行校验，依赖于平台的修改
								 * 暂时直接返回不做校验
								 */
								String initConnectionId = connectionId[0];
								String initConnectionPwd = connectionPwd[0];

								//unitSn当作每次连接的id
								connectionId[0] = ((StructAuthReq1) obj).getSn();
								connectionPwd[0] = ((StructAuthReq1) obj).getPwd();

								log.info("connectionId:{}->{}", initConnectionId, connectionId[0]);
								log.info("connectionPwd:{}->{}", initConnectionPwd, connectionPwd[0]);

								//log.info("电站单元信息:{}", JSON.toJSONString(structCommon));
								connection.write(Buffer.buffer(GoldSunUtil.commonAuthRes1()));

							}else if (obj instanceof StructAuthReq2) {
								connection.write(Buffer.buffer(GoldSunUtil
										.commonRes(GoldSunUtil.authSuccessRes2(), DataPackageTypeUtil.AUTH_TYPE)));

							}else if (obj instanceof StructStation) {
								/**
								 * NOTE:电站信息 是需要在平台上先创建的
								 * 目前电站信息设备不上传
								 */
								log.info("电站信息:{}", JSON.toJSONString(structCommon));

							}else if (obj instanceof StructDeviceInfo) {
								/**
								 * 2.处理设备信息
								 */
								//log.info("单元信息:{},设备信息:{}", connectionId[0], JSON.toJSONString(structCommon));
								goldsunMessageService.processGoldSunMessage(obj, connectionId[0], endpointIp);
								connection.write(Buffer.buffer(GoldSunUtil.commonRes(StructSuccessRes.deviceInfoSuccessRes(),
										DataPackageTypeUtil.DEVICE_INFO_TYPE)));

							}else if (obj instanceof StructRealtimeData) {
								/**
								 * 3.处理实时数据信息
								 */
								//log.info("实时数据信息:{}", JSON.toJSONString(structCommon));
								goldsunMessageService.processGoldSunMessage(obj, connectionId[0], endpointIp);
								connection.write(Buffer.buffer(GoldSunUtil.commonRes(StructSuccessRes.realTimeDataSuccessRes(),
										DataPackageTypeUtil.REALTIME_DATA_TYPE)));

							}else {
								log.warn("未识别的消息:{}", JSON.toJSONString(structCommon));
							}
						} catch (Exception e) {
							String hexStr = ByteBufUtil.hexDump(buffer.getByteBuf());
							log.error("解析出错:"+  hexStr);
							e.printStackTrace();
						}

						//重新设置fixedsize
						parser.fixedSizeMode(8);
						size = -1;
					}
				}
			});
			connection.handler(parser);

			/**
			 * 监听客户端的退出连接
			 */
			connection.closeHandler(close -> {
				log.info("goldsun client closed");
			});
		});

		/**
		 * 监听端口
		 */
		tcpServer.listen(goldsunServerPort, res -> {
			if (res.succeeded()) {
				log.info("金太阳协议服务器启动成功,监听端口:{}", goldsunServerPort);
			} else {
				log.error("Error on starting tcp server,reason is {}", res.cause().getMessage());
			}
		});
	}
}
