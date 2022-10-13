package com.chint.dama.dao.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-12-01 13:51
 */
@Data
@Accessors(chain = true)
@TableName("t_document")
public class DocumentPO implements Serializable {
	@TableField(value = "fileid")
	private String fileId;
	@TableField(value = "filename")
	private String fileName;
	@TableField(value = "size")
	private String size;
	@TableField(value = "path")
	private String path;
	@TableField(value = "contenttype")
	private String contentType;
	@TableField(value = "content")
	private byte[] content;
	@TableField(value = "filetype")
	private String fileType;
	@TableField(value = "createtime")
	private String createTime;
	@TableField(value = "createman")
	private String createMan;
	@TableField(value = "columnid")
	private String columnId;
}
