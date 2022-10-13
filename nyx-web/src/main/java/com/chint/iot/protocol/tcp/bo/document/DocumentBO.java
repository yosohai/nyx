package com.chint.iot.protocol.tcp.bo.document;

import lombok.Data;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-04-06 18:35
 */
@Data
public class DocumentBO {
	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件大小byte
	 */
	private Long size;

	/**
	 * 文件内容
	 * 图片存的就是base64文本
	 */
	private String fileContent;

	/**
	 * 文件后缀
	 */
	private String fileType;

	/**
	 * 栏目ID
	 */
	private String columnId;

	/**
	 *文件内容类型
	 */
	private String contentType;

	/**
	 * 文件字节数组
	 */
	private byte[] fileContentBytes;
}
