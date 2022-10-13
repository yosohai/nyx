package com.chint.iot.protocol.tcp.common;

/**
 * @author zhanglei5
 * @Description: ResultCode
 * @date 2021-03-24 14:38
 */
public enum ResultCode {
	SUCCESS("200", "成功"),
	FAILED("10000", "失败");

	public String code;
	public String msg;

	ResultCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
