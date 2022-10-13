package com.chint.iot.protocol.tcp.protocol.goldsun;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 处理金太阳协议:设备类型
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-07-27 13:11
 */
@AllArgsConstructor
@Getter
public enum GoldsunDeviceTypeEnum {
	NBQ(1, "NBQ","逆变器"),
	HLX(2, "HLX","汇流箱"),
	ZLPD(3, "ZLPD","直流配电柜"),
	JLPD(4, "JLPD","交流配电柜"),
	HJJC(5, "HJJC","环境监测装置"),
	DBJL(6, "DBJL","电表计量装置"),
	GFGZ(7, "GFGZ","光伏跟踪系统"),
	BMS(8, "BMS","BMS");

	private final int code;
	private final String value;
	private final String desc;

	/**
	 * 根据code获取编码
	 * @param goldsunDeviceTypeCode
	 * @return
	 */
	public static String getGoldsunDeviceTypeByCode(Integer goldsunDeviceTypeCode) {
		if (goldsunDeviceTypeCode == null) {
			return "";
		}
		for (GoldsunDeviceTypeEnum goldsunDeviceType: GoldsunDeviceTypeEnum.values()) {
			if (goldsunDeviceTypeCode.equals(goldsunDeviceType.getCode())) {
				return goldsunDeviceType.getValue();
			}
		}
		return "";
	}
}
