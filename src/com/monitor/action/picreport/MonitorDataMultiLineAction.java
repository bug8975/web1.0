package com.monitor.action.picreport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class MonitorDataMultiLineAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorDataService monitorDataService;

	@Autowired
	private IMonitorDataViewService dataViewService;


	@RequestMapping({ "/picreport/singledatamultiline.htm" })
	public ModelAndView getSingleDataLine(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String sensorTypeName, String monitorlineid, String beginTime, String endTime, String AxialDirection) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("picreport/singledatamultiline.html",
				0, request, response);

		// set query condition to front page back
		mv.addObject("sensorTypeName", sensorTypeName);
		mv.addObject("monitorlineid", monitorlineid);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("direction", AxialDirection);

		// for condition selecting
		List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
		mv.addObject("sensorTypes", sensorTypes);
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
			mv.addObject("monitorLines", monitorLines);
		}
		// for condition selecting
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			List list = new ArrayList();

			String his_beginTime = (String) session.getAttribute("beginTime");
			String his_endTime = (String) session.getAttribute("endTime");
			String his_monitorlineid = (String)session.getAttribute("monitorlineid");
			if(beginTime.equals(his_beginTime) && endTime.equals(his_endTime) && monitorlineid.equals(his_monitorlineid)){
				String sinkingData = (String) session.getAttribute("sinkingData");
				String xdata = (String) session.getAttribute("xdata");
				String zdata = (String) session.getAttribute("zdata");
				String sensorType = (String) session.getAttribute("sensorType");
				String unit = (String) session.getAttribute("unit");
				String sensorName = (String) session.getAttribute("sensorName");
				list = (ArrayList) session.getAttribute("datas");


				mv.addObject("sinkingData", sinkingData); 	// SinkingData/Y
				mv.addObject("xdata", xdata); 				// DeviceData/X
				mv.addObject("zdata", zdata); 				// SinkingAccumulation/Z
				mv.addObject("sensorType",sensorType);
				mv.addObject("unit", unit);
				mv.addObject("hasData", true);

				session.setAttribute("sinkingData", sinkingData); 	// SinkingData/Y
				session.setAttribute("xdata", xdata); 				// DeviceData/X
				session.setAttribute("zdata", zdata); 				// SinkingAccumulation/Z
				session.setAttribute("sensorType",sensorType);
				session.setAttribute("unit", unit);
				session.setAttribute("sensorName", sensorName);
				session.setAttribute("hasData", true);
				session.setAttribute("datas",list);

				session.setAttribute("beginTime", beginTime);
				session.setAttribute("endTime", endTime);
				session.setAttribute("monitorlineid",monitorlineid);

				mv.addObject("datas", list);
				mv.addObject("size", list.size());
			}else{
				List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.base=false and obj.monitorLine.id="+monitorlineid, null, -1, -1);
				mv.addObject("sensors", sensors);
				for(Sensor ss:sensors){
					long sensorIdLong = ss.getId();
					Sensor sensor = this.sensorService.getObjById(sensorIdLong);
					SensorType sensorType = this.sensorTypeService.getObjByName(sensor.getSensorType());
					String[] monitorDatas = getSensorMonitorDataWithString(sensorIdLong, beginTime, endTime);
					Map map = new HashMap();
					if(null != monitorDatas){
						map.put("sinkingData", monitorDatas[0]); // SinkingData/Y
						map.put("xdata", monitorDatas[1]); // DeviceData/X
						map.put("zdata", monitorDatas[2]); // SinkingAccumulation/Z
						map.put("sensorType", sensorType.getDisplayName());
						map.put("unit", sensorType.getUnit());
						map.put("sensorName", sensor.getName());

						mv.addObject("sinkingData", monitorDatas[0]); // SinkingData/Y
						mv.addObject("xdata", monitorDatas[1]); // DeviceData/X
						mv.addObject("zdata", monitorDatas[2]); // SinkingAccumulation/Z
						mv.addObject("sensorType", sensorType.getDisplayName());
						mv.addObject("unit", sensorType.getUnit());
						mv.addObject("hasData", true);
						map.put("hasData", true);
						list.add(map);

						session.setAttribute("sinkingData", monitorDatas[0]); // SinkingData/Y
						session.setAttribute("xdata", monitorDatas[1]); // DeviceData/X
						session.setAttribute("zdata", monitorDatas[2]); // SinkingAccumulation/Z
						session.setAttribute("sensorType", sensorType.getDisplayName());
						session.setAttribute("unit", sensorType.getUnit());
						session.setAttribute("sensorName", sensor.getName());
						session.setAttribute("hasData", true);
						// mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_SUCCESS);
					} else {
						mv.addObject("hasData", false);
						mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_NODATA);
						mv.addObject(Globals.WEB_RESULTSTRING, Globals.WEB_RESULTSTRING_NODATA);
					}
				}
				session.setAttribute("beginTime", beginTime);
				session.setAttribute("endTime", endTime);
				session.setAttribute("monitorlineid",monitorlineid);
				mv.addObject("datas", list);
				session.setAttribute("datas",list);
				mv.addObject("size", list.size());
			}
		}
		return mv;
	}

	private String[] getSensorMonitorDataWithString(long sensorid, String beginTime,
			String endTime) {
		List<MonitorDataView> results = null;
		try {
			Date beginTimeDate = Globals.DateFormat.parse(beginTime);
			long beginTimeLong = beginTimeDate.getTime();
			Date endTimeTimeDate = Globals.DateFormat.parse(endTime); 
			long endTimeTimeLong = endTimeTimeDate.getTime();
			endTimeTimeLong = endTimeTimeLong + (23*3600+59*60+59)*1000;
			results = com.monitor.action.picreport.Utils
					.querySensorMonitorData(dataViewService, sensorid, beginTimeLong, endTimeTimeLong);
		} catch (ParseException e) {
			System.err.println("[single line] Exception when get sensor monitor data value for sensor:" + sensorid + ", " + e.getMessage());
			// e.printStackTrace();
		}
		if(null != results && !results.isEmpty()){
			StringBuffer sbSinkingData = new StringBuffer("["); // y for InclinoMeter and FixedInclinoMeter, SinkingData for other sessor
			StringBuffer sbXData = new StringBuffer("[");
			StringBuffer sbZData = new StringBuffer("[");
			int i = 1;
			for(MonitorDataView monitorData : results){
				long collectingTime = monitorData.getCollectingTime();
				appendData(sbSinkingData, collectingTime, monitorData.getSinkingData());
				if(i < results.size()){ // has next
					sbSinkingData.append(",");
				}
				appendData(sbXData, collectingTime, monitorData.getDeviceData());
				if(i < results.size()){ // has next
					sbXData.append(",");
				}
				appendData(sbZData, collectingTime, monitorData.getSinkingAccumulation());
				if(i < results.size()){ // has next
					sbZData.append(",");
				}
				i++;
			}
			sbSinkingData.append("]");
			sbXData.append("]");
			sbZData.append("]");

			return new String[] {sbSinkingData.toString(), sbXData.toString(), sbZData.toString()};
		}
		return null;
	}

	private void appendData(StringBuffer strBuffer, long collectingTime, double data) {
		strBuffer.append("[");
		strBuffer.append(collectingTime);
		strBuffer.append(",");
		strBuffer.append(data);
		strBuffer.append("]");
	}
	
	//TODO: 新增主页累计形变单/多曲线
