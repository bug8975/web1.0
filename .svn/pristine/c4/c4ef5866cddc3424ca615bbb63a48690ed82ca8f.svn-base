package com.monitor.action.picreport;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
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
public class MonitorDataLineAction {

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
    

    @RequestMapping({ "/picreport/singledataline.htm" })
    public ModelAndView getSingleDataLine(HttpServletRequest request, HttpServletResponse response, 
    		String sensorTypeName, String monitorlineid, String sensorid, String beginTime, String endTime) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		return Utils.redirectToLoginForNonLogined(request, response);
    	}
        ModelAndView mv = new JModelAndView("picreport/singledataline.html",
                 0, request, response);

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
            List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.base=false and obj.monitorLine.id="+monitorlineid, null, -1, -1);
            mv.addObject("sensors", sensors);
        }
        
        //query data
        if(null != sensorid && !sensorid.isEmpty()){
        	long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
        	Sensor sensor = this.sensorService.getObjById(sensorIdLong);
        	SensorType sensorType = this.sensorTypeService.getObjByName(sensor.getSensorType());
            String[] monitorDatas = getSensorMonitorDataWithString(sensorIdLong, beginTime, endTime);
            if(null != monitorDatas){
    		    mv.addObject("sinkingData", monitorDatas[0]); // SinkingData/Y
    		    mv.addObject("xdata", monitorDatas[1]); // DeviceData/X
    		    mv.addObject("zdata", monitorDatas[2]); // SinkingAccumulation/Z
    		    mv.addObject("sensorType", sensorType.getDisplayName());
    		    mv.addObject("unit", sensorType.getUnit());
    		    if(Globals.SET_INCLINOMETER.contains(sensorType.getName())){
    		    	mv.addObject("isInclinometer", true);
    		    }
    		    mv.addObject("hasData", true);
    		    // mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_SUCCESS);
            } else {
    		    mv.addObject("hasData", false);
            	mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_NODATA);
            	mv.addObject(Globals.WEB_RESULTSTRING, Globals.WEB_RESULTSTRING_NODATA);
            }
        }

        return mv;
    }


    @RequestMapping({ "/picreport/currentsinkingdataline.htm" })
    public ModelAndView getCurrentSinkingDataLine(HttpServletRequest request, HttpServletResponse response, 
    		String sensorTypeName, String monitorlineid, String sensorid, String beginTime, String endTime) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		return Utils.redirectToLoginForNonLogined(request, response);
    	}
        ModelAndView mv = new JModelAndView("picreport/currentsinkingdataline.html",
                 0, request, response);

        // set query condition to front page back
        mv.addObject("sensorTypeName", sensorTypeName);
        mv.addObject("monitorlineid", monitorlineid);
        mv.addObject("sensorid", sensorid);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);

        // for condition selecting
        List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
        List<SensorType> showingSensorTypes = new ArrayList<SensorType>(sensorTypes);
        showingSensorTypes.remove(this.sensorTypeService.getObjByName("InclinoMeter")); // do not generate current sinking data line for InclinoMeter(20161015)
        showingSensorTypes.remove(this.sensorTypeService.getObjByName("FixedInclinoMeter"));
        showingSensorTypes.remove(this.sensorTypeService.getObjByName("TotalStation"));
        mv.addObject("sensorTypes", showingSensorTypes);
        if(null != sensorTypeName && !sensorTypeName.isEmpty()){
            List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
            mv.addObject("monitorLines", monitorLines);
        }
        // for condition selecting
        if(null != monitorlineid && !monitorlineid.isEmpty()){
            List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.base=false and obj.monitorLine.id="+monitorlineid, null, -1, -1);
            mv.addObject("sensors", sensors);
        }
        
        //query data
        if(null != sensorid && !sensorid.isEmpty()){
        	long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
        	Sensor sensor = this.sensorService.getObjById(sensorIdLong);
        	SensorType sensorType = this.sensorTypeService.getObjByName(sensor.getSensorType());
        	// on 20161015, 显示除倾角和测斜仪之外其他曲线
            String monitorData = getSensorMonitorSinkingDataWithString(sensorIdLong, beginTime, endTime);
            if(null != monitorData){
    		    mv.addObject("sinkingData", monitorData);
    		    mv.addObject("sensorType", sensorType.getDisplayName());
    		    mv.addObject("unit", sensorType.getUnit());
    		    if(Globals.SET_INCLINOMETER.contains(sensorType.getName())){
    		    	mv.addObject("isInclinometer", true);
    		    }
    		    mv.addObject("hasData", true);
    		    // mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_SUCCESS);
            } else {
            	mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_NODATA);
            	mv.addObject(Globals.WEB_RESULTSTRING, Globals.WEB_RESULTSTRING_NODATA);
            	mv.addObject("hasData", false);
            }
        }

        return mv;
    }
    
    @RequestMapping({ "/picreport/multipledataline.htm" })
    public ModelAndView getMultipleleDataLine(HttpServletRequest request, HttpServletResponse response,
            String sensorTypeName, String monitorlineid, String beginTime, String endTime) {
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
    		return Utils.redirectToLoginForNonLogined(request, response);
    	}
        ModelAndView mv = new JModelAndView("picreport/multipledataline.html", 0, request, response);
        
        // set query condition to front page back
        mv.addObject("sensorTypeName", sensorTypeName);
        mv.addObject("monitorlineid", monitorlineid);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);

        // for condition selecting
        List<SensorType> sensorTypes = this.sensorTypeService.getUsingSensorTypes();
        mv.addObject("sensorTypes", sensorTypes);
        if(null != sensorTypeName && !sensorTypeName.isEmpty()){
            List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
            mv.addObject("monitorLines", monitorLines);
        }

        //query data
		if (null != monitorlineid && !monitorlineid.isEmpty()) {
			boolean hasData = false;
            List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
            if(null != sensors && !sensors.isEmpty()){
            	List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
            	for(Sensor sensor : sensors){
                    String[] monitorDatas = getSensorMonitorDataWithString(sensor.getId(), beginTime, endTime);
                    String sinkingData = monitorDatas[0];
            		if(null != sinkingData && !sinkingData.isEmpty()){
            			Map<String, String> sensorAndData = new HashMap<String, String>();
            			sensorAndData.put("sensorName", sensor.getName());
            			sensorAndData.put("data", sinkingData);
            			datas.add(sensorAndData);
            			hasData = true;
            		}
            	}
            	if(hasData){
            		SensorType sensorType = this.sensorTypeService.getObjByName(sensors.get(0).getSensorType());
                	String dataJson = Json.toJson(datas, JsonFormat.compact());
                	mv.addObject("datas", dataJson);
        		    mv.addObject("sensorType", sensorType.getDisplayName());
        		    mv.addObject("unit", sensorType.getUnit());
            	} else {
                	mv.addObject(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_NODATA);
                	mv.addObject(Globals.WEB_RESULTSTRING, Globals.WEB_RESULTSTRING_NODATA);
            	}

            }
		}
        
        return mv;
    }
    
	/**
	 * a jquery request
	 * @param request
	 * @param response
	 * @param monitorlineid
	 * @param beginTime
	 * @param endTime
	 */
    @RequestMapping({ "/picreport/getmultipledata.htm" })
    public void getMultipleleData(HttpServletRequest request, HttpServletResponse response,
            String monitorlineid, String beginTime, String endTime) {
    	List<Map> datas = new ArrayList<Map>();
    	User user = Utils.checkLoginedUser(request);
    	if(user == null){
			Map error = new HashMap();
			error.put("sensorName", "NOTLOGINERROR");
			error.put("data", "NOTLOGINERROR");
			datas.add(error);
			writeResultToResponse(response, datas);
    		return; // not login
    	}
        //query data
		if (null != monitorlineid && !monitorlineid.isEmpty()) {
            List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
            if(null != sensors && !sensors.isEmpty()){
//            	List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
//            	for(Sensor sensor : sensors){
//                    String monitorData = getSensorMonitorData(sensor.getId(), beginTime, endTime);
//            		if(null != monitorData && !monitorData.isEmpty()){
//            			Map<String, String> sensorAndData = new HashMap<String, String>();
//            			sensorAndData.put("sensorName", sensor.getName());
//            			sensorAndData.put("data", monitorData);
//            			datas.add(sensorAndData);
//            		}
//            	}
            	SensorType sensorType = this.sensorTypeService.getObjByName(sensors.get(0).getSensorType());
            	for(Sensor sensor : sensors){
                	double[][] monitorData = getSensorMonitorDataWithArray(sensor.getId(), beginTime, endTime);
            		if(null != monitorData && monitorData.length>0){
            			Map sensorAndData = new HashMap();
            			sensorAndData.put("sensorName", sensor.getName());
            			sensorAndData.put("data", monitorData);
            			sensorAndData.put("sensorType", sensorType.getDisplayName());
            			sensorAndData.put("unit", sensorType.getUnit());
            			datas.add(sensorAndData);
            		}
            	}
            	writeResultToResponse(response, datas);
            }
		}
    }
    
	private void writeResultToResponse(HttpServletResponse response,
			List<Map> datas) {
		String dataJson = Json.toJson(datas, JsonFormat.compact());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
		    PrintWriter writer = response.getWriter();
		    writer.print(dataJson);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	

	private double[][] getSensorMonitorDataWithArray(long sensorid, String beginTime,
			String endTime) {
		try {
			Date beginTimeDate = Globals.DateFormat.parse(beginTime);
	    	long beginTimeLong = beginTimeDate.getTime();
	    	Date endTimeTimeDate = Globals.DateFormat.parse(endTime); 
	    	long endTimeTimeLong = endTimeTimeDate.getTime();
	    	endTimeTimeLong = endTimeTimeLong + (23*3600+59*60+59)*1000;
	    	
			List<MonitorDataView> results = com.monitor.action.picreport.Utils
				.querySensorMonitorData(dataViewService, sensorid, beginTimeLong,
					endTimeTimeLong);
			if(null != results && !results.isEmpty()){
				double[][] datas = new double[results.size()][2];
			    int i = 0;
			    for(MonitorDataView monitorData : results){
			    	datas[i][0] = monitorData.getCollectingTime();
			    	datas[i][1] = monitorData.getSinkingData();
			    	i++;
			    }
			    return datas;
			}
		} catch (ParseException e) {
			System.err.println("[mutple line] Exception when get monitor data value for sensor:" + sensorid + ", " + e.getMessage());
			// e.printStackTrace();
		}

		return null;
	}


	/**
	 * 
	 * @param sensorid
	 * @param beginTime
	 * @param endTime
	 * @return 
	 */
	private String getSensorMonitorSinkingDataWithString(long sensorid, String beginTime,
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
		    int i = 1;
		    for(MonitorDataView monitorData : results){
		    	long collectingTime = monitorData.getCollectingTime();
		    	appendData(sbSinkingData, collectingTime, monitorData.getSinkingData());
		    	if(i < results.size()){ // has next
		    		sbSinkingData.append(",");
		    	}
		    	i++;
		    }
		    sbSinkingData.append("]");
		    
		    return sbSinkingData.toString();
		}
		return null;
	}

	/**
	 * 
	 * @param sensorid
	 * @param beginTime
	 * @param endTime
	 * @return [0]- SinkingData/ydata, [1] - xdata, [2] - zdata
	 */
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
		    	if(Globals.SET_INCLINOMETER.contains(monitorData.getSensorType())){
			    	appendData(sbSinkingData, collectingTime, monitorData.getSinkingData());
			    	if(i < results.size()){ // has next
			    		sbSinkingData.append(",");
			    	}
		    		appendData(sbXData, collectingTime, monitorData.getDeviceData());
		    		appendData(sbZData, collectingTime, monitorData.getSinkingAccumulation());
			    	if(i < results.size()){ // has next
			    		sbXData.append(",");
			    	}
			    	if(i < results.size()){ // has next
			    		sbZData.append(",");
			    	}
		    	} else {
		    		appendData(sbZData, collectingTime, monitorData.getSinkingAccumulation());
			    	if(i < results.size()){ // has next
			    		sbZData.append(",");
			    	}
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
}
