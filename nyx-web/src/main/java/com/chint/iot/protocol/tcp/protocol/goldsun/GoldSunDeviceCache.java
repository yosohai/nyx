package com.chint.iot.protocol.tcp.protocol.goldsun;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 金太阳协议：设备上传设备信息后，设备信息缓存
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-07-28 12:54
 */
public class GoldSunDeviceCache {
	/**
	 * key:unitSn+deviceAddr
	 * value:deviceType
	 */
	public static Map<String, Integer> deviceInfoMap = new ConcurrentHashMap<String, Integer>();

	/**
	 * @param unitSn
	 * @param deviceAddr
	 */
	public static void addDeviceInfo(String unitSn, int deviceAddr, int deviceType) {
		deviceInfoMap.put(unitSn + deviceAddr, deviceType);
	}

	/**
	 * @param unitSn
	 * @param deviceAddr
	 * @return
	 */
	public static Integer getDeviceType(String unitSn, int deviceAddr){
		return deviceInfoMap.get((unitSn + deviceAddr));
	}
}
