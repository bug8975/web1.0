package com.monitor.action.deviceapp;

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

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.dao.AccumulativeDao;
import com.monitor.foundation.dao.ArchCoefficientDao;
import com.monitor.foundation.dao.MonitorDao;
import com.monitor.foundation.dao.TrendAnalysisDao;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.TrendAnalysis;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class DataArchvingAction {

	@Autowired
	private IMonitorDataService monitorDataService;
	
	@Autowired
	private IMonitorDataViewService monitorDataViewService;

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	public MonitorDao monitorDao;
	
	@Autowired
	public ArchCoefficientDao archCoefficientDao;
	
	@Autowired
	public AccumulativeDao accumulativeDao;
	
	@Autowired
	public TrendAnalysisDao trendanalysisDao;


	@RequestMapping({ "/deviceapp/dataArch.htm" })
	public ModelAndView appStatusList(HttpServletRequest request, HttpServletResponse response, String currentPage, 
			String sensorTypeName, String monitorlineid, String sensorid) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("deviceapp/dataArch.html", 0, request, response);

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
		return mv;
	}

	@RequestMapping({ "/deviceapp/timemodify.htm" })
	public void timeModify(HttpServletRequest request, HttpServletResponse response,String time) {
		
		int status = archCoefficientDao.timeModify(time);
		List notifylist = new ArrayList();
		Map map = new HashMap();
		map.put("taskTime", status);
		notifylist.add(map);
		String temp = Json.toJson(notifylist, JsonFormat.compact());
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
	
	@RequestMapping({ "/deviceapp/coeffmodify.htm" })
	public void coeffModify(HttpServletRequest request, HttpServletResponse response,String sensorName,double coefficient,double coeff) {
		
		int status = archCoefficientDao.coeffMondify(sensorName, coefficient, coeff);
		List notifylist = new ArrayList();
		Map map = new HashMap();
		map.put("coeffModify", status);
		notifylist.add(map);
		String temp = Json.toJson(notifylist, JsonFormat.compact());
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
	@RequestMapping({ "/deviceapp/load_coeff.htm" })
	public void loadCodff(HttpServletRequest request, HttpServletResponse response,String monitorlineid,String sensorid) {
		String coeff = null;
		String count;
		List notifylist = new ArrayList();
		int i = 0;
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			archCoefficientDao.timeAdd();
			if(null != sensorid && !sensorid.isEmpty()){
				long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
				Sensor sensor = this.sensorService.getObjById(sensorIdLong);
				//获取coefficient中转存的系数
				count = archCoefficientDao.coefficient(sensor.getName());
				coeff = archCoefficientDao.coeff(sensor.getName());
				String time= archCoefficientDao.time(i);
				Map map = new HashMap();
				map.put("id", sensor.getId());
				map.put("sensor", sensor.getName());
				map.put("count", count);
				map.put("coeff", coeff);
				map.put("time", time);
				notifylist.add(map);
			}else{
				List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
				for(Sensor sensor:sensors){
					int addBack =  archCoefficientDao.coeffAdd(sensor.getName());
					/*if(addBack == 0){
						Logger.printlnWithTime("The coefficients of Sensor:[" + sensor.getName()+ "]  add success!");
					}else if(addBack == 1){
						Logger.printlnWithTime("The coefficients of Sensor:[" + sensor.getName()+ "]  is existing!");
					}*/
					//获取coefficient中转存的系数
					count = archCoefficientDao.coefficient(sensor.getName());
					coeff = archCoefficientDao.coeff(sensor.getName());
					String time= archCoefficientDao.time(i);
					Map map = new HashMap();
					map.put("id", sensor.getId());
					map.put("sensor", sensor.getName());
					map.put("count", count);
					map.put("coeff", coeff);
					map.put("time", time);
					notifylist.add(map);
				}
			}
		}

		String temp = Json.toJson(notifylist, JsonFormat.compact());
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

	@RequestMapping({ "/deviceapp/dataArchOK.htm" })
	public void dataArchOK(HttpServletRequest request, HttpServletResponse response,String sensorTypeName,
			String monitorlineid, String sensorid, String beginTime,String endTime,String history) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
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
    	}
		String successMsg = null;
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			Date beginTimeDate;
			Date endTimeDate;
			long beginT = 0;
			long endT = 0;
			try {
				beginTimeDate = sdf.parse(beginTime);
				beginT = beginTimeDate.getTime();
				endTimeDate = sdf.parse(endTime);
				endT = endTimeDate.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
				Utils.setResponse(response, "范围时间转换错误！转存失败!");
				return;
			}
			List<MonitorDataView> insertData = new ArrayList<MonitorDataView>();
			List<MonitorDataView> updateData = new ArrayList<MonitorDataView>();
			//System.out.println("开始转存处理:"+Globals.DateFormatWithHM.format(System.currentTimeMillis()));
			if(null != sensorid && !sensorid.isEmpty()){
				String sensorType = sensorTypeName;
				List<TrendAnalysis> trendanalysis = new ArrayList<TrendAnalysis>();
				if(null==history || "".equals(history)){
					if(sensorType.equals("P-levelingTransducer")){
						Long monitorlineidLong = Long.parseLong(monitorlineid);
						trendanalysis= trendanalysisDao.getData(monitorlineidLong);
					}
				}
				List<MonitorData> monitordatas = this.monitorDataService.query("select obj from  MonitorData obj where obj.sensorType='"+ sensorTypeName+"' and obj.sensor.id = '"+ sensorid +"' and obj.collectingTime >='" + beginT +"' and obj.collectingTime <='" + endT +"'" , null, -1, -1);
				String time= archCoefficientDao.time();
				int checkTime = Integer.parseInt(time);
				for(MonitorData data:monitordatas){
					MonitorDataView monitorDataView = new MonitorDataView();
					monitorDataView.setAddTime(System.currentTimeMillis());
					monitorDataView.setDeleteStatus(data.isDeleteStatus());
					monitorDataView.setSensor(data.getSensor());
					monitorDataView.setMonitorLine(data.getMonitorLine());
					monitorDataView.setCollectingTime(data.getCollectingTime());
					monitorDataView.setDeviceData(data.getDeviceData());
					monitorDataView.setSinkingData(data.getSinkingData());
					monitorDataView.setSinkingOffset(data.getSinkingOffset());
					monitorDataView.setSensorType(data.getSensorType());
					long collecting = data.getCollectingTime();
					String sensorName = data.getSensor().getName();
					Sensor sensor = this.sensorService.getObjByProperty("name", sensorName);
					if(null != trendanalysis && !trendanalysis.isEmpty()){
						long maxTime = 0;
						long minTime = 0;
						long compareTimeLong = 0;
						long collectingLong = 0;
						for(TrendAnalysis trenddata:trendanalysis){
							String collectTime = Globals.DateTimeFormat.format(collecting);
							String collect_hms = collectTime.split(" ")[1];			//截取本次上传时间时分秒
							compareTimeLong = trenddata.getCompareTime();
							String compareTime = Globals.DateFormat.format(compareTimeLong);
							String compare = compareTime + " " + collect_hms;		//对应时间拼接
							if(!"".equals(compareTime) && null!= compareTime){
								try {
									Date collctingTimeDate = Globals.DateFormat.parse(collectTime.split(" ")[0]);
									collectingLong = collctingTimeDate.getTime();
									Date compareTimeDate = Globals.DateTimeFormat.parse(compare);
									maxTime = compareTimeDate.getTime();
									minTime = maxTime - trenddata.getTrendInterval();
								} catch (ParseException e) {
									Utils.setResponse(response, "时间转换错误！转存失败!");
			    					return;
								}
							}
						}
						if(collectingLong > compareTimeLong){
							List<MonitorDataView> dataview = this.monitorDataViewService.query("select obj from  MonitorDataView obj where  obj.collectingTime >='"
									+ minTime +"' and obj.collectingTime <='" + maxTime + "'and obj.sensor.id =" + sensor.getId() , null, -1, -1);
							if(null != dataview && !dataview.isEmpty()){
								double sinkingAccumulation = monitorDataView.getSinkingAccumulation();
								double compareData = dataview.get(0).getSinkingAccumulation();
								double accumlationNew =  sinkingAccumulation - compareData;
								monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(accumlationNew));
							}
						}
					}
					String ctime= sdf.format(collecting);
					String collectSubString = ctime.substring(11, 13);
					int collectSubint=Integer.parseInt(collectSubString);
					double coeff = 1;
					if(collectSubint < checkTime && collectSubint >= 00){
						//获取coefficient中转存的系数
						String count = archCoefficientDao.coefficient(data.getSensor().getName());
						coeff = Double.parseDouble(count);
						double sinkingAccumulation = data.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(sinkingAccumulation*coeff);
					}else{
						//获取coeff中转存的系数
						String count = archCoefficientDao.coeff(data.getSensor().getName());
						coeff = Double.parseDouble(count);
						double sinkingAccumulation = data.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(sinkingAccumulation*coeff);
					}
					double sumData;
					if(null!=sensor){
						//累计形变矫正系数是否需要创建,默认为0
						accumulativeDao.accumulateAdd(sensor.getMonitorLine().getId(),sensorName,collecting);
						//获取累计形变 矫正值
						long modifyTime = accumulativeDao.accumulateTime(sensorName);
						String accumulateData = accumulativeDao.accumulateData(sensorName,modifyTime);
						sumData = Double.parseDouble(accumulateData);
					}else{
						sumData = 0;
					}
					//数据调整
					if(0!=sumData){
						double sinkingAccumulation = monitorDataView.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(sinkingAccumulation + sumData));
					}
					int num = archCoefficientDao.insterOrUpdate(monitorDataView);
					if(num > 0){
						updateData.add(monitorDataView);
					}else{
						insertData.add(monitorDataView);
					}
				}
				int count_update = updateData.size();
				int count_insert = insertData.size();
				if( monitordatas.size() > 0 ){
					if(count_update > 0){
						archCoefficientDao.archivingUpdate(updateData);
					}
					if(count_insert > 0){
						archCoefficientDao.archivingInsert(insertData);
					}
					successMsg ="导入数据成功！\r\n" + "总计: "+monitordatas.size()+"条\r\n更新: "+count_update+"条\r\n增加: " + count_insert+"条";
				}else{
					Utils.setResponse(response, "查无数据！");
					return;
				}
			}else{
				String sensorType = sensorTypeName;
				List<TrendAnalysis> trendanalysis = new ArrayList<TrendAnalysis>();
				if(null==history || "".equals(history)){
					if(sensorType.equals("P-levelingTransducer")){
						Long monitorlineidLong = Long.parseLong(monitorlineid);
						trendanalysis= trendanalysisDao.getData(monitorlineidLong);
					}
				}
				List<MonitorData> monitordatas = this.monitorDataService.query("select obj from  MonitorData obj where obj.sensorType='"+ sensorTypeName +"' and obj.monitorLine.id = '"+ monitorlineid + "' and obj.collectingTime >='" + beginT +"' and obj.collectingTime <='" + endT +"'" , null, -1, -1);
				String time= archCoefficientDao.time();
				int checkTime = Integer.parseInt(time);
				for(MonitorData data:monitordatas){
					MonitorDataView monitorDataView = new MonitorDataView();
					monitorDataView.setAddTime(System.currentTimeMillis());
					monitorDataView.setDeleteStatus(data.isDeleteStatus());
					monitorDataView.setSensor(data.getSensor());
					monitorDataView.setMonitorLine(data.getMonitorLine());
					monitorDataView.setCollectingTime(data.getCollectingTime());
					monitorDataView.setDeviceData(data.getDeviceData());
					monitorDataView.setSinkingData(data.getSinkingData());
					monitorDataView.setSinkingOffset(data.getSinkingOffset());
					monitorDataView.setSensorType(data.getSensorType());
					long collecting = data.getCollectingTime();
					String sensorName = data.getSensor().getName();
					Sensor sensor = this.sensorService.getObjByProperty("name", sensorName);
					if(null != trendanalysis && !trendanalysis.isEmpty()){
						long maxTime = 0;
						long minTime = 0;
						long compareTimeLong = 0;
						long collectingLong = 0;
						for(TrendAnalysis trenddata:trendanalysis){
							String collectTime = Globals.DateTimeFormat.format(collecting);
							String collect_hms = collectTime.split(" ")[1];			//截取本次上传时间时分秒
							compareTimeLong = trenddata.getCompareTime();
							String compareTime = Globals.DateFormat.format(compareTimeLong);
							String compare = compareTime + " " + collect_hms;		//对应时间拼接
							if(!"".equals(compareTime) && null!= compareTime){
								try {
									Date collctingTimeDate = Globals.DateFormat.parse(collectTime.split(" ")[0]);
									collectingLong = collctingTimeDate.getTime();
									Date compareTimeDate = Globals.DateTimeFormat.parse(compare);
									maxTime = compareTimeDate.getTime();
									minTime = maxTime - trenddata.getTrendInterval();
								} catch (ParseException e) {
									Utils.setResponse(response, "时间转换错误！转存失败!");
			    					return;
								}
							}
						}
						if(collectingLong > compareTimeLong){
							List<MonitorDataView> dataview = this.monitorDataViewService.query("select obj from  MonitorDataView obj where  obj.collectingTime >='"
									+ minTime +"' and obj.collectingTime <='" + maxTime + "'and obj.sensor.id =" + sensor.getId() , null, -1, -1);
							if(null != dataview && !dataview.isEmpty()){
								double sinkingAccumulation = monitorDataView.getSinkingAccumulation();
								double compareData = dataview.get(0).getSinkingAccumulation();
								double accumlationNew =  sinkingAccumulation - compareData;
								monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(accumlationNew));
							}
						}
					}
					String ctime= sdf.format(collecting);
					String collectSubString = ctime.substring(11, 13);
					int collectSubint=Integer.parseInt(collectSubString);
					double coeff = 1;
					if(collectSubint < checkTime && collectSubint >= 00){
						//获取coefficient中转存的系数
						String count = archCoefficientDao.coefficient(data.getSensor().getName());
						coeff = Double.parseDouble(count);
						double sinkingAccumulation = data.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(sinkingAccumulation*coeff);
					}else{
						//获取coeff中转存的系数
						String count = archCoefficientDao.coeff(data.getSensor().getName());
						coeff = Double.parseDouble(count);
						double sinkingAccumulation = data.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(sinkingAccumulation*coeff);
					}
					double sumData;
					if(null!=sensor){
						//累计形变矫正系数是否需要创建
						accumulativeDao.accumulateAdd(sensor.getMonitorLine().getId(),sensorName,collecting);
						//获取累计形变 矫正值
						long modifyTime = accumulativeDao.accumulateTime(sensorName);
						String accumulateData = accumulativeDao.accumulateData(sensorName,modifyTime);
						sumData = Double.parseDouble(accumulateData);
					}else{
						sumData = 0;
					}
					//数据调整
					if(0 != sumData){
						double sinkingAccumulation = monitorDataView.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(sinkingAccumulation + sumData));
					}
					int num = archCoefficientDao.insterOrUpdate(monitorDataView);
					if(num > 0){
						updateData.add(monitorDataView);
					}else{
						insertData.add(monitorDataView);
					}
				}
				int count_update = updateData.size();
				int count_insert = insertData.size();
				if( monitordatas.size() > 0 ){
					if(count_update > 0){
						archCoefficientDao.archivingUpdate(updateData);
					}
					if(count_insert > 0){
						archCoefficientDao.archivingInsert(insertData);
					}
					successMsg ="导入数据成功！\r\n" + "总计: "+monitordatas.size()+"条\r\n更新: "+count_update+"条\r\n增加: " + count_insert+"条";
				}else{
					Utils.setResponse(response, "查无数据！");
					return;
				}
			}
		}else{
			Utils.setResponse(response, "监测线不能为空！");
			return;
		}
		Utils.setResponse(response, successMsg);
	}
}
