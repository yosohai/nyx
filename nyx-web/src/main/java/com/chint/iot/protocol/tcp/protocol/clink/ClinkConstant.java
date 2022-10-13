package com.chint.iot.protocol.tcp.protocol.clink;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-06-16 9:53
 */
public class ClinkConstant {

	/**
	 * 自定义命令
	 */
	public static final String SPECIAL_CMD_CODE ="11201500";

	/**
	 * 请求连接设备
	 */
	public static final String CONNECT_CMD_CODE ="11201501";

	/**
	 * 请求获取配置
	 */
	public static final String CONFIG_CMD_CODE ="11201502";

	/**
	 * 请求获取数据
	 */
	public static final String DATA_CMD_CODE ="11201503";

	/**
	 * 请求打开设备
	 */
	public static final String OPEN_CMD_CODE ="11201504";

	/**
	 * 请求关闭设备
	 */
	public static final String CLOSE_CMD_CODE ="11201505";

	/**
	 * 请求复位设备
	 */
	public static final String RESET_CMD_CODE ="112015FF";

	/**
	 * 请求搜索设备
	 */
	public static final String SEARCH_CMD_CODE ="11201555";

	/**
	 * 心跳消息
	 */
	public static final String HEARTBEAT_CMD_CODE ="00000000";



	public static final String TCP_CONNECTION_PREFIX = "TCP_CONNECTION_";
	public static final String TCP_LATESTMSG_PREFIX = "TCP_LATESTMSG_";

	public static final String TCP_LATESTMSG_KEY= "latestMessage";

}