//	@RequestMapping({ "/home.htm" })
//	public ModelAndView insertSingleDataLine(HttpServletRequest request, HttpServletResponse response, HttpSession session,
//			String sensorTypeName, String monitorlineid, String beginTime, String endTime, String AxialDirection) {
//		User user = Utils.checkLoginedUser(request);
//		if(user == null){
//			return Utils.redirectToLoginForNonLogined(request, response);
//		}
//		ModelAndView mv = new JModelAndView("home.html",
//				0, request, response);
//
//		// set query condition to front page back
//		mv.addObject("sensorTypeName", sensorTypeName);
//		mv.addObject("monitorlineid", monitorlineid);
//		mv.addObject("beginTime", beginTime);
//		mv.addObject("endTime", endTime);
//		mv.addObject("direction", AxialDirection);
//
//		// for condition selecting
//		List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
//		mv.addObject("sensorTypes", sensorTypes);
//		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
//			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
//			mv.addObject("monitorLines", monitorLines);
//		}
//		// for condition selecting
//		if(null != monitorlineid && !monitorlineid.isEmpty()){
//			List list = new ArrayList();
//
//			String his_beginTime = (String) session.getAttribute("beginTime");
//			String his_endTime = (String) session.getAttribute("endTime");
//			String his_monitorlineid = (String)session.getAttribute("monitorlineid");
//			if(beginTime.equals(his_beginTime) && endTime.equals(his_endTime) && monitorlineid.equals(his_monitorlineid)){
//				String sinkingData = (String) session.getAttribute("sinkingData");
//				String xdata = (String) session.getAttribute("xdata");
//				String zdata = (String) session.getAttribute("zdata");
//				String sensorType = (String) session.getAttribute("sensorType");
//				String unit = (String) session.getAttribute("unit");
//				String sensorName = (String) session.getAttribute("sensorName");
//				list = (ArrayList) session.getAttribute("datas");
//
//
//				mv.addObject("sinkingData", sinkingData); 	// SinkingData/Y
//				mv.addObject("xdata", xdata); 				// DeviceData/X
//				mv.addObject("zdata", zdata); 				// SinkingAccumulation/Z
//				mv.addObject("sensorType",sensorType);
//				mv.addObject("unit", unit);
//				mv.addObject("hasData", true);
//
//				session.setAttribute("sinkingData", sinkingData); 	// SinkingData/Y
//				session.setAttribute("xdata", xdata); 				// DeviceData/X
//				session.setAttribute("zdata", zdata); 				// SinkingAccumulation/Z
//				session.setAttribute("sensorType",sensorType);
//				session.setAttribute("unit", unit);
//				session.setAttribute("sensorName", sensorName);
//				session.setAttribute("hasData", true);
//				session.setAttribute("datas",list);
//
//				session.setAttribute("beginTime", beginTime);
//				session.setAttribute("endTime", endTime);
//				session.setAttribute("monitorlineid",monitorlineid);
//
//				mv.addObject("datas", list);
//				mv.addObject("size", list.size());
//			}else{
//				List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.base=false and obj.monitorLine.id="+monitorlineid, null, -1, -1);
//				mv.addObject("sensors", sensors);
//				for(Sensor ss:sensors){
//					long sensorIdLong = ss.getId();
//					Sensor sensor = this.sensorService.getObjById(sensorIdLong);
//					SensorType sensorType = this.sensorTypeService.getObjByName(sensor.getSensorType());
//					String[] monitorDatas = getSensorMonitorDataWithString(sensorIdLong, beginTime, endTime);
//					Map map = new HashMap();
//					if(null != monitorDatas){
//						map.put("sinkingData", monitorDatas[0]); // SinkingData/Y
//						map.put("xdata", monitorDatas[1]); // DeviceData/X
//						map.put("zdata", monitorDatas[2]); // SinkingAccumulation/Z
//						map.put("sensorType", sensorType.getDisplayName());
//						map.put("unit", sensorType.getUnit());
//						map.put("sensorName", sensor.getName());
//
//						mv.addObject("sinkingData", monitorDatas[0]); // SinkingData/Y
//						mv.addObject("xdata", monitorDatas[1]); // DeviceData/X
//						mv.addObject("zdata", monitorDatas[2]); // SinkingAccumulation/Z
//						mv.addObject("sensorType", sensorType.getDisplayName());
//						mv.addObject("unit", sensorType.getUnit());
//						mv.addObject("hasData", true);
//						map.put("hasData", true);
//						list.add(map);
//
//						session.setAttribute("sinkingData", monitorDatas[0]); // SinkingData/Y
//						session.setAttribute("xdata", monitorDatas[1]); // DeviceData/X
//						session.setAttribute("zdata", monitorDatas[2]); // SinkingAccumulation/Z
//						session.setAttribute("sensorType", sensorType.getDisplayName());
//						session.setAttribute("unit", sensorType.getUnit());
//						session.setAttribute("sensorName", sensor.getName());
//						session.setAttribute("hasData", true);
//						// mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_SUCCESS);
//					} else {
//						mv.addObject("hasData", false);
//						mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_NODATA);
//						mv.addObject(Globals.WEB_RESULTSTRING, Globals.WEB_RESULTSTRING_NODATA);
//					}
//				}
//				session.setAttribute("beginTime", beginTime);
//				session.setAttribute("endTime", endTime);
//				session.setAttribute("monitorlineid",monitorlineid);
//				mv.addObject("datas", list);
//				session.setAttribute("datas",list);
//				mv.addObject("size", list.size());
//			}
//		}
//		return mv;
//	}
}
