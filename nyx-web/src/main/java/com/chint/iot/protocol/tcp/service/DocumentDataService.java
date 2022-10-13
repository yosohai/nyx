package com.chint.iot.protocol.tcp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chint.dama.base.mapstruct.dto.FileUploadDTO;
import com.chint.dama.service.FileUploadService;
import com.chint.iot.protocol.http.protocol.HttpProtocol;
import com.chint.iot.protocol.tcp.bo.document.DocumentBO;
import com.chint.iot.protocol.tcp.bo.document.UploadDocumentMsg;
import com.chint.iot.protocol.tcp.bo.telemetry.IotMessage;
import com.chint.iot.protocol.tcp.common.BaseResponse;
import com.chint.iot.protocol.tcp.common.Constant;
import com.chint.iot.protocol.tcp.common.ResultCode;
import com.chint.iot.protocol.tcp.utils.CommonUtils;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-04-06 18:33
 */
@Service
@Slf4j
public class DocumentDataService {

	@Resource
	private HttpProtocol httpProtocol;

	@Resource
	private Vertx vertx;

	@Resource
	private FileUploadService fileUploadService;

	/**
	 * 文档数据透传
	 * @param dcoDataStr
	 * @return
	 */
	public BaseResponse uploadDocumentData(String dcoDataStr) {
		try {
			log.info("uploadDocumentData.dcoDataStr={}",dcoDataStr);
			//1.解析上传的数据
			JSONObject docDataJson = JSONObject.parseObject(dcoDataStr);
			UploadDocumentMsg uploadDocumentMsg = JSON
					.parseObject(docDataJson.toJSONString(), UploadDocumentMsg.class);

			//2.组装iotMessage 发送
			sendMessageToIgnite(uploadDocumentMsg);

			return BaseResponse.create(ResultCode.SUCCESS,"doc消息全部发送成功");
		} catch (Throwable t) {
			log.error("uploadDocumentData error.", t);
			return BaseResponse.create(ResultCode.FAILED,"doc消息上传失败");
		}
	}

	/**
	 * 二进制流上传
	 * @param rc
	 * @return
	 */
	public BaseResponse uploadDocumentDataWithBinary(RoutingContext rc){
		Set<FileUpload> uploads = rc.fileUploads();
		String columnId = rc.request().getParam("columnId");
		if(StringUtils.isEmpty(columnId)){
			columnId= Constant.COLUMN_DEFAULT_CODE;
		}
		if (!CollectionUtils.isEmpty(uploads)) {
			handleUploads(uploads, columnId);
		}
		return BaseResponse.create(ResultCode.SUCCESS, "文件上传成功");
	}

	private void handleUploads(Set<FileUpload> uploads, String columnId) {
		uploads.forEach(fileUpload->{
			String filePath = fileUpload.uploadedFileName();
			String fileName = fileUpload.fileName();
			log.info("start to read file.filePath={}", filePath);
			vertx.fileSystem().readFile(filePath, result -> {
				if (result.succeeded()) {
					//1.根据filepath异步读取文件 然后分别发送到下游
					Buffer buffer = result.result();
					byte[] fileBytes = buffer.getBytes();
					String fileType = "";
					try {
						fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
					} catch (Exception e) {
						log.error("fileName:" + fileName + " invalid.");
					}
					log.info("upload filename={},to column={}", fileUpload.fileName(), columnId);
					//2.调用fileUploadService
					FileUploadDTO fileUploadDTO = new FileUploadDTO();
					try {
						fileUploadDTO.setFileName(fileUpload.fileName());
						fileUploadDTO.setSize(fileUpload.size()+"");
						fileUploadDTO.setContentType(fileUpload.contentType());
						fileUploadDTO.setContent(fileBytes);
						fileUploadDTO.setFileType(fileType);
						fileUploadDTO.setColumnId(columnId);
						FileUploadDTO fileUploadDTORes = fileUploadService.saveFile(fileUploadDTO);
					} catch (Exception e) {
						log.error("fileUploadService.saveFile error", e);
					}finally {
						//3.删除存在本地的文件
						vertx.fileSystem().delete(filePath, r -> {
						});
					}
				} else {
					log.error("文件读取失败filePath={},result.cause()={}", filePath, result.cause());
				}
			});
		});
	}

	/**
	 * 发送数据到ignite
	 * @param uploadDocumentMsg
	 */
	private void sendMessageToIgnite(UploadDocumentMsg uploadDocumentMsg) {
		//2.组装iotMessage
		IotMessage iotMessage = new IotMessage();
		iotMessage.setEventType(Constant.HTTP_EVENT_FILE_TYPE);
		iotMessage.setClientId(uploadDocumentMsg.getClientId());
		iotMessage.setDeviceId(uploadDocumentMsg.getClientId());
		iotMessage.setMsgBody(JSON.toJSONString(uploadDocumentMsg));
		iotMessage.setMsgHeader(null);
		iotMessage.setMsgId(Constant.HTTP_MSG_ID);
		iotMessage.setMsgType(Constant.HTTP_MSG_FILE_TYPE);
		iotMessage.setSeqNo(UUID.randomUUID().toString());
		iotMessage.setTimeStamp(System.currentTimeMillis());
		//3.调用SDK发送数据
		String iotMessageStr = JSON.toJSONString(iotMessage);
		boolean isSuccessSend = httpProtocol.sendMessage(iotMessageStr);
		log.info("httpProtocol.send doc Message.isSuccessSend:{}", isSuccessSend);
		if (isSuccessSend) {
			//log.info("Successed.Send doc message:{}", iotMessageStr);
		} else {
			log.error("Failed.Send doc message:{}", iotMessageStr);
		}
	}
}
