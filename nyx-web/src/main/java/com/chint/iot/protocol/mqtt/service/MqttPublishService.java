package com.chint.iot.protocol.mqtt.service;

import com.chint.iot.protocol.mqtt.enums.EventTypeEnum;
import com.chint.iot.protocol.mqtt.enums.MessageTypeEnum;
import com.chint.iot.protocol.mqtt.handler.MqttTopicHandler;
import com.chint.iot.protocol.mqtt.protocol.MqttProtocol;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.mqtt.MqttEndpoint;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttPublishService {
	final static Logger LOGGER = LoggerFactory.getLogger(MqttPublishService.class);

	public static void publishHandle(MqttEndpoint endpoint, MqttProtocol mqttProtocol,
			MqttMessageService mqttTopicPaternService, String serverName) {

		endpoint.publishHandler(message -> {
			String clientId = endpoint.clientIdentifier();
			long receiveTs = System.currentTimeMillis();
			//LOGGER.info("publishHandle.clientId=" + clientId + ",receiveTs=" + receiveTs + ",threadName=" + Thread.currentThread().getName()+",serverName="+serverName);
			try {
				if (!MqttTopicHandler.isValidTopicFilter(message.topicName(), MessageTypeEnum.PUBLISH.getCode())) {

					LOGGER.info("非法的topic:" + message.topicName());
					endpoint.publish(message.topicName(), message.payload(), MqttQoS.FAILURE, false, false);
				}else {

/*					LOGGER.info("Just received message from client {} on topic [{}] payload [{}] with QoS [{}]", clientId,
							message.topicName(), message.payload(), message.qosLevel());*/
					// 上送客户端发布消息到数据平台
					mqttTopicPaternService.eventLogHandle(endpoint, message, EventTypeEnum.PUBLISH.getCode(),
							message.topicName(), mqttProtocol,receiveTs);
				}
			} catch (Exception e) {
				LOGGER.error("Happened to an error while handle the message send from client {} , error message is {}",
						clientId, ExceptionUtils.getStackTrace(e));
			}
		}).publishReleaseHandler(messageId -> {
			endpoint.publishComplete(messageId);
		});

	}
}
