package com.chint.iot.protocol.tcp.bo.document;

import lombok.Data;

import java.util.List;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-04-07 9:09
 */
@Data
public class UploadDocumentMsg {
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
	private List<DocumentBO> data;
}
