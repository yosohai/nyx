package com.chint.iot.protocol.tcp.protocol.clink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chint.iot.protocol.tcp.common.Constant;
import com.chint.iot.protocol.tcp.service.DeviceDataService;
import com.chint.iot.protocol.tcp.utils.IgniteCacheUtils;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-06-10 10:31
 */
@Slf4j
@Service
public class CLinkMessageService {

	@Resource
	private DeviceDataService deviceDataService;

	/**
	 * 根据协议解析CLinkMessage
	 * @param message
	 * @return
	 */
	public CLinkMessage decodeRequest(String message) {
		/**
		 * 所有指令都必须有的字段
		 */
		String messageHeader = message.substring(0, 10);
		String seqNo = message.substring(10, 18);
		String cmdCode = message.substring(18, 26);
		String clusterCode = message.substring(26, 30);
		String clientId = message.substring(30, 46);
		int port = Integer.parseInt(message.substring(46, 48));

		/**
		 * 根据不同指令进行处理
		 */
		int dataLen = 0;
		String dataString = "";
		String messageEnd = "";
		if (ClinkConstant.HEARTBEAT_CMD_CODE.equals(cmdCode)) {
			dataLen = Integer.parseInt(message.substring(48, 50));
			dataString = message.substring(50, message.length() - 8);
			messageEnd = message.substring(message.length() - 8);

		} else if (ClinkConstant.SPECIAL_CMD_CODE.equals(cmdCode)) {
			//TODO
			messageEnd = message.substring(message.length() - 8);
		} else {
			messageEnd = message.substring(message.length() - 8);
		}
		/**
		 * 封装请求信息
		 */
		CLinkMessage cLinkMessage = new CLinkMessage(messageHeader, seqNo, cmdCode, clusterCode, clientId, port,
				dataLen, dataString, messageEnd);
		log.info("handleRequest.cLinkMessage={}", JSON.toJSONString(cLinkMessage));

		return cLinkMessage;
	}

	/**
	 * 根据协议解析处理返回 TODO
	 *
	 * @param connection
	 * @param message
	 * @param cLinkMessage
	 * @return
	 */
	public String processRequest(NetSocket connection, String message, CLinkMessage cLinkMessage) {
		String responseStr = "success";
		if (ClinkConstant.HEARTBEAT_CMD_CODE.equals(cLinkMessage.getCmdCode())) {

			//保存连接
			saveConnection(connection, cLinkMessage);
			//保存最新的消息
			saveLatestMessage(cLinkMessage);
		} else if (ClinkConstant.CONFIG_CMD_CODE.equals(cLinkMessage.getCmdCode())) {
			/**
			 * 输出结果（自定义命令参数）：1个或多个自定义命令参数,每个参数均为8个字符
			 * 200100
			 */
			responseStr ="3004";
		} else if (ClinkConstant.CONNECT_CMD_CODE.equals(cLinkMessage.getCmdCode())||ClinkConstant.DATA_CMD_CODE.equals(cLinkMessage.getCmdCode())) {

			//返回最新消息
			JSONObject jsonObject = IgniteCacheUtils.get(ClinkConstant.TCP_LATESTMSG_PREFIX + cLinkMessage.getClientId());
			if (jsonObject == null) {
				log.info("No data found for clientId={}", cLinkMessage.getClientId());
				responseStr = "NoDataFound";
			} else {
				responseStr = jsonObject.getString(ClinkConstant.TCP_LATESTMSG_KEY);
			}
		} else if (ClinkConstant.OPEN_CMD_CODE.equals(cLinkMessage.getCmdCode())) {

			/**
			 *设备开关状态变量属性编码预定义为2001，变量值可为00或01,00表示关，01表示开
			 * 200101
			 */
			responseStr ="200101";
		} else if (ClinkConstant.CLOSE_CMD_CODE.equals(cLinkMessage.getCmdCode())) {

			/**
			 * 设备开关状态变量属性编码预定义为2001，变量值可为00或01,00表示关，01表示开
			 * 200100
			 */
			responseStr ="200100";
		} else if (ClinkConstant.RESET_CMD_CODE.equals(cLinkMessage.getCmdCode())) {

			/**
			 * 设备复位变量属性编码预定义为2003，变量值可为00或01,00表示失败，01表示成功
			 */
			responseStr = "200301";
		} else if (ClinkConstant.SEARCH_CMD_CODE.equals(cLinkMessage.getCmdCode())) {

			/**
			 * TODO
			 * 搜索设备不需要节点地址和端口，反而是搜索执行的结果为节点地址和端口（可能多个）
			 */
			responseStr = "Not support";
		}
		return responseStr;
	}

	/**
	 * 保存连接
	 * @param connection
	 * @param cLinkMessage
	 */
	private void saveConnection(NetSocket connection, CLinkMessage cLinkMessage) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("connection", cLinkMessage);
		//设备最长5分钟不发数据，超过则认为离线
		IgniteCacheUtils.put(ClinkConstant.TCP_CONNECTION_PREFIX + cLinkMessage.getClientId(), jsonObject, 60 * 5);
	}

	/**
	 * 保存最新的消息
	 * @param cLinkMessage
	 */
	private void saveLatestMessage(CLinkMessage cLinkMessage) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ClinkConstant.TCP_LATESTMSG_KEY, cLinkMessage.getDataString());
		IgniteCacheUtils.put(ClinkConstant.TCP_LATESTMSG_PREFIX + cLinkMessage.getClientId(), jsonObject);

		JSONObject jsonObject2 = IgniteCacheUtils.get(ClinkConstant.TCP_LATESTMSG_PREFIX + cLinkMessage.getClientId());
		System.out.println("-----------------" + jsonObject2);
	}

	/**
	 * 保存信息 考虑异步
	 * @param cLinkMessage
	 * @param hostAddress
	 */
	//@Async
	public void saveCLinkMessage(CLinkMessage cLinkMessage, String hostAddress, Long currentTime) {
		String clientId = cLinkMessage.getClientId();
		String topic = Constant.TCP_MSG_TYPE + "/" + cLinkMessage.getPort();
		String source = hostAddress;
		String requestDetail = getDataJson(cLinkMessage.getDataString(), currentTime, clientId);
		if (null != requestDetail) {
			log.info("Start to save tcp msg.clientId={},cLinkMessage={},ThreadName={}", clientId,
					JSON.toJSONString(cLinkMessage), Thread.currentThread().getName());
			deviceDataService.uploadMsgData(clientId, topic, source, requestDetail, "TCP", hostAddress,"cLink");
		} else {
			log.info("Failed to saveCLinkMessage.msg={},ThreadName={}", JSON.toJSONString(cLinkMessage),
					Thread.currentThread().getName());
		}
	}

	/**
	 * 100110.3@100210@10033.6
	 * @param dataString
	 * @return
	 */
	private String getDataJson(String dataString, Long currentTime, String clientId) {
		log.info("start to parse getDataJson.dataString={}", dataString);
		try {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String[] dataArr = dataString.split("@");
			for (String str : dataArr) {
				String key = str.substring(0, 4);
				String value = str.substring(4);
				dataMap.put(key, value);
			}
			UploadMsgBO uploadMsgBO = new UploadMsgBO();
			uploadMsgBO.setClientId(clientId);
			uploadMsgBO.setTS(currentTime);
			uploadMsgBO.setUploadTimeStamp(currentTime);
			uploadMsgBO.setData(dataMap);

			return JSON.toJSONString(uploadMsgBO);
		} catch (Exception e) {
			log.error("getDataJson error", e);
			return null;
		}
	}
}
