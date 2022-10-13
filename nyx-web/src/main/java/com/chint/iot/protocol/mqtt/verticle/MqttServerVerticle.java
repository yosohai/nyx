package com.chint.iot.protocol.mqtt.verticle;

import com.chint.iot.protocol.mqtt.cache.IgniteCacheService;
import com.chint.iot.protocol.mqtt.protocol.MqttProtocol;
import com.chint.iot.protocol.mqtt.service.AutoUserDcsService;
import com.chint.iot.protocol.mqtt.service.MqttConnectService;
import com.chint.iot.protocol.mqtt.service.MqttMessageService;
import com.chint.iot.protocol.mqtt.service.MqttPublishService;
import com.chint.iot.protocol.mqtt.service.MqttSubscribeService;
import io.vertx.core.Vertx;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.spi.cluster.service.DeviceDcsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ignite.Ignite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-11-11 15:04
 */
@Slf4j
@Component
public class MqttServerVerticle {
	final static Logger LOGGER = LoggerFactory.getLogger(MqttServerVerticle.class);

	@Resource
	private Vertx vertx;

	@Resource
	private Ignite ignite;

	@Resource
	private MqttMessageService mqttTopicPaternService;

	@Resource
	private IgniteCacheService igniteCacheService;

	@Resource
	private AutoUserDcsService autoUserDcsService;

	@Value("${chint.iot.telemetry.kafka.servers}")
	private String telemetryKafkaServers;

	@Value("${chint.iot.telemetry.mqttServerCount}")
	private String serverCountStr;

	@PostConstruct
	public void start(){
		MqttProtocol mqttProtocol = new MqttProtocol();
		//启动mqtt
		startServer(mqttProtocol, false, serverCountStr);
	}

	public void startServer(MqttProtocol mqttProtocol,boolean isWebSocket,String serverCountStr) {
		try {
/*			MqttMessageService mqttTopicPaternService = new MqttMessageService();
			IgniteCacheService igniteCacheService = new IgniteCacheService(mqttProtocol.getIgnite());
			DeviceDcsService deviceDcsService = new DeviceDcsService(mqttProtocol.getIgnite());
			AutoUserDcsService autoUserDcsService = new AutoUserDcsService(mqttProtocol.getIgnite());*/
			DeviceDcsService deviceDcsService = new DeviceDcsService(ignite);
			String mqttConfigInfo = mqttProtocol.getConfig();
			JsonObject json = new JsonObject(mqttConfigInfo);
			MqttServerOptions options = new MqttServerOptions(json);
			if (isWebSocket) {
				//处理websocket 依赖VERT-MQTT 4.0.0.ext版本
				options.setUseWebSocket(true);
				//options.setOverWebsocket(true);
				options.setPort(2883);
			}

			//初始化kafka
			initKafka(mqttProtocol, vertx);

			LOGGER.info("Runtime.getRuntime().availableProcessors()="+Runtime.getRuntime().availableProcessors());
			LOGGER.info("CpuCoreSensor.availableProcessors()="+CpuCoreSensor.availableProcessors());
			int serverCount = Integer.parseInt(serverCountStr);
			for (int i = 0; i < serverCount; i++) {
				String serverName = "server-"+i;
				createMqttServer(mqttProtocol, mqttTopicPaternService, igniteCacheService, options, vertx, serverName, deviceDcsService, autoUserDcsService);
			}
			LOGGER.info("Done:" + serverCount + " MQTT servers are listening on port [1883]");
		} catch (Exception e) {
			LOGGER.error("Happened to an error while start the server and the server will quit, error message is {}",
					ExceptionUtils.getStackTrace(e));
			System.exit(1);
		}

	}

	private static void createMqttServer(MqttProtocol mqttProtocol, MqttMessageService mqttTopicPaternService,
			IgniteCacheService igniteCacheService, MqttServerOptions options, Vertx vertx, String serverName,
			DeviceDcsService deviceDcsService, AutoUserDcsService autoUserDcsService) {
		MqttServer mqttServer = MqttServer.create(vertx, options);

		//注册事件监听
		mqttServer.endpointHandler(endpoint -> {

			// 客户端连接处理
			boolean keepGo = MqttConnectService
					.connecthandle(endpoint, mqttProtocol, mqttTopicPaternService,igniteCacheService, serverName, deviceDcsService, autoUserDcsService);
			if(!keepGo) {
				return;
			}

			// 客户端断开连接处理
			MqttConnectService.disConnecthandle(endpoint, mqttProtocol, mqttTopicPaternService);

			// 客户端订阅主题处理
			MqttSubscribeService.subscribeHandle(endpoint, mqttProtocol, mqttTopicPaternService);

			// 客户端取消订阅主题处理
			MqttSubscribeService.unSubscribeHandle(endpoint, mqttProtocol, mqttTopicPaternService);

			// 客户端上送消息处理
			MqttPublishService.publishHandle(endpoint, mqttProtocol, mqttTopicPaternService, serverName);

			// 关闭连接处理
			MqttConnectService.closeHandle(endpoint, mqttProtocol, mqttTopicPaternService);

			// 客户端保持连接处理
			MqttConnectService.pingHandle(endpoint, mqttProtocol, mqttTopicPaternService);
		}).listen(options.getPort(), options.getHost(), ar -> {
			if (ar.succeeded()) {
				LOGGER.info("MQTT server[" + serverName + "] is listening on port [{}]", mqttServer.actualPort());
			} else {
				LOGGER.error("Error on starting the server,reason is {}", ar.cause().getMessage());
			}
		});
	}

	/**
	 * kafka配置
	 * @param mqttProtocol
	 * @param vertx
	 */
	private void initKafka(MqttProtocol mqttProtocol, Vertx vertx) {
		Map<String, String> config = new HashMap<>();
		config.put("bootstrap.servers", telemetryKafkaServers);

		config.put("acks", "0");
		//128KB
		config.put("batch.size", "131072");
		config.put("linger.ms", "100");
		//64M
		config.put("buffer.memory", "67108864");
		config.put("retries", "0");
		//10M
		config.put("max.request.size", "10485760");

		config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		KafkaProducer<String, String> kafkaProducer = KafkaProducer.create(vertx, config);
		mqttProtocol.setKafkaProducer(kafkaProducer);
	}
}


