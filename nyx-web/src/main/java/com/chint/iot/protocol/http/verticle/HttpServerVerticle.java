package com.chint.iot.protocol.http.verticle;

import com.alibaba.fastjson.JSON;
import com.chint.iot.protocol.tcp.common.BaseResponse;
import com.chint.iot.protocol.tcp.common.Constant;
import com.chint.iot.protocol.tcp.common.ResultCode;
import com.chint.iot.protocol.tcp.service.DeviceDataService;
import com.chint.iot.protocol.tcp.service.DocumentDataService;
import com.chint.iot.protocol.tcp.utils.JWTUtil;
import com.sensorsdata.analytics.javasdk.util.Base64Coder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author zhanglei5
 * @Description: Verticle初始化和对应的route
 * @date 2021-03-24 9:24
 */
@Component
@Slf4j
public class HttpServerVerticle {

	@Resource
	private Vertx vertx;

	@Resource
	private DeviceDataService deviceDataService;

	@Resource
	private Environment env;

	@Value("${chint.http.listenPort}")
	private String httpListenPort;

	@Resource
	private DocumentDataService documentDataService;

	@Value("${application.version}")
	private String applicationVersion;

	/**
	 * 创建对应的route
	 * @throws Exception
	 */
	@PostConstruct
	public void start() throws Exception {
		Router router = Router.router(vertx);
		//20M 最大body 20971520
		router.post().handler(BodyHandler.create().setBodyLimit(80971520));

		//0.测试get
		router.get("/test").handler(this::getDataTest);

		//1.登录 颁发token
		router.post("/login").handler(this::handleLoginCheck);

		/**
		 * 2.设备遥测数据接口/api/device/telemetry
		 * 需要token校验
		 */
		router.post(Constant.ROOT_URL_PREFIX + "/device/telemetry").handler(this::handleTelemetryData);
		//不需要token验证
		router.post(Constant.ROOT_URL_PREFIX + "/device/message").handler(this::handleMessage);
		//不需要token验证: 通用消息处理
		router.post(Constant.ROOT_URL_PREFIX + "/device/msg/:clientId/:source/:topic").handler(this::handleMsg);

		/**
		 * 3.文档上传 base64格式
		 */
		router.post(Constant.ROOT_URL_PREFIX + "/fileUpload").handler(this::handleFileUpload);

		/**
		 * 4.文档上传 二进制格式
		 */
		router.post(Constant.ROOT_URL_PREFIX + "/uploads").handler(this::handleUploads);

		/**
		 * 5.RTMP 验证
		 */
		router.get("/rtmp/onpublish").handler(this::handelRtmp);
		router.get("/rtmp/onupdate").handler(this::handelRtmp);
		//router.get("/auth/publish").handler(this::handelRtmpPublishAuth);
		//router.get("/auth/publishDone").handler(this::handelRtmpPublishAuth);

		//创建httpServer并监听服务端口
		log.info("httpListenPort={}", httpListenPort);
		int chintHttpPort = Integer.parseInt(httpListenPort);
		log.info("chint http listen port :{}", chintHttpPort);
		vertx.createHttpServer().requestHandler(router).listen(chintHttpPort);
	}

	/**
	 * 权限校验
	 * @param rc
	 */
	private void handleLoginCheck(RoutingContext rc){
		JsonObject reqJson = rc.getBodyAsJson();
		System.out.println("login.reqJson=" + JSON.toJSONString(reqJson));
		String clientId = reqJson.getString("clientId");
		String username = reqJson.getString("username");
		String password = reqJson.getString("password");
		System.out.println("username=" + username + ",password=" + password);
		log.info("username={},password={}", username, password);
		BaseResponse baseResponse = deviceDataService.loginCheck(clientId,username, password);
		rc.response()
				.putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
				.end(JSON.toJSONString(baseResponse));
	}

	/**
	 * 处理设备遥测数据
	 * @param rc
	 */
	private void handleTelemetryData(RoutingContext rc) {
		String reqToken = rc.request().getHeader("Authorization");
		String clientId = rc.getBodyAsJson().getString("clientId");
		String endpointIp = rc.request().remoteAddress().hostAddress();
		if (StringUtils.isEmpty(clientId)) {
			rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
					.end(JSON.toJSONString(BaseResponse.create(ResultCode.FAILED, "clientId不能为空")));
		}else{
			if (!StringUtils.isEmpty(reqToken)
					&& JWTUtil.verifyToken(reqToken, clientId)) {
				BaseResponse baseResponse = deviceDataService.uploadTelemetryData(rc.getBodyAsString(), endpointIp);
				rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
						.end(JSON.toJSONString(baseResponse));
			} else {
				log.info("token校验失败reqToken={}", reqToken);
				rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
						.end(JSON.toJSONString(BaseResponse.create(ResultCode.FAILED, "token校验失败")));
			}
		}
	}

