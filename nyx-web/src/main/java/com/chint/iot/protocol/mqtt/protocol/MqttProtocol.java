package com.chint.iot.protocol.mqtt.protocol;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.chint.iot.protocol.mqtt.entity.IotMessage;
import com.chint.iot.protocol.mqtt.handler.MqttTopicHandler;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.spi.cluster.service.IotProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MqttProtocol extends IotProtocol {
	private final static Logger LOGGER = LoggerFactory.getLogger(MqttProtocol.class);

	private final Map<String, Map<String, MqttEndpoint>> subMap = new ConcurrentHashMap<String, Map<String, MqttEndpoint>>(
			16);

	private final Map<String, MqttEndpoint> connectionMap = new ConcurrentHashMap<String, MqttEndpoint>(100);

	public MqttProtocol() {
		super(false);
		protocolFlag = "MQTT-CHINT";
	}

	@Override
	public void procOtherUpMessage(String message) {

		IotMessage iotMessage = JSONObject.parseObject(message, IotMessage.class);
		if (iotMessage != null)
		{
			String msgBody = iotMessage.getMsgBody();
			String msgType = iotMessage.getMsgType();

			if (StringUtils.isNotBlank(msgType)) {
				List<MqttEndpoint> subEndPoints = getMqttEndpoints(msgType);
				if (subEndPoints != null && subEndPoints.size() > 0) {
					for (MqttEndpoint subEndPoint : subEndPoints) {
						subEndPoint.publish(msgType, Buffer.buffer(msgBody), MqttQoS.valueOf(subEndPoint.will().getWillQos()),
								false, false);
					}
				}
			}
		}
	}

	@Override
	public void procMessage(String nodeId, String message) {
		procLocalMessage(message);
	}

	@Override
	public void procLocalMessage(String message) {
		IotMessage iotMessage = JSONObject.parseObject(message, IotMessage.class);
		String clientId = iotMessage.getClientId();
		String msgBody = iotMessage.getMsgBody();
		String msgType = iotMessage.getMsgType();
		int qos = iotMessage.getQos();
		Map<String, MqttEndpoint> endPointMaps = subMap.get(msgType);
		if (endPointMaps != null && endPointMaps.size() > 0) {
			MqttEndpoint endPoint = endPointMaps.get(clientId);
			if (endPoint != null) {
				LOGGER.info("send msg to device.msgBody={}", msgBody);
				endPoint.publish(msgType, Buffer.buffer(msgBody), MqttQoS.valueOf(qos), false, false);
			}
		}
	}

	public void addSuber(String topicName, MqttEndpoint endPoint) {
		Map<String, MqttEndpoint> endPointMaps = subMap.get(topicName);
		if (endPointMaps == null) {
			endPointMaps = new HashMap<String, MqttEndpoint>();
		}
		endPointMaps.put(endPoint.clientIdentifier(), endPoint);
		subMap.put(topicName, endPointMaps);
	}

	public void removeSuber(String topicName, MqttEndpoint endPoint) {
		Map<String, MqttEndpoint> endPointMaps = subMap.get(topicName);
		if (endPointMaps != null) {
			endPointMaps.remove(endPoint.clientIdentifier());
		}
	}

	public void removeSuber(MqttEndpoint endPoint) {
		Set<String> topicSet = subMap.keySet();
		for (String topic : topicSet) {
			removeSuber(topic, endPoint);
		}
	}

	public List<MqttEndpoint> getMqttEndpoints(String topicName) {
		List<MqttEndpoint> endPoints = null;
		Map<String, MqttEndpoint> endPointMaps = subMap.get(topicName);
		if (endPointMaps != null && endPointMaps.size() > 0) {
			endPoints = new ArrayList<MqttEndpoint>();
			Set<String> keySet = endPointMaps.keySet();
			for (String key : keySet) {
				endPoints.add(endPointMaps.get(key));
			}
		}
		return endPoints;
	}

	/**
	 * connection相关处理：
	 * addConnection
	 * getConnection
	 * removeConnection
	 * @param connectionKey
	 * @param endPoint
	 */
	public void addConnection(String connectionKey, MqttEndpoint endPoint){
		connectionMap.put(connectionKey,endPoint);
	}

	public MqttEndpoint getConnection(String connectionKey){
		return connectionMap.get(connectionKey);
	}

	public void removeConnection(String connectionKey){
		connectionMap.remove(connectionKey);
	}

	/**
	 * 支持topicFilter，获取对应的endpoint列表MqttServerVerticle
	 * subMap中包含了所有的订阅信息，如果数量级上去,需要处理
	 * @param topicFilter
	 * @return
	 */
	public List<MqttEndpoint> getMqttEndpointsByTopicFilter(String topicFilter) {
		List<MqttEndpoint> endPointsList = new ArrayList<>();
		for (String topicName : subMap.keySet()) {
			if (MqttTopicHandler.matchTopic(topicName, topicFilter)) {
				Map<String, MqttEndpoint> mqttEndpointMap = subMap.get(topicName);
				if (CollectionUtil.isNotEmpty(mqttEndpointMap)) {
					for (String clientId : mqttEndpointMap.keySet()) {
						endPointsList.add(mqttEndpointMap.get(clientId));
					}
				}
			}
		}
		//log.info("getMqttEndpointsByTopicFilter->endPointsList.size={}", endPointsList.size());
		return endPointsList;
	}

	private KafkaProducer<String, String> kafkaProducer;

	public KafkaProducer<String, String> getKafkaProducer() {
		return kafkaProducer;
	}

	public void setKafkaProducer(KafkaProducer<String, String> kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}
}
