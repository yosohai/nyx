package com.chint.iot.protocol.mqtt.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(chain = true)
@Data
public class IotMessage implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1747942711872088969L;

	/**
	 * ~消息质量
	 */
	private Integer qos;

	/**
	 * 客户端标识
	 */
	private String clientId;

	/**
	 * ~设备Id
	 */
	private String deviceId;

	/**
	 * ~事件类型
	 */
	private String eventType;

	/**
	 * ~消息体
	 */
	private String msgBody;

	/**
	 * ~消息头
	 */
	private String msgHeader;

	/**
	 * ~消息Id
	 */
	private String msgId;

	/**
	 * ~消息类型
	 */
	private String msgType;

	/**
	 * ~消息序列
	 */
	private String seqNo;

	/**
	 * ~时间戳（精确到毫秒）
	 */
	private Long timeStamp;

	/**
	 * 协议类型
	 */
	private String protocolType;

	/**
	 * 协议类型
	 */
	private String protocolFlag;

	/**
	 * 连接终端地址
	 */
	private String endpointIp;

	/**
	 * 用户信息
	 */
	private String userName;

	/**
	 * ~平台的消息序列
	 */
	private String cloudSeqNo;
}
