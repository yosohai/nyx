package com.chint.iot.protocol.websocket.protocol;

import io.vertx.spi.cluster.service.IotProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * @author zhanglei5
 * @Description: websocket协议
 * @date 2021-12-01 14:17
 */
@Slf4j
public class WebSocketProtocol extends IotProtocol {
	public WebSocketProtocol() {
		super(false);
		protocolFlag = "WEBSOCKET-CHINT";
	}
}
