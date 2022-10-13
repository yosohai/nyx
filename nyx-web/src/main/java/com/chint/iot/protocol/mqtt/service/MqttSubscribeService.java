package com.chint.iot.protocol.mqtt.service;

import cn.hutool.core.collection.CollectionUtil;
import com.chint.iot.protocol.mqtt.enums.MessageTypeEnum;
import com.chint.iot.protocol.mqtt.handler.MqttTopicHandler;
import com.chint.iot.protocol.mqtt.protocol.MqttProtocol;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttTopicSubscription;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MqttSubscribeService {
	final static Logger LOGGER = LoggerFactory.getLogger(MqttSubscribeService.class);

	public static void subscribeHandle(MqttEndpoint endpoint, MqttProtocol mqttProtocol,
			MqttMessageService mqttTopicPaternService) {

		// handling requests for subscriptions
		endpoint.subscribeHandler(subscribe -> {
			String clientId = endpoint.clientIdentifier();
			long receiveTs = System.currentTimeMillis();
			try {
				List<MqttQoS> grantedQosLevels = new ArrayList<MqttQoS>();
				if (CollectionUtil.isNotEmpty(subscribe.topicSubscriptions())) {
					LOGGER.info("subscribe.topicSubscriptions.size:" + subscribe.topicSubscriptions().size());
				}
				for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
					LOGGER.info("Client {} subscription for topic = {} ,QoS = {}", clientId, s.topicName(),
							s.qualityOfService().value());
					if (MqttTopicHandler.isValidTopicFilter(s.topicName(), MessageTypeEnum.SUBSCRIBE.getCode())) {

						grantedQosLevels.add(s.qualityOfService());
						// 缓存订阅客户端
						mqttProtocol.addSuber(s.topicName(), endpoint);
					} else {
						LOGGER.info("subscribe failed.topicName:" + s.topicName());
						grantedQosLevels.add(MqttQoS.FAILURE);
					}
				}
				// ack the subscriptions request
				endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);

				// 发送客户端订阅消息给数据平台
				//mqttTopicPaternService.eventLogHandle(endpoint, null, EventTypeEnum.SUBSCRIBE.getCode(), null,mqttProtocol,receiveTs);
			} catch (Exception e) {
				LOGGER.error(
						"Happened to an error while handle the subscribe request from client {}, error message is {}",
						clientId, ExceptionUtils.getStackTrace(e));
			}
		});

	}

	public static void unSubscribeHandle(MqttEndpoint endpoint, MqttProtocol mqttProtocol,
			MqttMessageService mqttTopicPaternService) {

		endpoint.unsubscribeHandler(unsubscribe -> {
			String clientId = endpoint.clientIdentifier();
			long receiveTs = System.currentTimeMillis();
			try {
				for (String topic : unsubscribe.topics()) {
					LOGGER.info("Client {} Unsubscription for topic: {}", clientId, topic);
					// 删除缓存订阅客户端
					mqttProtocol.removeSuber(topic, endpoint);
				}
				// ack the subscriptions request
				endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
				// 发送客户端取消订阅消息给数据平台
				//mqttTopicPaternService.eventLogHandle(endpoint, null, EventTypeEnum.UNSUBSCRIBE.getCode(), null,mqttProtocol,receiveTs);
			} catch (Exception e) {
				LOGGER.error(
						"Happened to an error while handle the unsubscribe request from client {}, error message is {}",
						clientId, ExceptionUtils.getStackTrace(e));
				endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE);
				return;
			}
		});

	}
}
