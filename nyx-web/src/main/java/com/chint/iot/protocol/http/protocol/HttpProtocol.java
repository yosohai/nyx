package com.chint.iot.protocol.http.protocol;

import io.vertx.spi.cluster.service.IotProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * @author zhanglei5
 * @Description: http协议
 * @date 2021-03-24 14:17
 */
@Slf4j
public class HttpProtocol extends IotProtocol {

	public HttpProtocol() {
		super();
		protocolFlag = "HTTP-CHINT";
	}

	public HttpProtocol(IgniteConfiguration config) {
		super(config);
		protocolFlag = "HTTP-CHINT";
	}
}
