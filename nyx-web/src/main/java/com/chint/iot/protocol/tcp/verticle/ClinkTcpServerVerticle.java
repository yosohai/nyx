package com.chint.iot.protocol.tcp.verticle;

import com.chint.iot.protocol.tcp.protocol.clink.CLinkMessage;
import com.chint.iot.protocol.tcp.protocol.clink.CLinkMessageService;
import com.chint.iot.protocol.tcp.protocol.clink.ClinkConstant;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-06-10 10:13
 */
@Slf4j
@Component
public class ClinkTcpServerVerticle {

	@Resource
	private Vertx vertx;

	@Value("${chint.tcp.listenPort}")
	private String tcpServerListenPort;

	@Resource
	private CLinkMessageService cLinkMessageService;

	@PostConstruct
	public void start(){
		//创建tcpServer并监听服务端口
		int chintTcpPort = Integer.parseInt(tcpServerListenPort);
		createTcpServer(chintTcpPort);
	}

	private void createTcpServer(int tcpServerListenPort) {
		NetServer tcpServer = vertx.createNetServer();
		// 处理连接请求
		tcpServer.connectHandler(connection -> {
			/**
			 * 监听客户端的连接
			 */
			String hostAddress = connection.remoteAddress().hostAddress();
			log.info("Success to request tcpServer.RequestHost=" + hostAddress);

			/**
			 * 1.解析报文，封装为协议对象
			 * 2.按照协议响应给客户端
			 */
			connection.handler(buffer -> {
				Long currentTime = System.currentTimeMillis();
				//1.解析报文，封装为协议对象
				String reqMessage = buffer.toString();
				log.info("TCPServer receive msg:{}",reqMessage);
				CLinkMessage cLinkMessage = null;
				try {
					 cLinkMessage = cLinkMessageService.decodeRequest(reqMessage);
				}catch (Exception e){
					log.info("Invalid message={}",reqMessage);
					log.error("handleRequest error", e);
					connection.write(Buffer.buffer("Invalid request."));
					return;
				}
				if (cLinkMessage != null) {
					//2.按照协议响应给客户端
					String responseMsg = cLinkMessageService.processRequest(connection, reqMessage, cLinkMessage);

					//3.异步上报消息
					if (ClinkConstant.HEARTBEAT_CMD_CODE.equals(cLinkMessage.getCmdCode())) {
						cLinkMessageService.saveCLinkMessage(cLinkMessage, hostAddress, currentTime);
					}

					//4.返回结果
					connection.write(Buffer.buffer(responseMsg));
				}
			});

			/**
			 * 监听客户端的退出连接
			 */
			connection.closeHandler(close -> {
				log.info("Tcp client closed");
			});
		});

		/**
		 * 监听端口
		 */
		tcpServer.listen(tcpServerListenPort, res -> {
			if (res.succeeded()) {
				log.info("CLink TCP服务器启动成功,监听端口:{}", tcpServerListenPort);
			} else {
				log.error("Error on starting tcp server,reason is {}", res.cause().getMessage());
			}
		});
	}
}
