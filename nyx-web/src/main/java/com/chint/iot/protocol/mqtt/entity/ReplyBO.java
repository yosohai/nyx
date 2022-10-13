package com.chint.iot.protocol.mqtt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-09-01 14:28
 *
 * 设备主动上报帧云平台答复
 * 设备主动上报帧，平台均答复，Topic 是/Cloud/{DevTyp}/{DevSN}/All/Ack
 * {
 * "Ver": "1.2.5",
 * "Method": "All/Cack",
 * "Seq": "123",
 * "TS": 1552443713,
 * "Valid": 1,
 * "Remark": "XXX"
 * }
 */
@Data
public class ReplyBO {
	@JSONField(name = "Ver")
	private String Ver;
	@JSONField(name = "Method")
	private String Method;
	@JSONField(name = "Seq")
	private String Seq;
	@JSONField(name = "TS")
	private Long TS;
	@JSONField(name = "Valid")
	private int Valid;
	@JSONField(name = "Remark")
	private String Remark;
}
