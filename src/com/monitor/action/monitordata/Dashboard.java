package com.monitor.action.monitordata;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
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
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.MonitorDataQueryObject;
import com.monitor.foundation.domain.query.SensorQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;
import antlr.StringUtils;

@Controller
public class Dashboard {
	
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

  	@RequestMapping({ "/home.htm" })
  	public ModelAndView gotohome(HttpServletRequest request, HttpServletResponse response) {
  		ModelAndView mv = new JModelAndView("home.html",
  				0, request, response);
  		return mv;
  	}
    
    //TODO:新增主页实时曲线图
  	@RequestMapping({ "/monitordata/dashboard.htm" })
  	public ModelAndView insertMonitorDataLine(HttpServletRequest request, HttpServletResponse response, String currentPage,
  			String sensorTypeName, String monitorlineid, String sensorid) {
  		User user = Utils.checkLoginedUser(request);
  		if(user == null){
  			return Utils.redirectToLoginForNonLogined(request, response);
  		}
  		ModelAndView mv = new JModelAndView("monitordata/dashboard.html",
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
    
	@RequestMapping({"/getComboxValues.htm"})
    public void getComboxValues(HttpServletRequest request,
                            HttpServletResponse response) {
        Map map = new HashMap();
        List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
        map.put("SensorTypes", sensorTypes);       
        
        Gson gs = new Gson();
        String jsonString = gs.toJson(map);

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	//实时曲线
	@RequestMapping({"/getChartValues.htm"})
    public void getChartValues(HttpServletRequest request,
                            HttpServletResponse response,String sensorTypeName, String monitorlineid) {
        Map map = new HashMap();
        List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
        map.put("SensorTypes", sensorTypes);       
        
        if(null != monitorlineid && !monitorlineid.isEmpty()){
			String[] sensorDatas = getSersorMonitorDataWithString(monitorlineid);
			if(null != sensorDatas){
				if("InclinoMeter".equals(sensorTypeName) || "FixedInclinoMeter".equals(sensorTypeName)){
					map.put("sinkingData", sensorDatas[0]);
					map.put("sinkingAccumulation", sensorDatas[1]);
					map.put("sensor_name",sensorDatas[2]);
					System.out.print(sensorDatas[2]);
					map.put("initData",sensorDatas[3]);
					map.put("collectTime",sensorDatas[4]);
					map.put("specialData", true);
				}else{
					map.put("sinkingData", sensorDatas[0]);
					map.put("sinkingAccumulation", sensorDatas[1]);
					map.put("sensor_name",sensorDatas[2]);
					map.put("initData",sensorDatas[3]);
					map.put("collectTime",sensorDatas[4]);
					map.put("hasData", true);
					System.out.print(sensorDatas[2]);
					// map.put(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_SUCCESS);
				}
			}else {
				map.put("resultcode", Integer.valueOf(10));
				map.put("resultstring", "没有数据");
				map.put("hasData", Boolean.valueOf(false));
			}
		}

        
        Gson gs = new Gson();
        String jsonString = gs.toJson(map);

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
				if(i == 1) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String cTime = sdf.format( collectingTime );
					appendData(cellectTime, cTime);
					if(i < results.size()){ // has next
//						cellectTime.append(",");
					}
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
		strBuffer.append('"');
		strBuffer.append(data);
		strBuffer.append('"');
	}

	private void appendData(StringBuffer strBuffer, double data) {
//		strBuffer.append("[");
		strBuffer.append(data);
//		strBuffer.append("]");
	}

	private void appendInitData(StringBuffer strBuffer, double data) {
		double data1 = 0;
//		strBuffer.append("[");
		strBuffer.append(data1);
//		strBuffer.append("]");
	}
	
	//累计形变单曲线
    @RequestMapping({ "/getsingledataline.htm" })
    public void getSingleDataLine(HttpServletRequest request, HttpServletResponse response, 
    		String sensorTypeName, String monitorlineid, String sensorid, String beginTime, String endTime) {
    	Map map = new HashMap();

        // set query condition to front page back
        map.put("sensorTypeName", sensorTypeName);
        map.put("monitorlineid", monitorlineid);
        map.put("sensorid", sensorid);
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);

        // for condition selecting
		List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
        map.put("sensorTypes", sensorTypes);
        if(null != sensorTypeName && !sensorTypeName.isEmpty()){
            List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
            map.put("monitorLines", monitorLines);
        }
        // for condition selecting
        if(null != monitorlineid && !monitorlineid.isEmpty()){
            List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.base=false and obj.monitorLine.id="+monitorlineid, null, -1, -1);
            map.put("sensors", sensors);
        }
        
        //query data
        if(null != sensorid && !sensorid.isEmpty()){
        	long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
        	Sensor sensor = this.sensorService.getObjById(sensorIdLong);
        	SensorType sensorType = this.sensorTypeService.getObjByName(sensor.getSensorType());
            String[] monitorDatas = getSensorMonitorDataWithString(sensorIdLong, beginTime, endTime);
            if(null != monitorDatas){
    		    map.put("sinkingData", monitorDatas[0]); // SinkingData/Y
    		    map.put("xdata", monitorDatas[1]); // DeviceData/X
    		    map.put("zdata", monitorDatas[2]); // SinkingAccumulation/Z
    		    map.put("sensorType", sensorType.getDisplayName());
    		    map.put("unit", sensorType.getUnit());
    		    if(Globals.SET_INCLINOMETER.contains(sensorType.getName())){
    		    	map.put("isInclinometer", true);
    		    }
    		    map.put("hasData", true);
    		    // map.put(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_SUCCESS);
            } else {
    		    map.put("hasData", false);
            	map.put(Globals.WEB_RESULTCODE, Globals.WEB_RESULTCODE_NODATA);
            	map.put(Globals.WEB_RESULTSTRING, Globals.WEB_RESULTSTRING_NODATA);
            }
        }


        
        Gson gs = new Gson();
        String jsonString = gs.toJson(map);

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
