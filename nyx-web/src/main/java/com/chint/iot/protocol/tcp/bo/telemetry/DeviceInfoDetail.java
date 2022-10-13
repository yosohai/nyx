package com.chint.iot.protocol.tcp.bo.telemetry;

import lombok.Data;

/**
 * 设备信息
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-07-27 14:57
 */
@Data
public class DeviceInfoDetail {

	/**
	 * 设备唯一ID
	 */
	private String deviceId;

	/**
	 * 设备类型
	 */
	private String deviceTypeCode;

	/**
	 * 设备名称
	 */
	private String deviceName;

	/**
	 * 设备厂家
	 */
	private String deviceManufacture;
}
