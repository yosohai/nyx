package com.chint.iot.protocol.mqtt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author zhanglei5
 * @Description: mqtt消息
 * @date 2021-03-17 14:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqttMessageBO {
	private Type type;
	private String clientId;
	private Map<String, Object> headers;
	private String payload;
	private long storeTime;

	/**
	 * mqtt message type
	 */
	public enum Type{
		CONNECT(1),
		CONNACK(2),
		PUBLISH(3),
		PUBACK(4),
		PUBREC(5),
		PUBREL(6),
		PUBCOMP(7),
		SUBSCRIBE(8),
		SUBACK(9),
		UNSUBSCRIBE(10),
		UNSUBACK(11),
		PINGREQ(12),
		PINGRESP(13),
		DISCONNECT(14),
		WILL(15);

		private final int value;
		Type(int value) {
			this.value = value;
		}
	}
}
