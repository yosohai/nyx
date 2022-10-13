package com.chint.iot.protocol.mqtt.handler;

import com.chint.iot.protocol.mqtt.enums.MessageTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglei5
 * @Description: 主题管理
 * @date 2021-03-18 14:28
 * 订阅者与发布者之间通过主题路由消息进行通信，订阅者可以订阅含通配符主题，但发布者不允许向含通配符主题发布消息
 * 主题(Topic)通过'/'分割层级，支持'+', '#'通配符:
 * +:代表本级所有类目
 *   例如，Topic：/YourProductKey/+/update，可以代表/YourProductKey/device1/update和/YourProductKey/device2/update
 * #:这个通配符必须出现在 Topic 的最后一个类目，代表本级及下级所有类目。
 *   例如:Topic：/YourProductKey/device1/#，可以代表/YourProductKey/device1/update和YourProductKey/device1/update/error
 */
public class MqttTopicHandler {

	private static final List<String> device2CloudTopicList = new ArrayList();
	static {
		//设备上报设备信息
		device2CloudTopicList.add("/Info");
		//设备上报设备工况
		device2CloudTopicList.add("/Status");
		//设备上报实时数据
		device2CloudTopicList.add("/RTG");
		//设备越死区上报数据
		device2CloudTopicList.add("/RTD");
		//设备上报实时报警数据
		device2CloudTopicList.add("/Alarm");
		//设备上报实时消息数据
		device2CloudTopicList.add("/Message");
		//设备断点续传自动上报
		device2CloudTopicList.add("/History");
		//设备响应校时并上报时间
		device2CloudTopicList.add("/Timing/Cack");
		//设备响应设置参数指令
		device2CloudTopicList.add("/CmdSet/Cack");
		//设备响应档案指令
		device2CloudTopicList.add("/Archive/Cack");
		//设备响应透传指令
		device2CloudTopicList.add("/Trans/Cack");
		//设备基本信息设置(用户名密码)
		device2CloudTopicList.add("/Setting");
	}

	/**
	 * 根据消息类型校验TopicFilter是否合法
	 * 订阅:
	 * 1.校验层级分隔符，不允许以/符号结尾
	 * 2.+符号需要单独占据一个层级
	 * 3.校验多级通配符，#符号要么单独使用，要么独自存在于最后一个层级
	 * @param topicFilter
	 * @param messageTypeCode
	 * @return
	 */
	public static boolean isValidTopicFilter(String topicFilter, String messageTypeCode) {

		if(StringUtils.isBlank(topicFilter) || StringUtils.isBlank(messageTypeCode)){
			return false;
		}

		//分messageType进行处理
		if (MessageTypeEnum.PUBLISH.getCode().equals(messageTypeCode)) {
			/**
			 * 发布主题校验
			 * 不允许以/符号结尾
			 * 其他的暂时不校验
			 */
            return !StringUtils.endsWithIgnoreCase(topicFilter, "/") && isStandardTopic(topicFilter);

		} else if (MessageTypeEnum.SUBSCRIBE.getCode().equals(messageTypeCode)) {
			//校验层级分隔符，不允许以/符号结尾
			if (StringUtils.endsWithIgnoreCase(topicFilter, "/")) {
				return false;
			}
			//+符号需要单独占据一个层级
			if (topicFilter.contains("+")) {
				String[] splits = topicFilter.split("/");
				for (String str : splits) {
					if (str.contains("+") && str.length() > 1) {
						return false;
					}
				}
			}
			//校验多级通配符，#符号要么单独使用，要么独自存在于最后一个层级
			if (topicFilter.contains("#")) {
				if (StringUtils.countMatches(topicFilter, "#") > 1) {
					return false;
				}
				return StringUtils.endsWithIgnoreCase(topicFilter, "#");
			}
		}
		return true;
	}

	/**
	 * topicFilter是否能匹配上topic， mqtt标准校验
	 * @param topic
	 * @param topicFilter
	 * @return
	 */
	public static boolean matchTopic(String topic, String topicFilter) {

		if (StringUtils.isBlank(topic) || StringUtils.isBlank(topicFilter)) {
			return false;
		}

		if (topic.contains("+") || topic.contains("#")) {
			String[] topicSplits = topic.split("/");
			String[] filterSplits = topicFilter.split("/");

			if (!topic.contains("#") && topicSplits.length < filterSplits.length) {
				return false;
			}
			String level;
			for (int i = 0; i < topicSplits.length; i++) {
				level = topicSplits[i];
				if (!level.equals(filterSplits[i]) && !level.equals("+") && !level.equals("#")) {
					return false;
				}
			}
		} else {
			return topic.equals(topicFilter);
		}
		return true;
	}

	/**
	 * topicFilter是否合法 系统业务校验
	 * @param topicFilter
	 * @return
	 */
	private static boolean isStandardTopic(String topicFilter) {
		boolean result = true;
		///Edge 开头
		//if (topicFilter == null || !topicFilter.startsWith("/Edge/")) {
		if (topicFilter == null) {
			return false;
		}

		/*for (String device2CloudTopic : device2CloudTopicList) {
			if (topicFilter.endsWith(device2CloudTopic)) {
				result = true;
				break;
			}
			result = false;
		}*/
		return result;
	}

	public static void main(String[] args) {
		//String topic = "/YourProductKey/device1/#";
		//String topicFilter = "/YourProductKey/device1/update/error";
		//System.out.println(matchTopic(topic, topicFilter));
		System.out.println(isStandardTopic("/Edge/test/test/RTG"));
		System.out.println(isStandardTopic("/chint/iot/gateway/register"));
		System.out.println(isStandardTopic("/chint/iot/breaker/register"));
		System.out.println(isStandardTopic("/chint/iot/breaker/property"));
		System.out.println(isStandardTopic("/chint/iot/breaker/status"));
		System.out.println(isStandardTopic("/chint/iot/breaker/event"));
		System.out.println(isStandardTopic("/chint/iot/breaker/power"));

	}
}
