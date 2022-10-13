package com.chint.iot.protocol.tcp.protocol.clink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-06-09 9:24
 *
 * 2013012820X99999991120150150006ea2a7d1bd6745982018100110.3@100210@10033.6FFFFEEEE
 * 普通命令:
 * 消息头       序列号	  命令码    簇号      地址      端口	     |  数据流长度     数据流                     结束符
 * 10个字符     8个字符    8个字符   4个字符   16个字符   2个字符    |  2个字符       100110.3@100210@10033.6    8个字符
 * 2013012820                      5000                 20                        100110.3@100210@10033.6    FFFFEEEE
 *
 *
 * 心跳
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CLinkMessage {

	/**
	 * 消息头
	 * message.substring(0, 10)
	 * 2013012820
	 */
	private String messageHeader;

	/**
	 * 序列号
	 * message.substring(10, 18)
	 */
	private String seqNo;

	/**
	 * 命令码：
	 * message.substring(18, 26)
	 *
	 * 设备连接:11201501
	 * 获取配置:11201502
	 * 获取数据:11201503
	 * 打开设备:11201504
	 * 关闭设备:11201505
	 * 复位设备:112015FF
	 * 搜索设备:11201555
	 * 心跳消息:00000000
	 * 自定义命令:11201500
	 */
	private String cmdCode;

	/**
	 * 簇号
	 * message.substring(26, 30)
	 * 5000
	 */
	private String clusterCode;

	/**
	 * 设备唯一编号
	 *  message.substring(30, 46)
	 */
	private String clientId;

	/**
	 * 端口
	 * update：端口当作设备类型编码使用
	 * message.substring(46, 48);
	 * 20
	 */
	private int port;

	/**
	 * message.substring(48, 50);
	 */
	private int dataLen;

	/**
	 * 100110.3@100210@10033.6
	 * 说明:
	 * 根据@分割，前四位代表key,后面几位代表value
	 */
	private String dataString;

	/**
	 * 结束符
	 * message.substring(message.length() - 8);
	 * FFFFEEEE
	 */
	private String messageEnd;

	public static CLinkMessage parseMsgStr(String message) {
		String messageHeader = message.substring(0, 10);
		String seqNo = message.substring(10, 18);
		String cmdCode = message.substring(18, 26);
		String clusterCode = message.substring(26, 30);
		String clientId = message.substring(30, 46);
		int port = Integer.parseInt(message.substring(46, 48));
		int dataLen = Integer.parseInt(message.substring(48, 50));
		String dataString = message.substring(50, message.length() - 8);
		String messageEnd = message.substring(message.length() - 8);
		return new CLinkMessage(messageHeader, seqNo, cmdCode, clusterCode, clientId, port, dataLen, dataString,
				messageEnd);
	}
	public static void main(String[] args) {
		String message ="2013012820X99999991120150150006ea2a7d1bd67459820181001:10.3@1002:10@1003:3.6FFFFEEEE";
		System.out.println(parseMsgStr(message));
	}
}
