package com.chint.iot.protocol.mqtt.handler;

import com.alibaba.fastjson.JSON;
import com.chint.iot.protocol.mqtt.cache.IgniteCacheService;
import com.chint.iot.protocol.mqtt.constant.CacheConstant;
import com.chint.iot.protocol.mqtt.entity.MqttMessageBO;
import com.chint.iot.protocol.mqtt.enums.MessageHeaderEnum;
import io.vertx.mqtt.MqttWill;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglei5
 * @Description: mqtt消息处理器
 * @date 2021-03-17 14:40
 */
@Slf4j
public class MqttMessageHandler {

	/**
	 * 1.遗嘱标志（Will Flag）被设置为 1，表示如果连接请求被接受了，
	 * 遗嘱（Will Message）消息必须被存储在服务端并且与这个网络连接关联。
	 * 2.如果遗嘱标志被设置为 1，连接标志中的 Will QoS 和 Will Retain 字段会被服务端用到，
	 * 同时有效载荷中必须包含Will Topic和Will Message字段
	 * 3.如果遗嘱标志被设置为 0，连接标志中的 Will QoS 和 Will Retain 字段 必须 设置为0，
	 * 并且有效载荷中 不能 包含 Will Topic 和 Will Message 字段
	 *
	 * Note: 只需要处理willflag=true的情况，只有这种情况才会去保存
	 * @param clientId
	 * @param mqttWillDetail
	 */
	public void storeWillMessage(String clientId, MqttWill mqttWillDetail, IgniteCacheService igniteCacheService){

		boolean isWillFlag = mqttWillDetail.isWillFlag();
		String willTopic = mqttWillDetail.getWillTopic();
		String willMessage = String.valueOf(mqttWillDetail.getWillMessage());
		//遗嘱判断
		if (isWillFlag) {
			if (willMessage == null || StringUtils.isBlank(willTopic)) {
				throw new RuntimeException("Will Topic和Will Message字段不能为空");
			}
			//构建mqttWillMessage
			Map<String, Object> headers = new HashMap<>();
			headers.put(MessageHeaderEnum.RETAIN.getCode(), mqttWillDetail.isWillRetain());
			headers.put(MessageHeaderEnum.QOS.getCode(), mqttWillDetail.getWillQos());
			headers.put(MessageHeaderEnum.TOPIC.getCode(), willTopic);
			headers.put(MessageHeaderEnum.WILL.getCode(), isWillFlag);
			MqttMessageBO mqttWillMessage = new MqttMessageBO(MqttMessageBO.Type.WILL, clientId, headers, willMessage,
					System.currentTimeMillis());

			//保存mqttWillMessage ignite
			String willMsgCacheKey = CacheConstant.MQTT_WILL_MSG_PREFIX + clientId;
			log.info("start to cache willMessage.willMsgCacheKey={},msg={}", willMsgCacheKey,
					JSON.toJSONString(mqttWillMessage));
			igniteCacheService.put(willMsgCacheKey, JSON.toJSONString(mqttWillMessage));
		}
	}

	/**
	 * 清除遗嘱消息
	 * @param clientId
	 */
	public void clearWillMessage(String clientId, IgniteCacheService igniteCacheService) {
		String willMsgCacheKey = CacheConstant.MQTT_WILL_MSG_PREFIX + clientId;
		igniteCacheService.remove(willMsgCacheKey);
	}
}
