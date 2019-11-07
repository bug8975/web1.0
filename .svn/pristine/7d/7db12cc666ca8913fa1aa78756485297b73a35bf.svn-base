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

import org.apache.poi.hssf.util.HSSFColor.GOLD;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.MonitorDataQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class DataViewGisAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;
	
	@Autowired
	private IMonitorDataService monitorDataService;

	@RequestMapping({ "/monitordata/monitordataforgis.htm" })
	public ModelAndView monitorDataList(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String sensorTypeName, String monitorlineid, String sensorid, String alarmlevel) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("monitordata/monitordataforgis.html",
				0, request, response);
		
        List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj", null, -1, -1);
        mv.addObject("sensors", sensors);
		return mv;
	}
	
	@RequestMapping({"/query_sensorType.htm"})
    public void loadSensorType(HttpServletRequest request, HttpServletResponse response ,String sensorName) {
        Map params = new HashMap();
        List list = new ArrayList();
        if(sensorName.matches("^[A-Z]+[-]+[0-9]+$")){
        	List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.name ='" + sensorName + "'", params, -1, -1);
            for (MonitorLine monitorLine : monitorLines) {
                Map map = new HashMap();
                map.put("sensorType", monitorLine.getSensorType());
                list.add(map);
            }
        }else{
        	List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.name ='" + sensorName + "'", params, -1, -1);
            for (Sensor sensor : sensors) {
                Map map = new HashMap();
                map.put("sensorType", sensor.getSensorType());
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
	
	
	@RequestMapping({"/query_sensor.htm"})
    public void loadSensor(HttpServletRequest request, HttpServletResponse response ,String sensorName) {
        Map params = new HashMap();
        List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.name ='" + sensorName+ "'", params, -1, -1);
        List list = new ArrayList();
        for (Sensor sensor : sensors) {
            Map map = new HashMap();
            map.put("id", sensor.getId());
            map.put("sensorName", sensor.getName());
            map.put("base", sensor.isBase());
            map.put("alarmLevel", sensor.getAlarmLevel());
            map.put("monitorLineid", sensor.getMonitorLine().getId());
            map.put("collectingTime", sensor.getLastCollectingTime());
            List<SensorType> sensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name='"+ sensor.getSensorType()+"'", null);
            for(SensorType sensorTypes:sensorType){
            	map.put("sensorType", sensorTypes.getName());
            	map.put("sensorTypeName", sensorTypes.getDisplayName());
            	map.put("unit", sensorTypes.getUnit());
            }
            map.put("deviceData", sensor.getLastDeviceData());
            map.put("sinkingData", sensor.getLastSinkingData());
            map.put("sinkingAccumulation", sensor.getLastSinkingAccumulation());
            list.add(map);
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
	
	@RequestMapping({"/query_monitorline.htm"})
    public void loadMonitorLine(HttpServletRequest request, HttpServletResponse response ,String monitorName) {
		List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.name='"+monitorName+"'", null, -1, -1);
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
				map.put("sensorType", sensorDatas[6]);
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
			String sensorType = null;
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
				
				sensorType = sensorData.getSensorType();
				i++;
			}
			sbSinkingData.append("]");
			sbZData.append("]");
			sersor_names.append("]");
			initData.append("]");
			
			return new String[] {sbSinkingData.toString(), sbZData.toString(), sersor_names.toString(),initData.toString(),cellectTime.toString(),monitorName.toString(),sensorType.toString()};
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
	
	private void appendData(StringBuffer strBuffer, String data) {
		strBuffer.append("'");
		strBuffer.append(data);
		strBuffer.append("'");
	}
}
