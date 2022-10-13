package com.chint.iot.protocol.tcp.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-04-14 9:56
 */
public class CommonUtils {
	//base64字符串转byte[]
	public static byte[] base64String2ByteFun(String base64Str) {
		return Base64.decodeBase64(base64Str);
	}

	//byte[]转base64
	public static String byte2Base64StringFun(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}
}
