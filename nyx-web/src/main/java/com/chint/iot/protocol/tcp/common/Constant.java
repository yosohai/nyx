package com.chint.iot.protocol.tcp.common;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-03-24 15:45
 */
public class Constant {

	public static String ARG_MSG_FORMAT_JSON = "application/json";

	public static String ROOT_URL_PREFIX ="/api";

	public static String COLUMN_DEFAULT_CODE ="DEFAULT_CODE";

	/**
	 * 设备测点数据上传
	 */
	public static String HTTP_EVENT_TYPE="HTTP-UPLOAD";
	public static String HTTP_MSG_TYPE="/HTTP-MSG";
	public static String HTTP_PROTOCOL_TYPE="HTTP-CHINT";

	public static String TCP_EVENT_TYPE="TCP-UPLOAD";
	public static String TCP_MSG_TYPE="/TCP-MSG";
	public static String TCP_PROTOCOL_TYPE="TCP-CHINT";

	/**
	 * 金太阳协议
	 */
	public static String GOLDSUN_SERVER_TYPE="GOLDSUN";
	public static String GOLDSUN_EVENT_TYPE="GOLDSUN-UPLOAD";
	public static String GOLDSUN_MSG_TYPE="/GOLDSUN-MSG";
	public static String GOLDSUN_PROTOCOL_TYPE="GOLDSUN-CHINT";

	/**
	 * RTMP协议
	 */
	public static String RTMP_SERVER_TYPE="RTMP";
	public static String RTMP_EVENT_TYPE="RTMP-UPLOAD";
	public static String RTMP_MSG_TYPE="/RTMP-MSG";
	public static String RTMP_PROTOCOL_TYPE="RTMP-CHINT";

	/**
	 * 文件数据上传
	 */
	public static String HTTP_EVENT_FILE_TYPE="HTTP-FILE-UPLOAD";
	public static String HTTP_MSG_FILE_TYPE="/HTTP-FILE-MSG";

	public static String HTTP_MSG_ID="-1";

	//24小时
	public static long TOKEN_MAX_AGE=24*60*60*1000;
	//public static String TOKEN_KEY ="0ea28ee6e6024eadbae4c678798e081d";

	/**
	 * webSocket
	 */
	public static String WEBSOCKET_SERVER_TYPE="WEBSOCKET";
	public static String WEBSOCKET_EVENT_TYPE="WEBSOCKET-UPLOAD";
	public static String WEBSOCKET_MSG_TYPE="/WEBSOCKET-MSG";
	public static String WEBSOCKET_PROTOCOL_TYPE="WEBSOCKET-CHINT";
}
