package com.chint.iot.protocol.mqtt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageHeaderEnum {
	TOPIC("topic", "主题"),
	QOS("qos", "qos标志"),
	RETAIN("retain", "retain标志"),
	WILL("will", "will标志");

	private final String code;
	private final String value;
}
