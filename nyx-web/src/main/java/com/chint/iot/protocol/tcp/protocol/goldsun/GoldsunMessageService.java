package com.chint.iot.protocol.tcp.protocol.goldsun;

import com.alibaba.fastjson.JSON;
import com.chint.iot.protocol.tcp.bo.telemetry.DeviceInfoDetail;
import com.chint.iot.protocol.tcp.bo.telemetry.UploadTelemetryMsg;
import com.chint.iot.protocol.tcp.common.Constant;
import com.chint.iot.protocol.tcp.service.DeviceDataService;
import com.chint.protocol.goldsun.struct.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理金太阳协议
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-07-27 13:11
 */
@Slf4j
@Service
public class GoldsunMessageService {

	@Resource
	private DeviceDataService deviceDataService;

	/**
	 * 连接有效判断
	 * TODO:需要对sn和密码进行校验，依赖于平台的修改
	 * @return
	 */
	public boolean isValidConnection(){
		return true;
	}

	/**
	 *  deviceId:电站单元code+设备类型code+设备公共地址
	 * 设备上报设备信息:/Edge/{DevTyp}/{DevSN}/Info
	 * 设备上报实时数据:/Edge/{DevTyp}/{DevSN}/RTG
	 */
	public void processGoldSunMessage(Object dataObject, String unitSn, String endpointIp){
		if (dataObject instanceof StructDeviceInfo) {
			/**
			 * 设备消息
			 * "data":{
			 *         "details":[
			 *             {
			 *                 "addr":83,
			 *                 "manufacture":"",
			 *                 "name":"HL1006A06",
			 *                 "type":2
			 *             }
			 *         ],
			 *         "id":31,
			 *         "num":1
			 *     }
			 */
			List<StructDeviceInfo.StructDeviceInfoDetail> deviceList = ((StructDeviceInfo) dataObject).getDetails();
			if(!CollectionUtils.isEmpty(deviceList)){
				for (StructDeviceInfo.StructDeviceInfoDetail structDeviceInfoDetail : deviceList) {
					/**
					 * 处理设备ID，topic
					 */
					int deviceAddr = structDeviceInfoDetail.getAddr();
					int deviceType = structDeviceInfoDetail.getType();
					String deviceTypeCode =GoldsunDeviceTypeEnum.getGoldsunDeviceTypeByCode(deviceType);
					if (StringUtils.isEmpty(deviceTypeCode)) {
						log.warn("不支持的设备数据unitSn={},deviceAddr={},deviceType={},structDeviceInfoDetail={}",unitSn,deviceAddr,deviceType,JSON.toJSONString(structDeviceInfoDetail));
						continue;
					}
					//deviceId:电站单元code+设备类型code+设备公共地址
					String clientId = unitSn + deviceTypeCode + deviceAddr;
					String topic = "/Edge/" + deviceTypeCode + "/" + clientId + "/Info";
					//设备信息放入缓存
					//log.info("addDeviceInfo.unitSn={},deviceAddr={},deviceType={}", unitSn, deviceAddr, deviceType);
					GoldSunDeviceCache.addDeviceInfo(unitSn,deviceAddr,deviceType);
					/**
					 * 构建设备信息
					 */
					DeviceInfoDetail deviceInfoDetail = new DeviceInfoDetail();
					deviceInfoDetail.setDeviceId(clientId);
					deviceInfoDetail.setDeviceTypeCode(deviceTypeCode);
					deviceInfoDetail.setDeviceName(structDeviceInfoDetail.getName());
					deviceInfoDetail.setDeviceManufacture(structDeviceInfoDetail.getManufacture());

					/**
					 * 发送给下游
					 */
					deviceDataService.uploadMsgData(clientId, topic, Constant.GOLDSUN_SERVER_TYPE, JSON.toJSONString(deviceInfoDetail),
							Constant.GOLDSUN_SERVER_TYPE, endpointIp, unitSn);
				}
			}

		} else if (dataObject instanceof StructRealtimeData) {
			/**
			 * 实时数据消息
			 * {
			 *     "crc16":19963,
			 *     "data":{
			 *         "addr":52,//设备地址
			 *         "datas":[
			 *             {
			 *                 "addr":318,//测点地址
			 *                 "id":1,
			 *                 "qcode":1,
			 *                 "value":350
			 *             },
			 *             {
			 *                 "addr":356,
			 *                 "id":2,
			 *                 "qcode":1,
			 *                 "timeObjB":{
			 *                     "day":27,
			 *                     "hour":14,
			 *                     "min":29,
			 *                     "mon":7,
			 *                     "secHi":0,
			 *                     "secLo":1,
			 *                     "year":21
			 *                 },
			 *                 "value":1
			 *             }
			 *         ],
			 *         "id":41,//类别标识
			 *         "objNb":19,//数据信息体个数
			 *         "stat":0,
			 *         "tmObjA":{
			 *             "hour":14,
			 *             "mday":27,
			 *             "min":29,
			 *             "mon":7,
			 *             "sec":1,
			 *             "year":21
			 *         }
			 *     },
			 *     "len":0,
			 *     "major":"1.0",
			 *     "minor":"0.0",
			 *     "pNo":1,
			 *     "type":4 //实时数据
			 * }
			 */
			//公共地址(设备地址)
			int deviceAddr = ((StructRealtimeData) dataObject).getAddr();
			List<Object> dataList = ((StructRealtimeData) dataObject).getDatas();
			//设备上传的数据时间TODO
			StructTimeObjA deviceInfoTs = ((StructRealtimeData) dataObject).getTmObjA();

			if (!CollectionUtils.isEmpty(dataList)) {

				/**
				 * 1.处理设备ID，topic
				 * deviceId:电站单元code+设备类型code+设备公共地址
				 */
				Integer deviceType = GoldSunDeviceCache.getDeviceType(unitSn, deviceAddr);
				String deviceTypeCode = GoldsunDeviceTypeEnum.getGoldsunDeviceTypeByCode(deviceType);
				if (StringUtils.isEmpty(deviceTypeCode)) {
					log.warn("不支持的设备实时数据unitSn={},deviceAddr={},deviceType={},StructRealtimeData={}",unitSn,deviceAddr,deviceType,JSON.toJSONString(dataObject));
					return;
				}
				String clientId = unitSn + deviceTypeCode + deviceAddr;
				String topic = "/Edge/" + deviceTypeCode + "/" + clientId + "/RTG";
				//log.info("实时数据下发deviceType={},deviceTypeCode={},clientId={},topic={}", deviceType, deviceTypeCode,clientId, topic);

				/**
				 * 2.处理设备的每个测点信息
				 */
				Map<String,Object> pointDataMap = new HashMap<>();
				for (Object objectData : dataList) {
					//信息单元地址(测点)地址
					int pointAddr;
					//测点值
					int value;
					//数据质量1有效/0无效
					int qcode;
					if (objectData instanceof StructDataYcYm) {
						pointAddr = ((StructDataYcYm) objectData).getAddr();
						value = ((StructDataYcYm) objectData).getValue();
						qcode = ((StructDataYcYm) objectData).getQcode();

					} else if (objectData instanceof StructDataYx) {
						pointAddr = ((StructDataYx) objectData).getAddr();
						value = ((StructDataYx) objectData).getValue();
						qcode = ((StructDataYx) objectData).getQcode();

					} else {
						log.info("未处理的实时数据:{}", JSON.toJSONString(dataObject));
						continue;
					}
					//先把所有质量数据都下发下去
					pointDataMap.put("P" + pointAddr, value);
				}

				/**
				 * 3.构建实时数据信息
				 */
				UploadTelemetryMsg uploadTelemetryMsg = new UploadTelemetryMsg();
				uploadTelemetryMsg.setUploadTimeStamp(System.currentTimeMillis());
				uploadTelemetryMsg.setTS(System.currentTimeMillis());
				uploadTelemetryMsg.setClientId(clientId);
				uploadTelemetryMsg.setData(pointDataMap);
				uploadTelemetryMsg.setTopic(topic);

				/**
				 *4.数据下发处理
				 */
				deviceDataService.uploadMsgData(clientId, topic, Constant.GOLDSUN_SERVER_TYPE, JSON.toJSONString(uploadTelemetryMsg),
						Constant.GOLDSUN_SERVER_TYPE, endpointIp, unitSn);
			}

		} else {
			log.warn("无需处理的消息:{}", JSON.toJSONString(dataObject));
			return;
		}
	}
}
