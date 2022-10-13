package com.chint.iot.protocol.mqtt.verticle;

import com.chint.iot.protocol.mqtt.protocol.WsMqttProtocol;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-11-11 15:04
 */
@Slf4j
@Component
public class WsMqttServerVerticle {
	final static Logger LOGGER = LoggerFactory.getLogger(WsMqttServerVerticle.class);

	@Value("${chint.iot.telemetry.mqttServerCount}")
	private String serverCountStr;

	@Resource
	private MqttServerVerticle mqttServerVerticle;

	@PostConstruct
	public void start(){
		WsMqttProtocol wsMqttProtocol = new WsMqttProtocol();
		//启动ws mqtt
		mqttServerVerticle.startServer(wsMqttProtocol, true, serverCountStr);
	}
}


