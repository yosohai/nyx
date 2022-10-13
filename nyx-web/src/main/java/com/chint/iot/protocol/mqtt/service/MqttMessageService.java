package com.chint.iot.protocol.mqtt.service;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chint.iot.protocol.mqtt.constant.Constant;
import com.chint.iot.protocol.mqtt.entity.IotMessage;
import com.chint.iot.protocol.mqtt.entity.ReplyBO;
import com.chint.iot.protocol.mqtt.enums.EventTypeEnum;
import com.chint.iot.protocol.mqtt.protocol.MqttProtocol;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.messages.MqttPublishMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class MqttMessageService {
	final static Logger LOGGER = LoggerFactory.getLogger(MqttMessageService.class);
	public void eventLogHandle(MqttEndpoint endPoint, MqttPublishMessage message, String eventType, String initTopicName,
			MqttProtocol mqttProtocol,long receiveTs) {
		String cloudSeqNo = UUID.randomUUID().toString();
		String msgId = null;
		String msgBody = null;
		//String topic = null;
		if (message != null) {
			msgBody = decodeMsgBody(endPoint, message);
			msgId = "" + message.messageId();
			//topic = message.topicName();
		}
		JSONObject msgBodyJsonObject = JSON.parseObject(msgBody);
		//所有消息体都增加一个cloudSeqNo标识
		msgBodyJsonObject.put("cloudSeqNo",cloudSeqNo);

		String seqNo = msgBodyJsonObject.getString("Seq");

		int msgQos = message.qosLevel().value();
		int qos = endPoint.will().getWillQos();

		//获取deviceId
		String deviceId = getDeviceId(endPoint, msgBodyJsonObject);
		String clientId = endPoint.clientIdentifier();
		String msgHeader = null;
		//String seqNo = UUID.randomUUID().toString();
		IotMessage iotMessage = new IotMessage();
		iotMessage.setProtocolType(Constant.MQTT_PROTOCOL_TYPE);
		iotMessage.setProtocolFlag(Constant.MQTT_PROTOCOL_TYPE);
		iotMessage.setEventType(eventType);
		iotMessage.setClientId(clientId);
		iotMessage.setDeviceId(deviceId);
		iotMessage.setMsgBody(msgBodyJsonObject.toJSONString());
		iotMessage.setMsgHeader(msgHeader);
		iotMessage.setMsgId(msgId);
		iotMessage.setMsgType(initTopicName);
		iotMessage.setQos(msgQos);
		iotMessage.setSeqNo(seqNo);
		iotMessage.setTimeStamp(receiveTs);
		iotMessage.setEndpointIp(endPoint.remoteAddress().hostAddress());
		iotMessage.setUserName(endPoint.auth().getUsername());
		iotMessage.setCloudSeqNo(cloudSeqNo);

		/**
		 * 1.上报数据才往下发
		 */
		if (EventTypeEnum.PUBLISH.getCode().equals(eventType)) {
			String telemetryMsg = JSONObject.toJSONString(iotMessage);
			KafkaProducerRecord<String, String> record = KafkaProducerRecord.create(Constant.IOT_TELEMETRY_MSG_TOPIC, telemetryMsg);
			mqttProtocol.getKafkaProducer().write(record);
			LOGGER.info("successed.SendMessagetoKafka.ClientID=" + clientId + ",threadName=" + Thread.currentThread()
					.getName() + ",sendTs=" + System.currentTimeMillis() + ",telemetryMsg=" + telemetryMsg);
		}

		/**
		 * 2.下发消息给订阅者上报topic的订阅者
		 */
		List<MqttEndpoint> subEndPoints = mqttProtocol.getMqttEndpointsByTopicFilter(initTopicName);
		if (subEndPoints != null && subEndPoints.size() > 0) {
			for (MqttEndpoint subEndPoint : subEndPoints) {
				subEndPoint.publish(initTopicName, message.payload(), MqttQoS.valueOf(subEndPoint.will().getWillQos()),
						false, false);
			}
		}

		/**
		 * 3.设备主动上报帧云平台答复订阅
		 * Topic 是/Cloud/{DevTyp}/{DevSN}/All/Ack
		 */
		LOGGER.info("eventLogHandle.seq={}", seqNo);
		if (StringUtils.isNotBlank(seqNo) && seqNo.startsWith("cloud")) {
			LOGGER.info("Device Reply data.no need to reply data.seqNo={}", seqNo);
		} else {
			LOGGER.info("seqNo is normal data.seqNo={}", seqNo);
			replyData(clientId, initTopicName, mqttProtocol, receiveTs, msgBodyJsonObject, qos, subEndPoints);
		}


		/**
		 * 4.发送响应消息给客户端
		 */
		LOGGER.info("message.qosLevel()={},msgQos={}", message.qosLevel(), msgQos);
		if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
			endPoint.publishAcknowledge(message.messageId());
		} else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
			endPoint.publishReceived(message.messageId());
		}
	}

	/**
	 * 根据不同情况获取deviceId
	 * 兼容MQTT一个通道所有设备的情况
	 * 从数据中去获取唯一标识
	 * 针对特定的租户进行处理
	 * @param endPoint
	 * @return
	 */
	private String getDeviceId(MqttEndpoint endPoint, JSONObject msgBodyJsonObject) {
		String userName = endPoint.auth().getUsername();
		String deviceIdFromsn = msgBodyJsonObject.getString("sn");
		String deviceIdFromSn = msgBodyJsonObject.getString("Sn");
		String deviceIdFromSN = msgBodyJsonObject.getString("SN");
		if (StringUtils.isNotBlank(deviceIdFromsn)) {
			return deviceIdFromsn;
		}
		if (StringUtils.isNotBlank(deviceIdFromSn)) {
			return deviceIdFromSn;
		}
		if (StringUtils.isNotBlank(deviceIdFromSN)) {
			return deviceIdFromSN;
		}
		return endPoint.clientIdentifier();
	}

	/**
	 * 设备主动上报帧云平台答复订阅
	 * Topic 是/Cloud/{DevTyp}/{DevSN}/All/Ack
	 */
	private void replyData(String clientId, String initTopicName, MqttProtocol mqttProtocol, long receiveTs,
			JSONObject msgBodyJsonObject, int qos, List<MqttEndpoint> subEndPoints) {
		try {
			String[] topicArry = initTopicName.split("/");
			String devTyp = topicArry[2];
			String devSN = topicArry[3];
			String replyTopic = "/Cloud/" + devTyp + "/" + devSN + "/All/Ack";
			List<MqttEndpoint> replySubEndPoints = mqttProtocol.getMqttEndpointsByTopicFilter(replyTopic);
			if (replySubEndPoints != null && replySubEndPoints.size() > 0) {
				String Ver = msgBodyJsonObject.getString("Ver");
				//String Method = msgBodyJsonObject.getString("Method");
				String Seq = msgBodyJsonObject.getString("Seq");
				ReplyBO replyBO = new ReplyBO();
				replyBO.setVer(Ver);
				replyBO.setMethod("All/Cack");
				replyBO.setSeq(Seq);
				replyBO.setTS(receiveTs);
				replyBO.setValid(1);
				replyBO.setRemark("ack");
				for (MqttEndpoint replySubEndPoint : replySubEndPoints) {
					String replyInfo = JSON.toJSONString(replyBO);
					LOGGER.info("ReplyInfo.clientId={},Seq={},ReplyInfo={}", clientId, Seq, replyInfo);
					replySubEndPoint.publish(replyTopic, Buffer.buffer(replyInfo), MqttQoS.valueOf(qos), false,
							false);
				}
			}
		} catch (Exception e) {
			LOGGER.error("replyData error.", e);
		}
	}

	/**
	 * 根据USERNAME来处理
	 * 现在只有多点的数据才是UTF-8
	 * 平台标准是GB2312
	 * MSGBODY 字符串转码
	 * @param message
	 * @return
	 */
	private String decodeMsgBody(MqttEndpoint endPoint, MqttPublishMessage message){
		String decodeMsgBody ="";
		try {
			String userName = endPoint.auth().getUsername();
			String connClientId = endPoint.clientIdentifier();
			//哆点的数据utf-8
			/**
			 * 哆点的数据clientId都是chint_sit开头
			 * TODO:根据创建的用户名来进行处理
			 */
			if(connClientId.startsWith("chint_sit") || connClientId.startsWith("chintutf8")){
				decodeMsgBody = new String(message.payload().getBytes(), StandardCharsets.UTF_8);
			}else{
				decodeMsgBody = new String(message.payload().getBytes(),"GB2312");
			}
		} catch (Exception e) {
			LOGGER.error("failed to decodeMsgBody.消息转码失败，不转码继续处理.", e);
			decodeMsgBody = message.payload().toString();
		}
		return decodeMsgBody;
	}


}
