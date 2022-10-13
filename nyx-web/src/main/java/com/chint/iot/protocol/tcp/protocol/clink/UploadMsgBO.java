package com.chint.iot.protocol.tcp.protocol.clink;

import lombok.Data;

import java.util.Map;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-06-10 13:19
 */
@Data
public class UploadMsgBO {

	/**
	 * ~上传时间戳（精确到毫秒）
	 */
	private Long TS;

	/**
	 * ~上传时间戳（精确到毫秒）
	 */
	private Long uploadTimeStamp;

	/**
	 * 设备唯一标识ID
	 */
	private String clientId;

	/**
	 * 测点数据集合
	 */
	private Map<String,Object> data;
}
