package com.chint.iot.protocol.tcp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chint.iot.protocol.http.protocol.HttpProtocol;
import com.chint.iot.protocol.tcp.bo.telemetry.IotMessage;
import com.chint.iot.protocol.tcp.bo.telemetry.RtmpInfoDetail;
import com.chint.iot.protocol.tcp.bo.telemetry.UploadTelemetryMsg;
import com.chint.iot.protocol.tcp.common.BaseResponse;
import com.chint.iot.protocol.tcp.common.Constant;
import com.chint.iot.protocol.tcp.common.ResultCode;
import com.chint.iot.protocol.tcp.utils.IgniteCacheUtils;
import com.chint.iot.protocol.tcp.utils.JWTUtil;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author zhanglei5
 * @Description: 设备数据服务
 * @date 2021-03-24 15:53
 */
@Slf4j
@Service
public class DeviceDataService {

	@Resource
	private HttpProtocol httpProtocol;

	@Resource
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${chint.iot.telemetry.topic}")
	private String iotTelemetryTopic;


	@Value("${chint.video.data.url}")
	private String videoDataURL;

	/**
	 *
	 * @param clientId
	 * @param username
	 * @param password
	 * @return
	 */
	public BaseResponse loginCheck(String clientId, String username, String password) {
		if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			log.info("参数不正确.");
			return BaseResponse.create(ResultCode.FAILED, "参数不正确");
		}
		boolean isValidRequest = httpProtocol.login(clientId, username, password);
		//boolean isValidRequest =true;
		if (isValidRequest) {
			String token = JWTUtil.createToken(Constant.TOKEN_MAX_AGE, clientId);
			return BaseResponse.create(ResultCode.SUCCESS.code, "校验成功", token);
		} else {
			log.info("校验失败,请检查该设备是否已经注册clientId={}", clientId);
			return BaseResponse.create(ResultCode.FAILED, "校验失败,请检查该设备是否已经注册");
		}
	}

	/**
	 * 设备遥测数据上传
	 * @param telemetryDataStr
	 * @return
	 */
	public BaseResponse uploadTelemetryData(String telemetryDataStr, String endpointIp) {
		try {
			log.info("uploadTelemetryData.telemetryDataStr={}",telemetryDataStr);
			String cloudSeqNo = cn.hutool.core.lang.UUID.randomUUID().toString();
			//1.解析上传的数据
			JSONObject telemetryDataJson = JSONObject.parseObject(telemetryDataStr);
			UploadTelemetryMsg uploadTelemetryDetail = JSON
					.parseObject(telemetryDataJson.toJSONString(), UploadTelemetryMsg.class);
			uploadTelemetryDetail.setTS(uploadTelemetryDetail.getUploadTimeStamp());
			//所有消息体都增加一个cloudSeqNo标识
			uploadTelemetryDetail.setCloudSeqNo(cloudSeqNo);

			//2.组装iotMessage
			IotMessage iotMessage = new IotMessage();
			iotMessage.setEventType(Constant.HTTP_EVENT_TYPE);
			iotMessage.setProtocolType(Constant.HTTP_PROTOCOL_TYPE);
			iotMessage.setProtocolFlag(Constant.HTTP_PROTOCOL_TYPE);
			iotMessage.setClientId(uploadTelemetryDetail.getClientId());
			iotMessage.setDeviceId(uploadTelemetryDetail.getClientId());
			iotMessage.setMsgBody(JSON.toJSONString(uploadTelemetryDetail));
			iotMessage.setMsgHeader(null);
			iotMessage.setMsgId(Constant.HTTP_MSG_ID);
			iotMessage.setSeqNo(UUID.randomUUID().toString());
			iotMessage.setTimeStamp(System.currentTimeMillis());
			iotMessage.setMsgType(uploadTelemetryDetail.getTopic());
			iotMessage.setSource(uploadTelemetryDetail.getSource());
			iotMessage.setEndpointIp(endpointIp);
			iotMessage.setCloudSeqNo(cloudSeqNo);
			String iotMessageStr = JSON.toJSONString(iotMessage);

			//3.发送数据到kafka
			kafkaTemplate.send(iotTelemetryTopic, iotMessageStr);
			log.info("successfully sendMessageToKafka.clientId="+ uploadTelemetryDetail.getClientId() + ",iotMessageStr:" + iotMessageStr);

			return BaseResponse.create(ResultCode.SUCCESS,"telemetry消息全部发送成功");
		} catch (Throwable t) {
			log.error("uploadTelemetryData error.", t);
			return BaseResponse.create(ResultCode.FAILED,"telemetry消息上传失败");
		}
	}

	public BaseResponse uploadMsgData(String clientId, String topic, String source, String msgData, String serverType, String endpointIp, String userName) {
		try {
			//log.info("{}:uploadMsgData.msgData={}", serverType, msgData);
			//log.info("[serverType:{},topic:{},clientId:{}],sent successfully.uploadMsgData.msgData:{}",serverType,topic,clientId,msgData);

			//1.组装iotMessage
			String cloudSeqNo = cn.hutool.core.lang.UUID.randomUUID().toString();
			IotMessage iotMessage = new IotMessage();
			if("TCP".equals(serverType)){
				iotMessage.setEventType(Constant.TCP_EVENT_TYPE);
				iotMessage.setProtocolType(Constant.TCP_PROTOCOL_TYPE);
				iotMessage.setProtocolFlag(Constant.TCP_PROTOCOL_TYPE);

			} else if(Constant.GOLDSUN_SERVER_TYPE.equals(serverType)){
				/**
				 * 金太阳协议
				 */
				iotMessage.setEventType(Constant.GOLDSUN_EVENT_TYPE);
				iotMessage.setProtocolType(Constant.GOLDSUN_PROTOCOL_TYPE);
				iotMessage.setProtocolFlag(Constant.GOLDSUN_PROTOCOL_TYPE);

			}else if(Constant.RTMP_SERVER_TYPE.equals(serverType)){
				/**
				 * RTMP协议
				 */
				iotMessage.setEventType(Constant.RTMP_EVENT_TYPE);
				iotMessage.setProtocolType(Constant.RTMP_PROTOCOL_TYPE);
				iotMessage.setProtocolFlag(Constant.RTMP_PROTOCOL_TYPE);

			} else if(Constant.WEBSOCKET_SERVER_TYPE.equals(serverType)){
				/**
				 * WEBSOCKET协议
				 */
				iotMessage.setEventType(Constant.WEBSOCKET_EVENT_TYPE);
				iotMessage.setProtocolType(Constant.WEBSOCKET_PROTOCOL_TYPE);
				iotMessage.setProtocolFlag(Constant.WEBSOCKET_PROTOCOL_TYPE);

			}else{
				iotMessage.setEventType(Constant.HTTP_EVENT_TYPE);
				iotMessage.setProtocolType(Constant.HTTP_PROTOCOL_TYPE);
				iotMessage.setProtocolFlag(Constant.HTTP_PROTOCOL_TYPE);
			}
			iotMessage.setClientId(clientId);
			iotMessage.setDeviceId(clientId);

			/**
			 * enrich msgData
			 */
			try{
				JSONObject msgBodyJsonObject = JSON.parseObject(msgData);
				//所有消息体都增加一个cloudSeqNo标识
				msgBodyJsonObject.put("cloudSeqNo",cloudSeqNo);
				iotMessage.setMsgBody(msgBodyJsonObject.toJSONString());
			}catch (Exception e){
				log.error("msg will be processed but need to fix error.", e);
				iotMessage.setMsgBody(msgData);
			}
			iotMessage.setMsgHeader(null);
			iotMessage.setMsgId(Constant.HTTP_MSG_ID);
			iotMessage.setMsgType(topic);
			iotMessage.setSeqNo(UUID.randomUUID().toString());
			iotMessage.setTimeStamp(System.currentTimeMillis());
			iotMessage.setSource(source);
			iotMessage.setEndpointIp(endpointIp);
			iotMessage.setUserName(userName);
			iotMessage.setCloudSeqNo(cloudSeqNo);
			String iotMessageStr = JSON.toJSONString(iotMessage);

			//3.发送数据到kafka
			kafkaTemplate.send(iotTelemetryTopic, iotMessageStr);
			log.info("[serverType:{},topic:{},clientId:{}],sent successfully.iotMessageStr:{}",serverType,topic,clientId,iotMessageStr);

			return BaseResponse.create(ResultCode.SUCCESS, "消息全部发送成功");
		} catch (Throwable t) {
			log.error("serverType:" + serverType + ",uploadTelemetryData error.", t);
			return BaseResponse.create(ResultCode.FAILED, serverType + ":消息上传失败");
		}
	}

	/**
	 * 处理rtmp流信息
	 * @param rc
	 * @return
	 */
	public int processRtmpStream(RoutingContext rc){
		String clientId = rc.request().getParam("name");
		String username = rc.request().getParam("username");
		String password = rc.request().getParam("password");
		//publish_done
		String callType = rc.request().getParam("call");
		log.info("callType={},clientId={},username={},password={}",callType,clientId, username, password);

		/**
		 * 1.检验传入参数
		 */
		if(StringUtils.isEmpty(clientId) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
			log.info("handelRtmpPublishAuth failed. input param error");
			return 500;
		}

		/**
		 * 2.校验设备用户名密码
		 */
		boolean isValidRequest = httpProtocol.login(clientId, username, password);
		if(!isValidRequest){
			log.info("handelRtmpPublishAuth failed. username or password is not correct");
			return 500;
		}

		/**
		 * 3.开始推流
		 * 处理用户名密码验证问题(不支持自动注册，防止推送非法视频)
		 */
		if ("publish_done".equals(callType)) {
			//设备下线
			IgniteCacheUtils.removeDeviceStatus(clientId);
			log.info("callType:{},device:{} publish done and set device offline.",callType, clientId);
		}else{
			//设备上线
			IgniteCacheUtils.putDeviceStatus(clientId,"ONLINE");
			log.info("callType:{},device:{} publish start and set device online.",callType, clientId);
		}

		log.info("processRtmpStream success.");
		return 200;
	}

	/**
	 * @param rc
	 * @return
	 */
	public int processRtmp(RoutingContext rc){
		String clientId = rc.request().getParam("name");
		String username = rc.request().getParam("username");
		String password = rc.request().getParam("password");
		String deviceTypeCode = rc.request().getParam("deviceTypeCode");
		String topic = "/Edge/" + deviceTypeCode + "/" + clientId + "/RTG";
		String endpointIp =rc.request().remoteAddress().hostAddress();
		if (clientId == null || deviceTypeCode == null) {
			log.info("Ignore request.clientId={},deviceTypeCode={}", clientId, deviceTypeCode);
			return 201;
		}

		RtmpInfoDetail rtmpInfoDetail = new RtmpInfoDetail();
		rtmpInfoDetail.setDeviceId(clientId);
		rtmpInfoDetail.setDeviceTypeCode(deviceTypeCode);
		//http://10.126.12.108:81/hls/rtmpzhangleitest.m3u8
		rtmpInfoDetail.setDataURL(videoDataURL + clientId + ".m3u8");

		/**
		 * 发送给下游
		 */
		uploadMsgData(clientId, topic, Constant.RTMP_SERVER_TYPE, JSON.toJSONString(rtmpInfoDetail),
				Constant.RTMP_SERVER_TYPE, endpointIp, username);
		log.info("processRtmp success.");
		return 200;
	}
}
