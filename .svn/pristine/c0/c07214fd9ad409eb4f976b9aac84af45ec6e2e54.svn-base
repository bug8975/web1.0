package com.monitor.action.monitordata;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.core.constant.Globals;
import com.monitor.action.Utils;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Logger;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.MonitorDataQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class DataUpdateAction {
	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	private IMonitorDataService monitorDataService;

	@Autowired
	private IMonitorDataViewService dataViewService;

	//    @SecurityMapping(title = "数据查看", value = "/monitordata/monitordatalist.htm*", rtype = "monitordata", rname = "监测数据", rcode = "monitordata", rgroup = "监测数据")
	@RequestMapping({ "/monitordata/monitordataupdatelist.htm" })
	public ModelAndView monitorDataList(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String sensorTypeName, String monitorlineid, String sensorid, String beginTime, String endTime) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}

		ModelAndView mv = new JModelAndView("monitordata/monitordataupdatelist.html",
				0, request, response);
		mv.addObject(Globals.MV_ATTR_USERROLE, user.getUserRole()); // control the user operation based on role

		// set query condition to front page back
		mv.addObject("sensorTypeName", sensorTypeName);
		mv.addObject("monitorlineid", monitorlineid);
		mv.addObject("sensorid", sensorid);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);

		// for condition selecting
		List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
		mv.addObject("sensorTypes", sensorTypes);
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
			mv.addObject("monitorLines", monitorLines);
		}
		// for condition selecting
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
			mv.addObject("sensors", sensors);
		}

		// Query object
		MonitorDataQueryObject qo = new MonitorDataQueryObject(currentPage, mv, "collectingTime", "DESC");
		qo.setPageSize(Integer.valueOf(12));
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			qo.addQuery("obj.sensorType",
					new SysMap("sensorType", sensorTypeName), "=");
		}
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			qo.addQuery("obj.monitorLine.id",
					new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
		}
		if(null != sensorid && !sensorid.isEmpty()){
			qo.addQuery("obj.sensor.id",
					new SysMap("sensorid", Long.valueOf(Long.parseLong(sensorid))), "=");
		}
		if(null != beginTime && !beginTime.isEmpty() && !endTime.isEmpty() && !endTime.isEmpty()){
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date beginTimeDate = dateFormat.parse(beginTime);
				long beginTimeLong = beginTimeDate.getTime();
				Date endTimeTimeDate = dateFormat.parse(endTime); 
				long endTimeTimeLong = endTimeTimeDate.getTime();
				qo.addQuery("obj.collectingTime",
						new SysMap("beginCollectingTime", Long.valueOf(beginTimeLong)), ">=");
				qo.addQuery("obj.collectingTime",
						new SysMap("endCollectingTime", Long.valueOf(endTimeTimeLong)), "<=");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		IPageList pList = this.dataViewService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

		mv.addObject("SensorTypes", sensorTypeService.getSensorTypeDisplayNames()); // for display in UI
		mv.addObject("SensorUnits", sensorTypeService.getSensorUnits()); // for display in UI
		return mv;
	}

	/**
	 * a jquery request
	 * @param request
	 * @param response
	 * @param formString
	 */
	@RequestMapping({"/updateMonitorDatas.htm"})
	public void updateMonitorDatas(HttpServletRequest request, HttpServletResponse response, String formString) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			//"resultcode:1,resultstring:请登录!"
			StringBuffer sb = new StringBuffer(Globals.WEB_RESULTCODE);
			sb.append(":");
			sb.append(Globals.WEB_RESULTCODE_NOTLOGIN);
			sb.append(",");
			sb.append(Globals.WEB_RESULTSTRING);
			sb.append(":请登录!");
			Utils.setResponse(response, sb.toString());
			return; // not login
		} else if(!user.getUserRole().equals(Globals.USERROLE_ADMIN)){
			Utils.setResponse(response, "当前用户没有修改权限!");
			return; // not login
		}
		if(null != formString && !formString.isEmpty()){
			String[] sensorDataArray = formString.split(";");
			if(null != sensorDataArray && sensorDataArray.length > 0){
				MonitorDataView[] monitorDataList = new MonitorDataView[sensorDataArray.length];
				int i=0;
				for(String sensorDataString : sensorDataArray){
					String[] sensorNameValue = sensorDataString.split("=");
					String dataIdString = sensorNameValue[0];
					String monitorDataString = sensorNameValue[1];
					String[] monitorDatStrArray = monitorDataString.split(",");
					String sensorName = monitorDatStrArray[0];
					if(monitorDatStrArray.length != 4){
						Utils.setResponse(response, "传感器[" + sensorName + "]数据格式错误!");
						return;
					}
					String deviceData = monitorDatStrArray[1].trim();
					String sinkingData = monitorDatStrArray[2].trim();
					String sinkingAccumulation = monitorDatStrArray[3].trim();
					if(null != sensorName && !sensorName.isEmpty() && null != deviceData && !deviceData.isEmpty()
							&& null != sinkingData && !sinkingData.isEmpty()){
						double deviceDataD = -99999999;
						double sinkingDataD = -99999999;
						double sinkingAccumulationD = -99999999;
						try {
							deviceDataD = Double.valueOf(deviceData);
							sinkingDataD = Double.valueOf(sinkingData);
							sinkingAccumulationD = Double.valueOf(sinkingAccumulation);
						} catch (Throwable e){
							Utils.setResponse(response, "传感器[" + sensorName + "]数据格式错误!");
							return;
						}
						MonitorDataView monitorData = this.dataViewService.getObjById(Long.valueOf(dataIdString));
						monitorData.setDeviceData(CommUtil.roundDouble(deviceDataD));
						monitorData.setSinkingData(CommUtil.roundDouble(sinkingDataD));
						monitorData.setSinkingAccumulation(CommUtil.roundDouble(sinkingAccumulationD));
						monitorDataList[i] = monitorData;

						Sensor sensor = this.sensorService.getObjByProperty("name", sensorName);
						MonitorLine monitorLine = monitorData.getMonitorLine();
						if(null != sensor){
							boolean modifySensor = false;
							if(sensor.getLastCollectingTime() <= monitorData.getCollectingTime()){ // used to query
								sensor.setLastCollectingTime(monitorData.getCollectingTime());
								sensor.setLastDeviceData(CommUtil.roundDouble(monitorData.getDeviceData()));
								sensor.setLastSinkingData(CommUtil.roundDouble(monitorData.getSinkingData()));
								sensor.setLastSinkingAccumulation(CommUtil.roundDouble(monitorData.getSinkingAccumulation()));
								if (!sensor.isBase()) {
									calculateAlarmLevel(monitorLine, sensor);
								}
								modifySensor = true;
							}
							if (modifySensor) {
								this.sensorService.update(sensor);
							}
						}
					}
					i++;
				}
				this.dataViewService.update(monitorDataList);
			}
		}
		Utils.setResponse(response, "保存成功!");
	}
	
	private void calculateAlarmLevel(MonitorLine monitorLine, Sensor sensor) {
		double thresholdl1 = monitorLine.getSinkingthresholdl1();
		double thresholdl2 = monitorLine.getSinkingthresholdl2();
		double thresholdl3 = monitorLine.getSinkingthresholdl3();
		/*//特殊处理，TAIA项目将数据库内阈值放大10倍，本身字段
		if(monitorLine.getName().equals("G1-01")){
			thresholdl1 *= 10;
			thresholdl2 *= 10;
			thresholdl3 *= 10;
		}*/
		if(thresholdl1 < 0){
			thresholdl1 = thresholdl1 * -1;
		}
		if(thresholdl2 < 0){
			thresholdl2 = thresholdl2 * -1;
		}
		if(thresholdl3 < 0){
			thresholdl3 = thresholdl3 * -1;
		}
		// comment on 20161215, 判断alarm，原来是根据即时形变，改成按照累计形变判断
		double nagetiveSinkingAccumulation = sensor.getLastSinkingAccumulation();
		if(nagetiveSinkingAccumulation < 0){
			nagetiveSinkingAccumulation = nagetiveSinkingAccumulation * -1;
		}
		int alarmLevel = calculateAlarmLevelWithData(thresholdl1, thresholdl2, thresholdl3, nagetiveSinkingAccumulation);
		if(Globals.SET_INCLINOMETER.contains(sensor.getSensorType())){
			double nagetiveXData = sensor.getLastDeviceData();
			if(nagetiveXData < 0){
				nagetiveXData = nagetiveXData * -1;
			}
			int alarmLevelX = calculateAlarmLevelWithData(thresholdl1, thresholdl2, thresholdl3, nagetiveXData);
			double nagetiveYData = sensor.getLastSinkingData();
			if(nagetiveYData < 0){
				nagetiveYData = nagetiveYData * -1;
			}
			int alarmLevelY = calculateAlarmLevelWithData(thresholdl1, thresholdl2, thresholdl3, nagetiveYData);
			int higherLevel = (alarmLevel > alarmLevelX) ? alarmLevel : alarmLevelX;
			higherLevel = (higherLevel > alarmLevelY) ? higherLevel : alarmLevelY;
			sensor.setAlarmLevel(higherLevel);
		} else {
			sensor.setAlarmLevel(alarmLevel);
		}
		//Logger.printlnWithTime("Sensor " + sensor.getName() +" - alarm level is " + sensor.getAlarmLevel());
	}


	private int calculateAlarmLevelWithData(double thresholdl1,
			double thresholdl2, double thresholdl3, double nagetiveSinkingData) {
		if(nagetiveSinkingData >= thresholdl3){
			return Globals.ALARMLEVEL_L3;
		} else if(nagetiveSinkingData >= thresholdl2){
			return Globals.ALARMLEVEL_L2;
		} else if(nagetiveSinkingData >= thresholdl1){
			return Globals.ALARMLEVEL_L1;
		} else {
			return Globals.ALARMLEVEL_NORMAL;
		}
	}
}