	private void handleMessage(RoutingContext rc) {
		String clientId = rc.getBodyAsJson().getString("clientId");
		String endpointIp = rc.request().remoteAddress().hostAddress();
		if (StringUtils.isEmpty(clientId)) {
			rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
					.end(JSON.toJSONString(BaseResponse.create(ResultCode.FAILED, "clientId不能为空")));
		} else {
			BaseResponse baseResponse = deviceDataService.uploadTelemetryData(rc.getBodyAsString(), endpointIp);
			rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
					.end(JSON.toJSONString(baseResponse));
		}
	}

	private void handleMsg(RoutingContext rc) {
		String clientId = rc.pathParam("clientId");
		String source = rc.pathParam("source");
		String topic = rc.pathParam("topic");
		String endpointIp = rc.request().remoteAddress().hostAddress();

		log.info("clientId=" + clientId + ",source=" + source + ",topic=" + topic);
		if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(source) || StringUtils.isEmpty(topic)) {
			rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
					.end(JSON.toJSONString(BaseResponse.create(ResultCode.FAILED, "clientId,source,topic不能为空")));
		} else {
			String requestDetail = rc.getBodyAsString();
			if(topic.contains("SHENCE")){
				log.info("SHENCE数据:"+requestDetail);
				requestDetail = requestDetail.substring("data=".length(), requestDetail.indexOf("&ext="));
				requestDetail = Base64Coder.decodeString(requestDetail
						.replace("%2B","+")
						.replace("%2F","/")
						.replace("%3F","?")
						.replace("%25","%")
						.replace("%23","#")
						.replace("%3D","=")
				);
			}
			BaseResponse baseResponse = deviceDataService.uploadMsgData(clientId, topic, source, requestDetail,"HTTP", endpointIp, "shence");
			rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
					.end(JSON.toJSONString(baseResponse));
		}
	}

	/**
	 * 处理文件上传 base64 JSON方式
	 * {
	 *     "clientId":"zhangleiTest0000000000001",
	 *     "data":[
	 *         {
	 *             "fileContent":"base64xxx"
	 *             "fileName":"PIKAQU.jpg",
	 *             "fileType":"jpg",
	 *             "columnId":"API",
	 *             "size":1000
	 *         },
	 *         {
	 *             "fileContent":"base64xxx"
	 *             "fileName":"hahaha.jpg",
	 *             "fileType":"jpg",
	 *             "columnId":"API",
	 *             "size":1000
	 *         }
	 *     ]
	 * }
	 * @param rc
	 */
	private void handleFileUpload(RoutingContext rc){
		String reqToken = rc.request().getHeader("Authorization");
		String clientId = rc.getBodyAsJson().getString("clientId");
		if (StringUtils.isEmpty(clientId)) {
			rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
					.end(JSON.toJSONString(BaseResponse.create(ResultCode.FAILED, "clientId不能为空")));
		}else{
			if (!StringUtils.isEmpty(reqToken)
					&& JWTUtil.verifyToken(reqToken, clientId)) {
				String bodyString = rc.getBodyAsString();
				//log.info("handleFileUpload.bodyString={}", bodyString);
				BaseResponse baseResponse = documentDataService.uploadDocumentData(bodyString);
				rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
						.end(JSON.toJSONString(baseResponse));
			} else {
				log.info("token校验失败reqToken={}", reqToken);
				rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
						.end(JSON.toJSONString(BaseResponse.create(ResultCode.FAILED, "token校验失败")));
			}
		}
	}

	private void getDataTest(RoutingContext routingContext) {
		String resp = "test==>application.version=" + applicationVersion;
		routingContext.response()
				.putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
				.end(JSON.toJSONString(resp));
	}

	/**
	 * form-data方式
	 * 文件通过 二进制流 形式上传
	 * @param rc
	 */
	private void handleUploads(RoutingContext rc){
		BaseResponse baseResponse = documentDataService.uploadDocumentDataWithBinary(rc);
		rc.response().putHeader(HttpHeaders.CONTENT_TYPE, Constant.ARG_MSG_FORMAT_JSON)
				.end(JSON.toJSONString(baseResponse));
	}

	/**
	 * 处理RTMP 推流验证
	 * @param rc
	 */
	private void handelRtmpPublishAuth(RoutingContext rc) {
		int statusCode = deviceDataService.processRtmpStream(rc);
		rc.response().setStatusCode(statusCode);
		rc.response().end("auth done");
	}

	/**
	 * TODO:
	 * 处理RTMP 推流验证
	 * @param rc
	 */
	private void handelRtmp(RoutingContext rc) {
		int statusCode = deviceDataService.processRtmp(rc);
		rc.response().setStatusCode(statusCode);
		rc.response().end("process rtmp done");
	}
}
