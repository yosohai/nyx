package com.chint.iot.protocol.mqtt.constant;

public interface MqttTopicPattern {
	/**
	 * ~设备上报设备信息
	 */
    String DEVICE_PUBLISH_INFO = "/Edge/[^/]{1,}/[^/]{1,}/Info";
	/**
	 * ~云端向设备召读设备信息
	 */
    String DEVICE_SUBSCRIBE_INFO = "/Cloud/[^/]{1,}/[^/]{1,}/Info/Call";

	/**
	 * ~设备上报设备工况
	 */
    String DEVICE_PUBLISH_STATUS = "/Edge/[^/]{1,}/[^/]{1,}/Status";

	/**
	 * ~云端向设备召读设备工况
	 */
    String DEVICE_SUBSCRIBE_STATUS = "/Cloud/[^/]{1,}/[^/]{1,}/Status/Call";

	/**
	 * ~设备上报实时数据
	 */
    String DEVICE_PUBLISH_RTG = "/Edge/[^/]{1,}/[^/]{1,}/RTG";

	/**
	 * ~云端向设备召读实时数据
	 */
    String DEVICE_SUBSCRIBE_RTG = "/Cloud/[^/]{1,}/[^/]{1,}/RTG/Call";

	/**
	 * ~设备越死区上报数据
	 */
    String DEVICE_PUBLISH_RTD = "/Edge/[^/]{1,}/[^/]{1,}/RTD";

	/**
	 * ~设备上报实时报警数据
	 */
    String DEVICE_PUBLISH_ALARM = "/Edge/[^/]{1,}/[^/]{1,}/Alarm";

	/**
	 * ~设备上报实时消息数据
	 */
    String DEVICE_PUBLISH_MESSAGE = "/Edge/[^/]{1,}/[^/]{1,}/Message";

	/**
	 * ~设备断点续传自动上报
	 */
    String DEVICE_PUBLISH_HISTORY = "/Edge/[^/]{1,}/[^/]{1,}/History";

	/**
	 * ~云端向设备发送历史数据请求
	 */
    String DEVICE_SUBSCRIBE_HISTORY = "/Cloud/[^/]{1,}/[^/]{1,}/History/Call";

	/**
	 * ~云端向设备发送对时请求
	 */
    String DEVICE_SUBSCRIBE_TIMING = "/Cloud/[^/]{1,}/[^/]{1,}/Timing/Call";

	/**
	 * ~设备响应校时并上报时间
	 */
    String DEVICE_PUBLISH_TIMING = "/Edge/[^/]{1,}/[^/]{1,}/Timing/Cack";

	/**
	 * ~云端向设备发送控制指令
	 */
    String DEVICE_SUBSCRIBE_CMDSET = "/Cloud/[^/]{1,}/[^/]{1,}/CmdSet/Call";

	/**
	 * ~设备响应设置参数指令
	 */
    String DEVICE_PUBLISH_CMDSET = "/Edge/[^/]{1,}/[^/]{1,}/CmdSet/Cack";

	/**
	 * ~云端向设备发送档案指令
	 */
    String DEVICE_SUBSCRIBE_ARCHIVE = "/Cloud/[^/]{1,}/[^/]{1,}/Archive/Call";

	/**
	 * ~设备响应档案指令
	 */
    String DEVICE_PUBLISH_ARCHIVE = "/Edge/[^/]{1,}/[^/]{1,}/Archive/Cack";

	/**
	 * ~云端向设备发送透传指令
	 */
    String DEVICE_SUBSCRIBE_TRANS = "/Cloud/[^/]{1,}/[^/]{1,}/Trans/Call";

	/**
	 * ~设备响应透传指令
	 */
    String DEVICE_PUBLISH_TRANS = "/Edge/[^/]{1,}/[^/]{1,}/Trans/Cack";

	/**
	 * ~云端向设备发送答复指令
	 */
    String DEVICE_SUBSCRIBE_ACK = "/Cloud/[^/]{1,}/[^/]{1,}/All/Ack";

/*	*//**
	 * ~云端广播
	 *//*
	public static final String DEVICE_SUBSCRIBE_BROADCAST = "^/Cloud/Broadcast/[^/]{1,}";*/
}
