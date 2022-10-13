package com.chint.dama.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chint.dama.base.mapstruct.dto.FileUploadDTO;
import com.chint.dama.dao.po.DocumentPO;
import com.chint.dama.base.util.DateUtils;
import com.chint.dama.dao.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author zhanglei5
 * @Description: 文件存储服务
 * @date 2021-12-01 14:01
 */
@DS("linkerdb")
@Service
@Slf4j
public class FileUploadService {

	@Resource
	MinIOService minIOService;

	@Resource
	private DocumentMapper documentMapper;

	/**
	 * 1.存MINIO获取PATH
	 * 2.把对应PATH以及文件信息存入t_document
	 * @param fileUploadDTO
	 * @return
	 * @throws Exception
	 */
	public FileUploadDTO saveFile(FileUploadDTO fileUploadDTO) throws Exception {
		String fileId = UUID.randomUUID().toString().replace("-", "");
		String path = "";
		DocumentPO doc = new DocumentPO();
		BeanUtils.copyProperties(fileUploadDTO,doc);
		if (minIOService.isEnable()) {
			path = minIOService.upload(fileUploadDTO.getContent(), fileUploadDTO.getFileName(), fileUploadDTO.getColumnId());
			doc.setContent(null);
		}
		doc.setFileId(fileId);
		doc.setPath(path);
		doc.setCreateTime(DateUtils.getDateTime());

		/**
		 * 插入到t_document
		 */
		try {
			documentMapper.insert(doc);
		} catch (Exception e) {
			log.error("insert to document table error, but file has uploaded to MINIO, please check..doc=[" + JSON
					.toJSONString(doc) + "]", e);
		}

		/**
		 * 返回文件ID和名称信息
		 */
		FileUploadDTO fileUploadRes = new FileUploadDTO();
		fileUploadRes.setFileId(fileId);
		fileUploadRes.setFileName(fileUploadDTO.getFileName());

		return fileUploadRes;
	}


}
