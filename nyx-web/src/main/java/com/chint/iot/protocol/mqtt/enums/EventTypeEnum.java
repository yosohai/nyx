package com.chint.iot.protocol.mqtt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventTypeEnum {
	CONNECT("CONNECT", "建立连接"),
	DISCONNECT("DISCONNECT", "断开连接"),
	CLOSE("CLOSE", "关闭连接"),
	SUBSCRIBE("SUBSCRIBE", "订阅"),
	UNSUBSCRIBE("UNSUBSCRIBE", "取消订阅"),
	PUBLISH("PUBLISH", "发布消息")
	;

	private final String code;
	private final String value;
}
