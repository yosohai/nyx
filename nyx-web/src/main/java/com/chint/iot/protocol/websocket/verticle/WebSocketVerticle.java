package com.chint.iot.protocol.websocket.verticle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chint.iot.protocol.tcp.common.BaseResponse;
import com.chint.iot.protocol.tcp.common.Constant;
import com.chint.iot.protocol.tcp.common.ResultCode;
import com.chint.iot.protocol.tcp.service.DeviceDataService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author zhanglei5
 * @Description: websocket协议
 * @date 2021-12-01 14:17
 */
@Component
@Slf4j
public class WebSocketVerticle {

    public static final String CHINT_WB_PATH = "/devicedata";

    @Resource
    private Vertx vertx;

    @Resource
    private DeviceDataService deviceDataService;

    @Value("${chint.websocket.listenPort}")
    private String webSocketListenPort;

    @PostConstruct
    public void start() throws Exception {
        HttpServer httpServer = vertx.createHttpServer().webSocketHandler(serverWebSocket -> {
            String path = serverWebSocket.path();
            log.info("client connected: sessionId:{},path:{}", serverWebSocket.textHandlerID(), path);
            //System.out.println("client connected: sessionId:" + serverWebSocket.textHandlerID() + ",path:" + path);
            if (WebSocketVerticle.CHINT_WB_PATH.equals(path)) {
                handleRequest(serverWebSocket);
            } else {
                log.error("非法请求{}，拒绝连接", path);
                serverWebSocket.close();
                return;
            }
        });
        int webSocketListenPortInt = Integer.parseInt(webSocketListenPort);
        httpServer.listen(webSocketListenPortInt, server -> {
            if (server.succeeded()) {
                log.info("WebSocket启动成功,监听端口:{},path:{}", webSocketListenPortInt, WebSocketVerticle.CHINT_WB_PATH);
            } else {
                server.cause().printStackTrace();
            }
        });
    }

    /**
     * https://wiki.chint.com/pages/viewpage.action?pageId=37585936
     * {
     *     "clientId":"c332848df65b493d80a7e8c7432a3caf",
     *     "topic":"/WEBSOCKET-MSG/SHEBEILEIXING",
     *     "source":"jiangshan",
     *     "data":{
     *         "Tmp_phsA":3809.83655,
     *         "TotW":999999999,
     *         "PhW_phsA":3515.17956,
     *         "PhVar_phsB":3660.30945
     *     },
     *     "uploadTimeStamp":1619499047069
     * }
     * @param serverWebSocket
     */
    public void handleRequest(ServerWebSocket serverWebSocket) {
        log.info("有新的连接加入，{}", serverWebSocket.textHandlerID());
        serverWebSocket.writeTextMessage("server connected successfully");
        serverWebSocket.textMessageHandler(message -> {
            BaseResponse baseResponse = null;
            try {
                log.info("server收到host消息,message:{}", message);
                System.out.println("server收到host消息,message:" + message);
                JSONObject msgBodyJsonObject = JSON.parseObject(message);
                String clientId = msgBodyJsonObject.getString("clientId");
                String source = msgBodyJsonObject.getString("source");
                String topic = msgBodyJsonObject.getString("topic");
                String userName = msgBodyJsonObject.getString("userName");
                if (StringUtils.isBlank(clientId) || StringUtils.isBlank(source) || StringUtils.isBlank(topic)) {
                    baseResponse = BaseResponse.create(ResultCode.FAILED, "clientId,source,topic不能为空");
                } else {
                    String endpointIp = serverWebSocket.remoteAddress().hostAddress();
                    baseResponse = deviceDataService
                            .uploadMsgData(clientId, topic, source, message, Constant.WEBSOCKET_SERVER_TYPE, endpointIp,
                                    userName);
                }
            } catch (Exception e) {
                log.error("websocket消息上传失败", e);
                BaseResponse.create(ResultCode.FAILED, "websocket消息上传失败");
            } finally {
                serverWebSocket.writeTextMessage(JSON.toJSONString(baseResponse));
            }
        });

        serverWebSocket.pongHandler(e -> {
            log.info("pong消息，{}", e);
        });

        serverWebSocket.closeHandler(c -> {
            log.info("socket 关闭了");
        });

        serverWebSocket.endHandler(end -> {
            log.info("end 操作");
        });

        serverWebSocket.exceptionHandler(ex -> {
            ex.printStackTrace();
        });
    }
}
