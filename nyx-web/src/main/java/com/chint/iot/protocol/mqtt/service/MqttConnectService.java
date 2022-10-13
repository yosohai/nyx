package com.chint.iot.protocol.mqtt.service;

import com.alibaba.fastjson.JSONObject;
import com.chint.iot.protocol.mqtt.cache.IgniteCacheService;
import com.chint.iot.protocol.mqtt.constant.CacheConstant;
import com.chint.iot.protocol.mqtt.handler.MqttMessageHandler;
import com.chint.iot.protocol.mqtt.protocol.MqttProtocol;
import com.chint.iot.protocol.mqtt.util.ChintSHA;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.spi.cluster.service.DeviceDcsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MqttConnectService {
	final static Logger LOGGER = LoggerFactory.getLogger(MqttConnectService.class);

	public static boolean connecthandle(MqttEndpoint endpoint, MqttProtocol mqttProtocol,
			MqttMessageService mqttTopicPaternService, IgniteCacheService igniteCacheService, String serverName,
			DeviceDcsService deviceDcsService, AutoUserDcsService autoUserDcsService) {
		boolean bool = false;
		try {
			String clientId = endpoint.clientIdentifier();
			long receiveTs = System.currentTimeMillis();
			//如果连接已经存在，断开之前的一次连接
			MqttEndpoint existedMqttEndpoint = mqttProtocol
					.getConnection(CacheConstant.MQTT_ENDPOINT_PREFIX + clientId);
			if (existedMqttEndpoint != null) {
				LOGGER.info("start to close existed connection");
				existedMqttEndpoint.close();
				LOGGER.info("end to close existed done");
			}
			boolean isCleanSession = endpoint.isCleanSession();
			// mqtt连接安全信息较验
			/**
			 * 1. 设备第一次注册
			 * 1.1 用户名在系统中配置过
			 *   A>校验用户名、密码
			 *   B>自动注册采用系统配置的用户名、密码
			 * 1.2 用户名在系统中没有配置
			 * 不允许连接
			 *
			 * 2. 设备已经注册
			 * 2.1校验设备用户、密码(原有逻辑)
			 * mqttProtocol.login(clientId, userName, password);
			 *
			 */
			String userName = null;
			String password = null;
/*			boolean isFirstConnect = firstTimeConnect(clientId, deviceDcsService);
			if (endpoint.auth() != null) {
				userName = endpoint.auth().getUsername();
				password = endpoint.auth().getPassword();
				LOGGER.info("clientId=" + clientId + ",isFirstConnect=" + isFirstConnect + ",userName=" + userName+ ",password=" + password);
				if(isFirstConnect){
					boolean isValidSystemUser = isValidAccessUser(userName, password, autoUserDcsService);
					LOGGER.info("isValidSystemUser=" + isValidSystemUser + ",userName=" + userName + ",password=" + password);
					if (!isValidSystemUser) {
						LOGGER.info("user:[" + userName + "] is not valid in system.");
						endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
						return bool;
					}
				}else{
					//2.平台已经注册过:非第一次的话则需要根据平台来进行判断
					boolean checkRegisterDevice = loginWithEncryptPwd(clientId, userName, password,deviceDcsService);
					LOGGER.info("checkRegisterDevice=" + checkRegisterDevice + ",userName=" + userName + ",password=" + password);
					if (!checkRegisterDevice) {
						LOGGER.info("MQTT client [{}] request to connect failed,[username = [{}], password = [{}]",
								clientId, userName, password);
						endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
						//mqttTopicPaternService.eventLogHandle(endpoint, null, EventTypeEnum.CONNECT.getCode(), null,mqttProtocol,receiveTs);
						return bool;
					}
				}
			} else {
				LOGGER.info("endpoint.auth() is null...");
				endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
				return bool;
			}

			LOGGER.info("clientId=" + clientId + ",isFirstConnect[" + isFirstConnect + "]Auth successful...");*/
			bool = true;
			Boolean isWillFlag = null;
			String willTopic = null;
			Buffer willMessage = null;
			Integer willQos = null;
			Boolean isWillRetain = null;

			// 遗嘱信息处理
			if (endpoint.will() != null) {
				isWillFlag = endpoint.will().isWillFlag();
				willTopic = endpoint.will().getWillTopic();
				willMessage = endpoint.will().getWillMessage();
				willQos = endpoint.will().getWillQos();
				isWillRetain = endpoint.will().isWillRetain();

				//保存遗嘱信息
				MqttMessageHandler mqttMessageHandler = new MqttMessageHandler();
				mqttMessageHandler.storeWillMessage(clientId, endpoint.will(),igniteCacheService);
			}

			int keepAliveTimeSeconds = endpoint.keepAliveTimeSeconds();

			// accept connection from the remote client
			endpoint.accept(false);
			//LOGGER.info("start to save connection...connectionKey:"+ CacheConstant.MQTT_ENDPOINT_PREFIX + clientId);
			mqttProtocol.addConnection(CacheConstant.MQTT_ENDPOINT_PREFIX + clientId, endpoint);

			LOGGER.info(
					"MQTT client {} request to connect, clean session = {},username = {}, password = {},will flag ={} , topic ={} , msg ={} , QoS ={} , isRetain ={},keep alive timeout ={},threadName={},serverName={}",
					clientId, isCleanSession, userName, password, isWillFlag, willTopic, willMessage, willQos,
					isWillRetain, keepAliveTimeSeconds, Thread.currentThread().getName(),serverName);
			// 发送客户端连接消息给数据平台
			//mqttTopicPaternService.eventLogHandle(endpoint, null, EventTypeEnum.CONNECT.getCode(), null, mqttProtocol,receiveTs);
		} catch (Exception e) {
			LOGGER.error(
					"Happened to an error while connect to the server, the connect will be refused please check and try later, error message is {}",
					ExceptionUtils.getStackTrace(e));
			endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE);
			bool = false;
		}
		return bool;
	}

	private static boolean isValidAccessUser(String userName, String password, AutoUserDcsService autoUserDcsService) {
		JSONObject systemUserJSONObject = autoUserDcsService.get(userName);
		if (systemUserJSONObject != null) {
			LOGGER.info("isValidAccessUser.systemUserJSONObject={}",systemUserJSONObject.toJSONString());
			String systemPassword = systemUserJSONObject.getString("password");
			String encryptPwd = encrypt(userName,password);
			LOGGER.info("systemPassword={},encryptPwd={}");
			if (systemPassword != null && systemPassword.equals(encryptPwd)) {
				LOGGER.info("isValidAccessUser true...");
				return true;
			}
		}else{
			LOGGER.info("autoUserDcsService.username["+userName+"]not exist in ignite");
			return false;
		}
		return false;
	}

	public static String encrypt(String username, String password){
		return ChintSHA.SHA256(username + "|" + password);
	}

	public static boolean loginWithEncryptPwd(String clientId, String userName, String password, DeviceDcsService deviceDcsService) {
		boolean result = false;
		if (deviceDcsService != null) {
			JSONObject json = deviceDcsService.get(clientId);
			String encryptPwd = encrypt(userName, password);
			if (json != null && userName != null && password != null && userName.equals(json.getString("username"))
					&& encryptPwd.equals(json.getString("password"))) {
				result = true;
			} else {
				if (json != null) {
					System.out.println(json.toJSONString());
					LOGGER.info("loginWithEncryptPwd.json.toJSONString={}",json.toJSONString());
				} else {
					LOGGER.info("loginWithEncryptPwd.device (" + clientId + ") is null");
				}
			}
		} else {
			LOGGER.info("deviceDcsService null");
		}
		return result;
	}
	/**
	 * 是否是第一次连接
	 * @param clientId
	 * @return
	 */
	private static boolean firstTimeConnect(String clientId,DeviceDcsService deviceDcsService) {
		JSONObject json = deviceDcsService.get(clientId);
		if (json == null) {
			LOGGER.info("firstTimeConnect.clientId=" + clientId);
			return true;
		}
		return false;
	}

	public static void disConnecthandle(MqttEndpoint endpoint, MqttProtocol mqttProtocol,
			MqttMessageService mqttTopicPaternService) {
		// handling disconnect message
		endpoint.disconnectHandler(v -> {
			String clientId = endpoint.clientIdentifier();
			long receiveTs = System.currentTimeMillis();
			try {
				LOGGER.info("Received disconnect from client {}", clientId);
				// 删除所有此endpoint的缓存
				mqttProtocol.removeSuber(endpoint);
				// 发送客户端断开边接消息给数据平台
				//mqttTopicPaternService.eventLogHandle(endpoint, null, EventTypeEnum.DISCONNECT.getCode(), null,mqttProtocol,receiveTs);

				Boolean isWillFlag = null;
				String willTopic = null;
				Buffer willMessage = null;
				Integer willQos = null;
				Boolean isWillRetain = null;

				// 遗嘱信息处理
				if (endpoint.will() != null) {
					isWillFlag = endpoint.will().isWillFlag();
					willTopic = endpoint.will().getWillTopic();
					willMessage = endpoint.will().getWillMessage();
					willQos = endpoint.will().getWillQos();
					isWillRetain = endpoint.will().isWillRetain();
					//遗嘱主题为空为遗嘱信息为空均不下发
					if (StringUtils.isNotBlank(willTopic) && willMessage != null
							&& StringUtils.isNotBlank(willMessage.toString())) {
						List<MqttEndpoint> subEndPoints = mqttProtocol.getMqttEndpoints(willTopic);
						if (subEndPoints != null && subEndPoints.size() > 0) {
							for (MqttEndpoint subEndPoint : subEndPoints) {
								subEndPoint.publish(willTopic, willMessage, MqttQoS.valueOf(willQos), false,
										isWillRetain);
							}
						}
					}
				}
				LOGGER.info("disconnect successfully...");
			} catch (Exception e) {
				LOGGER.error("Happened to an error while client {} disConnect to the server, error message is {}",
						clientId, ExceptionUtils.getStackTrace(e));
			}
		});

	}

	public static void closeHandle(MqttEndpoint endpoint, MqttProtocol mqttProtocol,
			MqttMessageService mqttTopicPaternService) {
		String clientId = endpoint.clientIdentifier();
		long receiveTs = System.currentTimeMillis();
		endpoint.closeHandler(v -> {
			try {
				LOGGER.info("Connection from client {} closed", clientId);
				// 删除所有此endpoint的缓存
				mqttProtocol.removeSuber(endpoint);
				// 发送关闭连接消息给数据平台
				//mqttTopicPaternService.eventLogHandle(endpoint, null, EventTypeEnum.CLOSE.getCode(), null,mqttProtocol,receiveTs);
			} catch (Exception e) {
				LOGGER.error("Happened to an error while close connect from client {}, error message is {}", clientId,
						ExceptionUtils.getStackTrace(e));
			}finally {
				//删除连接信息
				mqttProtocol.removeConnection(CacheConstant.MQTT_ENDPOINT_PREFIX + clientId);
				LOGGER.info("remove connection successfully...");
			}
		});

	}

	public static void pingHandle(MqttEndpoint endpoint, MqttProtocol mqttProtocol,
			MqttMessageService mqttTopicPaternService) {
		String clientId = endpoint.clientIdentifier();
		endpoint.pingHandler(v -> {
			try {
				LOGGER.info("Ping received from client {}", endpoint.clientIdentifier());
			} catch (Exception e) {
				LOGGER.error("Happened to an error while ping to client {} , error message is {}", clientId,
						ExceptionUtils.getStackTrace(e));
				endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE);
				return;
			}
		});

	}

}
