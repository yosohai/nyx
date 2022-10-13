package com.chint.iot.protocol.tcp.bo.telemetry;

import lombok.Data;

/**
 * @author zhanglei5
 * @Description: IotMessage 所有协议的返回格式和名称保持一致
 * @date 2021-03-26 19:46
 */
@Data
public class IotMessage {

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
	 * 客户端标识
	 */
	private String source;

	/**
	 * 连接终端地址
	 */
	private String endpointIp;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * ~平台的消息序列
	 */
	private String cloudSeqNo;
}
