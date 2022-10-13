package com.chint.iot.protocol.tcp.bo.telemetry;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Map;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-03-25 12:44
 */
@Data
public class UploadTelemetryMsg {

	/**
	 * ~上传时间戳（精确到毫秒）
	 */
	private Long uploadTimeStamp;

	/**
	 * ~上传时间戳（精确到毫秒）
	 */
	@JSONField(name = "TS")
	private Long TS;

	/**
	 * 设备唯一标识ID
	 */
	private String clientId;

	/**
	 * 测点数据集合
	 */
	private Map<String,Object> data;

	/**
	 * 设备类型
	 * 消息类型/deviceType
	 */
	private String topic;

	/**
	 * 数据来源
	 */
	private String source;

	/**
	 * 唯一序号
	 */
	private String cloudSeqNo;
}
