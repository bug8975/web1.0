package com.monitor.action.monitordata;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.constant.NameToDisplayNameEnum;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.MonitorDataQueryObject;
import com.monitor.foundation.domain.query.SensorQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

import antlr.StringUtils;

@Controller
public class DataLineViewAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	private IMonitorDataService monitorDataService;

	//    @SecurityMapping(title = "曲线查看", value = "/monitordata/monitordataline.htm*", rtype = "monitordata", rname = "监测数据", rcode = "monitordata", rgroup = "监测数据")
	@RequestMapping({ "/monitordata/monitordataline.htm" })
	public ModelAndView getMonitorDataLine(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String sensorTypeName, String monitorlineid, String sensorid) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("monitordata/monitordataline.html",
				0, request, response);

		// return query condition
		mv.addObject("sensorTypeName", sensorTypeName);
		mv.addObject("monitorlineid", monitorlineid);
		mv.addObject("sensorid", sensorid);

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
		SensorQueryObject qo = new SensorQueryObject(currentPage, mv, "name", "ASC");
		qo.setPageSize(Integer.valueOf(15));
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
			mv.addObject("monitorLines", monitorLines);
		}
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			String[] sensorDatas = getSersorMonitorDataWithString(monitorlineid);
			if(null != sensorDatas){
				if("InclinoMeter".equals(sensorTypeName) || "FixedInclinoMeter".equals(sensorTypeName)){
					mv.addObject("sinkingData", sensorDatas[0]);
					mv.addObject("sinkingAccumulation", sensorDatas[1]);
					mv.addObject("sensor_name",sensorDatas[2]);
					mv.addObject("initData",sensorDatas[3]);
					mv.addObject("cellectTime",sensorDatas[4]);
					mv.addObject("specialData", true);
				}else{
					mv.addObject("sinkingData", sensorDatas[0]);
					mv.addObject("sinkingAccumulation", sensorDatas[1]);
					mv.addObject("sensor_name",sensorDatas[2]);
					mv.addObject("initData",sensorDatas[3]);
					mv.addObject("cellectTime",sensorDatas[4]);
					mv.addObject("hasData", true);
					// mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_SUCCESS);
				}
			}else {
				mv.addObject("resultcode", Integer.valueOf(10));
				mv.addObject("resultstring", "没有数据");
				mv.addObject("hasData", Boolean.valueOf(false));
			}
		}

		if(null != sensorid && !sensorid.isEmpty()){
			qo.addQuery("obj.id",
					new SysMap("sensorid", Long.valueOf(Long.parseLong(sensorid))), "=");
		}
		IPageList pList = this.sensorService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

		mv.addObject("SensorTypes", sensorTypeService.getSensorTypeDisplayNames()); // for display in UI
		mv.addObject("SensorUnits", sensorTypeService.getSensorUnits()); // for display in UI

		return mv;
	}

	@RequestMapping({ "/createLine.htm" })
	public void getMonitorDataLine(HttpServletRequest request, HttpServletResponse response, String sensorType){
		List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorType+"'", null, -1, -1);
		List list = new ArrayList();
		Long monitorlineid = null;
		String[] sensorDatas = null;
		for (MonitorLine monitorline : monitorLines) {
			Map map = new HashMap();
			monitorlineid = monitorline.getId();
			if(monitorlineid!=null&& !"".equals(monitorlineid)){
				sensorDatas = getLine(monitorlineid.toString());
				map.put("sinkingData", sensorDatas[0]);
				map.put("sinkingAccumulation", sensorDatas[1]);
				map.put("sensor_name",sensorDatas[2]);
				map.put("initData",sensorDatas[3]);
				map.put("cellectTime",sensorDatas[4]);
				map.put("monitorName", sensorDatas[5]);
				list.add(map);
			}
		}
		String temp = Json.toJson(list, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	private String[] getLine(String monitorlineid) {
		MonitorDataQueryObject qo = new MonitorDataQueryObject();
		qo.setPageSize(Integer.MAX_VALUE);
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			qo.addQuery("obj.monitorLine.id",
					new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
		}
		IPageList pList = this.sensorService.list(qo);
		List<Sensor> results = pList.getResult();

		if(null != results && !results.isEmpty()){
			StringBuffer sbSinkingData = new StringBuffer("["); // y for InclinoMeter and FixedInclinoMeter, SinkingData for other sessor
			StringBuffer sbZData = new StringBuffer("[");
			StringBuffer sersor_names= new StringBuffer("[");
			StringBuffer initData= new StringBuffer("[");
			String cellectTime = null;
			String monitorName = null;
			int i=1;
			double FirstsinkingData = 0.0;
			for(Sensor sensorData: results){
				if( FirstsinkingData == 0){
					FirstsinkingData = sensorData.getLastSinkingData();
				}
				appendDataSpecial(sbSinkingData, sensorData.getLastSinkingData());
				if(i < results.size()){ // has next
					sbSinkingData.append(",");
				}
				appendDataSpecial(sbZData, sensorData.getLastSinkingAccumulation());
				if(i < results.size()){ // has next
					sbZData.append(",");
				}
				String[] name = sensorData.getName().split("-");
				appendData(sersor_names, name[name.length-1]);
				if(i < results.size()){ // has next
					sersor_names.append(",");
				}
				appendInitDataSpecial(initData, sensorData.getFirstDeviceData());
				if(i < results.size()){ // has next
					initData.append(",");
				}
				//new add
/*				long collectingTime = sensorData.getLastCollectingTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String cTime = sdf.format( collectingTime );
				appendData(cellectTime, cTime);
				if(i < results.size()){ // has next
					cellectTime.append(",");
				}*/
				long collectingTime = sensorData.getLastCollectingTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				cellectTime = sdf.format( collectingTime );
				
				monitorName = sensorData.getMonitorLine().getName();

				i++;
			}
			sbSinkingData.append("]");
			sbZData.append("]");
			sersor_names.append("]");
			initData.append("]");
			
			return new String[] {sbSinkingData.toString(), sbZData.toString(), sersor_names.toString(),initData.toString(),cellectTime.toString(),monitorName.toString() };
		}
		return null;
	}
	
	private void appendDataSpecial(StringBuffer strBuffer, String data) {
		strBuffer.append(data);
	}
	
	private void appendDataSpecial(StringBuffer strBuffer, double data) {
		strBuffer.append(data);
	}
	
	private void appendInitDataSpecial(StringBuffer strBuffer, double data) {
		double data1 = 0;
		strBuffer.append(data1);
	}


	private String[] getSersorMonitorDataWithString(String monitorlineid) {
		MonitorDataQueryObject qo = new MonitorDataQueryObject();
		qo.setPageSize(Integer.MAX_VALUE);
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			qo.addQuery("obj.monitorLine.id",
					new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
		}
		IPageList pList = this.sensorService.list(qo);
		List<Sensor> results = pList.getResult();

		if(null != results && !results.isEmpty()){
			StringBuffer sbSinkingData = new StringBuffer("["); // y for InclinoMeter and FixedInclinoMeter, SinkingData for other sessor
			StringBuffer sbZData = new StringBuffer("[");
			StringBuffer sersor_names= new StringBuffer("[");
			StringBuffer initData= new StringBuffer("[");
			StringBuffer cellectTime= new StringBuffer("[");

			int i=1;
			for(Sensor sensorData: results){
				appendData(sbSinkingData, sensorData.getLastSinkingData());
				if(i < results.size()){ // has next
					sbSinkingData.append(",");
				}
				appendData(sbZData, sensorData.getLastSinkingAccumulation());
				if(i < results.size()){ // has next
					sbZData.append(",");
				}
				String[] name = sensorData.getName().split("-");
				appendData(sersor_names, name[name.length-1]);
				if(i < results.size()){ // has next
					sersor_names.append(",");
				}
				appendInitData(initData, sensorData.getFirstDeviceData());
				if(i < results.size()){ // has next
					initData.append(",");
				}
				//new add
				long collectingTime = sensorData.getLastCollectingTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String cTime = sdf.format( collectingTime );
				appendData(cellectTime, cTime);
				if(i < results.size()){ // has next
					cellectTime.append(",");
				}
				i++;
			}
			sbSinkingData.append("]");
			sbZData.append("]");
			sersor_names.append("]");
			initData.append("]");
			cellectTime.append("]");
			return new String[] {sbSinkingData.toString(), sbZData.toString(), sersor_names.toString(),initData.toString(),cellectTime.toString() };
		}
		return null;
	}

	
	
	private void appendData(StringBuffer strBuffer, String data) {
		strBuffer.append("'");
		strBuffer.append(data);
		strBuffer.append("'");
	}

	private void appendData(StringBuffer strBuffer, double data) {
		strBuffer.append("[");
		strBuffer.append(data);
		strBuffer.append("]");
	}

	private void appendInitData(StringBuffer strBuffer, double data) {
		double data1 = 0;
		strBuffer.append("[");
		strBuffer.append(data1);
		strBuffer.append("]");
	}

	
}
