package com.monitor.action.deviceapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
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
import com.monitor.core.tools.JXLReportUtil;
import com.monitor.foundation.dao.InitialValueSettingDao;
import com.monitor.foundation.dao.MonitorDao;
import com.monitor.foundation.domain.InitialValue;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

@Controller
public class InitialValueAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	public MonitorDao monitorDao;
	
	@Autowired
	public InitialValueSettingDao initialValueSettingDao;


	@RequestMapping({ "/deviceapp/initialValueModify.htm" })
	public ModelAndView appAccumulatModify(HttpServletRequest request, HttpServletResponse response, String currentPage, 
			String sensorTypeName, String monitorlineid, String sensorid) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("deviceapp/initialValueModify.html", 0, request, response);

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
		List<MonitorLine> lines = this.monitorDao.getMonitorLine();
		mv.addObject("lines", lines);
		return mv;
	}


	@RequestMapping({ "/deviceapp/initValueModify.htm" })
	public void initValueModify(HttpServletRequest request, HttpServletResponse response,String sensorid,String deviceData0,String deviceData1,String deviceData2,String time) {
		Map map = new HashMap();
		long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
		Sensor sensor = this.sensorService.getObjById(sensorIdLong);
		/*String Time = Globals.DateTimeFormat.format(System.currentTimeMillis()); 
		Date mTime = null;
		long modifyTime = 0;
		try {
			mTime = Globals.DateTimeFormat.parse(Time);
			modifyTime = mTime.getTime();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}*/
		long modifyTime = Long.parseLong(time);
		double deviceData0_ = 0;
		double deviceData1_ = 0;
		double deviceData2_ = 0;
		int status = 0;
		try {
			if(deviceData0.matches("^[\\+\\-]?[\\d]+(\\.[\\d]+)?$") && deviceData1.matches("^[\\+\\-]?[\\d]+(\\.[\\d]+)?$") && deviceData2.matches("^[\\+\\-]?[\\d]+(\\.[\\d]+)?$")){
				Long monitorlineid = sensor.getMonitorLine().getId();
				//初始值
				double data0 = CommUtil.roundDouble(Double.parseDouble(deviceData0));			
				double data1 = CommUtil.roundDouble(Double.parseDouble(deviceData1));			
				double data2	 = CommUtil.roundDouble(Double.parseDouble(deviceData2));			
				long collectingTimeSel = initialValueSettingDao.initialTime(sensor.getName());
				List<InitialValue> dataSels = initialValueSettingDao.getInitialValue(sensor.getName(),collectingTimeSel);
				double dataSel0 = CommUtil.roundDouble(dataSels.get(0).getDeviceData0());
				double dataSel1 = CommUtil.roundDouble(dataSels.get(0).getDeviceData1());
				double dataSel2 = CommUtil.roundDouble(dataSels.get(0).getDeviceData2());
				if(data0 == dataSel0 && data1 == dataSel1 && data2 == dataSel2){
					deviceData0_ = dataSel0;
					deviceData1_ = dataSel1;
					deviceData2_ = dataSel2;
					status = initialValueSettingDao.initialVlaueModify(sensor.getName(),collectingTimeSel,monitorlineid,deviceData0_,deviceData1_,deviceData2_);
				}else{
					deviceData0_ = data0;
					deviceData1_ = data1;
					deviceData2_ = data2;
					status = initialValueSettingDao.initialVlaueModify(sensor.getName(),modifyTime,monitorlineid,deviceData0_,deviceData1_,deviceData2_);
				}
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();  
		}
		List notifylist = new ArrayList();
		if(status == 1){
			map.put("add", status);
		}else{
			map.put("add", 0);
		}
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

	@RequestMapping({ "/deviceapp/load_initialValue.htm" })
	public void loadInitialValue(HttpServletRequest request, HttpServletResponse response,String monitorlineid,String sensorid) {
		long collectingTime = 0;
		List notifylist = new ArrayList();
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			if(null != sensorid && !sensorid.isEmpty()){
				long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
				Sensor sensor = this.sensorService.getObjById(sensorIdLong);
				String sensorName = sensor.getName();
				//获取最近时间
				collectingTime = initialValueSettingDao.initialTime(sensorName);
				List<InitialValue> initialValue = initialValueSettingDao.getInitialValue(sensorName,collectingTime);
				String TimeShow = Globals.DateTimeFormat.format(collectingTime);
				Map map = new HashMap();
				if(!initialValue.isEmpty()){
					map.put("id", sensor.getId());
					map.put("sensor", sensor.getName());
					map.put("deviceData0", CommUtil.roundDouble(initialValue.get(0).getDeviceData0()));
					map.put("deviceData1", CommUtil.roundDouble(initialValue.get(0).getDeviceData1()));
					map.put("deviceData2", CommUtil.roundDouble(initialValue.get(0).getDeviceData2()));
					map.put("getType", initialValue.get(0).getFirstValueGetType());
					int initSetStatus = initialValue.get(0).getInitSetStatus();
					if( initSetStatus == 1){
						map.put("initSetStatus", "等待下发");
					}else if( initSetStatus == 2){
						map.put("initSetStatus", "-");
					}else if( initSetStatus == 3){
						map.put("initSetStatus", "已确认");
					}else{
						map.put("initSetStatus", "已下发");
					}
					map.put("collectingTime", TimeShow);
					//当前初值模式
					int setcode = this.initialValueSettingDao.setModes(monitorlineid);
					if( setcode == 0){
						map.put("setModes", "时间范围模式");
					}else if( setcode == 1){
						map.put("setModes", "单个模式");
					}else if( setcode == -1){
						map.put("setModes", "未设置");
					}
				}else{
					long monitorlineidLong = Long.valueOf(Long.parseLong(monitorlineid));
					InitialValue initial = new InitialValue();
					String Time = Globals.DateTimeFormat.format(System.currentTimeMillis()); 
					Date mTime = null;
					try {
						mTime = Globals.DateTimeFormat.parse(Time);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					long collectTime = mTime.getTime();
					initial.setSensorName(sensorName);
					initial.setCollectingTime(collectTime);
					initial.setFirstValueGetType(0);
					initial.setDeviceData0(0);
					initial.setDeviceData1(0);
					initial.setDeviceData2(0);
					initial.setReserved(0);
					initial.setInitSetStatus(2);
					this.initialValueSettingDao.insertInitialValue(initial,monitorlineidLong);
					List<InitialValue> initValue = initialValueSettingDao.getInitialValue(sensorName,collectTime);
					map.put("id", sensor.getId());
					map.put("sensor", sensor.getName());
					map.put("deviceData0", CommUtil.roundDouble(initValue.get(0).getDeviceData0()));
					map.put("deviceData1", CommUtil.roundDouble(initValue.get(0).getDeviceData1()));
					map.put("deviceData2", CommUtil.roundDouble(initValue.get(0).getDeviceData2()));
					map.put("getType", initValue.get(0).getFirstValueGetType());
					int initSetStatus = initValue.get(0).getInitSetStatus();
					String timeShow = Globals.DateTimeFormat.format(collectTime);
					if( initSetStatus == 1){
						map.put("initSetStatus", "等待下发");
					}else if( initSetStatus == 2){
						map.put("initSetStatus", "-");
					}else if( initSetStatus == 3){
						map.put("initSetStatus", "已确认");
					}else{
						map.put("initSetStatus", "已下发");
					}
					map.put("collectingTime", timeShow);
					//当前初值模式
					int setcode = this.initialValueSettingDao.setModes(monitorlineid);
					if( setcode == 0){
						map.put("setModes", "时间范围模式");
					}else if( setcode == 1){
						map.put("setModes", "单个模式");
					}else if( setcode == -1){
						map.put("setModes", "未设置");
					}
				}
				notifylist.add(map);
			}else{
				List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
				String Time = Globals.DateTimeFormat.format(System.currentTimeMillis()); 
				Date mTime = null;
				try {
					mTime = Globals.DateTimeFormat.parse(Time);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long modifyTimeNow = mTime.getTime();
				for(Sensor sensor:sensors){
					String sensorName = sensor.getName();
					collectingTime = initialValueSettingDao.initialTime(sensorName);
					List<InitialValue> initialValue = initialValueSettingDao.getInitialValue(sensorName,collectingTime);
					String TimeShow = Globals.DateTimeFormat.format(collectingTime);
					Map map = new HashMap();
					if(!initialValue.isEmpty()){
						map.put("id", sensor.getId());
						map.put("sensor", sensor.getName());
						map.put("deviceData0", CommUtil.roundDouble(initialValue.get(0).getDeviceData0()));
						map.put("deviceData1", CommUtil.roundDouble(initialValue.get(0).getDeviceData1()));
						map.put("deviceData2", CommUtil.roundDouble(initialValue.get(0).getDeviceData2()));
						map.put("getType", initialValue.get(0).getFirstValueGetType());
						int initSetStatus = initialValue.get(0).getInitSetStatus();
						if( initSetStatus == 1){
							map.put("initSetStatus", "等待下发");
						}else if( initSetStatus == 2){
							map.put("initSetStatus", "-");
						}else if( initSetStatus == 3){
							map.put("initSetStatus", "已确认");
						}else{
							map.put("initSetStatus", "已下发");
						}
						map.put("collectingTime", TimeShow);
						//当前初值模式
						int setcode = this.initialValueSettingDao.setModes(monitorlineid);
						if( setcode == 0){
							map.put("setModes", "时间范围模式");
						}else if( setcode == 1){
							map.put("setModes", "单个模式");
						}else if( setcode == -1){
							map.put("setModes", "未设置");
						}
					}else{
						long monitorlineidLong = Long.valueOf(Long.parseLong(monitorlineid));
						InitialValue initial = new InitialValue();
						String initTime = Globals.DateTimeFormat.format(System.currentTimeMillis()); 
						Date iTime = null;
						try {
							iTime = Globals.DateTimeFormat.parse(initTime);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						long collectTime = iTime.getTime();
						initial.setSensorName(sensorName);
						initial.setCollectingTime(collectTime);
						initial.setFirstValueGetType(0);
						initial.setDeviceData0(0);
						initial.setDeviceData1(0);
						initial.setDeviceData2(0);
						initial.setReserved(0);
						initial.setInitSetStatus(2);
						this.initialValueSettingDao.insertInitialValue(initial,monitorlineidLong);
						List<InitialValue> initValue = initialValueSettingDao.getInitialValue(sensorName,collectTime);
						map.put("id", sensor.getId());
						map.put("sensor", sensor.getName());
						map.put("deviceData0", CommUtil.roundDouble(initValue.get(0).getDeviceData0()));
						map.put("deviceData1", CommUtil.roundDouble(initValue.get(0).getDeviceData1()));
						map.put("deviceData2", CommUtil.roundDouble(initValue.get(0).getDeviceData2()));
						map.put("getType", initValue.get(0).getFirstValueGetType());
						int initSetStatus = initValue.get(0).getInitSetStatus();
						String timeShow = Globals.DateTimeFormat.format(collectTime);
						if( initSetStatus == 1){
							map.put("initSetStatus", "等待下发");
						}else if( initSetStatus == 2){
							map.put("initSetStatus", "-");
						}else if( initSetStatus == 3){
							map.put("initSetStatus", "已确认");
						}else{
							map.put("initSetStatus", "已下发");
						}
						map.put("collectingTime", timeShow);
						//当前初值模式
						int setcode = this.initialValueSettingDao.setModes(monitorlineid);
						if( setcode == 0){
							map.put("setModes", "时间范围模式");
						}else if( setcode == 1){
							map.put("setModes", "单个模式");
						}else if( setcode == -1){
							map.put("setModes", "未设置");
						}
					}
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

	@RequestMapping({ "/deviceapp/initCollect.htm" })
	public void initCollect(HttpServletRequest request, HttpServletResponse response,
			String monitorlineidString, String beginTime, String endTime,String sampleInterval) {
		int status = 0;
		int failStatus = 0;
		String statusStr = "";
		List notifylist = new ArrayList();
		Map map = new HashMap<>();
		int exist =  this.initialValueSettingDao.collectExist();
		if(exist == -1){
			statusStr = "-1";
			map.put("status", statusStr);
			map.put("errorMsg", "已存在未完成初值采集指令!");
			notifylist.add(map);
		}else{
			if(null != monitorlineidString && !monitorlineidString.isEmpty()){
				String[] monitorlineids = monitorlineidString.split(",");
				List errorList = new ArrayList();
				for(int i=0;i<=monitorlineids.length-1;i++){
					long monitorlinidLong = Long.valueOf(Long.parseLong(monitorlineids[i]));
					long sampleIntervalLong = Long.valueOf(Long.parseLong(sampleInterval));
					Date beginTimeDate;
					Date endTimeTimeDate;
					long beginTimeLong = 0;
					long endTimeLong = 0;
					try {
						beginTimeDate = Globals.DateFormatWithHM.parse(beginTime);
						beginTimeLong = beginTimeDate.getTime();
						endTimeTimeDate = Globals.DateFormatWithHM.parse(endTime); 
						endTimeLong = endTimeTimeDate.getTime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					MonitorLine monitorline = this.monitorLineService.getObjById(monitorlinidLong);
					int insert = this.initialValueSettingDao.initialCollect(monitorlinidLong,beginTimeLong,endTimeLong,sampleIntervalLong);
					if(insert == 1){
						status++;
					}else if(insert == 0 ){
						errorList.add(monitorline.getName());
					}
				}
				if(status == monitorlineids.length){
					statusStr = "1";
				}else if(status == 0){
					statusStr = "0";
					map.put("errorMsg", errorList);
				}
				map.put("status", statusStr);
				notifylist.add(map);
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

	@RequestMapping({ "/deviceapp/exsit_initialCollet.htm" })
	public void exsit_initialCollet(HttpServletRequest request, HttpServletResponse response,
			String monitorlineid) {
		int status = 0;
		int failStatus = 0;
		String statusStr = "";
		List notifylist = new ArrayList();
		Map map = new HashMap<>();
		int exist =  this.initialValueSettingDao.collectExist(monitorlineid);
		if(exist == -1){
			statusStr = "-1";
			map.put("status", statusStr);
			map.put("errorMsg", "已存在未完成初值采集指令!");
			notifylist.add(map);
		}else{
			statusStr = "0";
			map.put("status", statusStr);
			notifylist.add(map);
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

	@RequestMapping({ "/deviceapp/initialValueSensorReport.htm" })
	public void sensorReport(HttpServletRequest request, HttpServletResponse response,
			String sensorid, String beginTime, String endTime) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return; // not login
		}
		File excelFile = null;
		ServletOutputStream out = null;
		FileInputStream inputStream = null;
		try{
			// query data
			if(null != sensorid && !sensorid.isEmpty()){
				long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));

				Date beginTimeDate = Globals.DateFormatWithHM.parse(beginTime);
				long beginTimeLong = beginTimeDate.getTime();
				Date endTimeTimeDate = Globals.DateFormatWithHM.parse(endTime); 
				long endTimeTimeLong = endTimeTimeDate.getTime();
				Sensor sensor = this.sensorService.getObjById(sensorIdLong);
				List<InitialValue> results = initialValueSettingDao.getInitialValueObj(sensor.getName(),beginTimeLong,endTimeTimeLong);
				if(null != results && !results.isEmpty()){
					SensorType sensorType = sensorTypeService.getObjByName(sensor.getSensorType());
					excelFile = this.createSensorExcelFile(sensor.getName(), results, sensorType);
				}
			}
			// create excel and return as stream
			//1. set ContentType for file, 这样设置，会自动判断下载文件类型  
			response.setContentType("multipart/form-data");  
			//2. 设置文件头：the last parameter is file name to download
			String fileName = (null == excelFile) ? "sensor_null.xls" : excelFile.getName();
			response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);  
			if(null != excelFile){
				inputStream = new FileInputStream(excelFile);  
				//3.通过response获取ServletOutputStream对象(out)  
				out = response.getOutputStream();  

				byte[] buffer = new byte[512];  
				int len;
				while ((len = inputStream.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				out.flush();  
			}
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			System.err.println("[sensorExcelReport] Exception when get sensor monitor data value for sensor:" + sensorid + ", " + e.getMessage());
			// e.printStackTrace();
		} finally {
			try{
				if(null != inputStream){
					inputStream.close();
				}
				if(null != out){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping({ "/deviceapp/initialValueLineReport.htm" })
	public void lineReport(HttpServletRequest request, HttpServletResponse response,
			String monitorlineid, String beginTime, String endTime) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return; // not login
		}
		File excelFile = null;
		ServletOutputStream out = null;
		FileInputStream inputStream = null;
		try{
			//query data
			if (null != monitorlineid && !monitorlineid.isEmpty()) {
				List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
				if(null != sensors && !sensors.isEmpty()){
					long monitorlineIdLong = Long.valueOf(Long.parseLong(monitorlineid));
					MonitorLine monitorLine = this.monitorLineService.getObjById(monitorlineIdLong);
					SensorType sensorType = sensorTypeService.getObjByName(monitorLine.getSensorType());
					String currentTime = Globals.DateTimeFormat4File.format(new Date());
					String fileName = monitorLine.getName() + "_" + currentTime + ".xls";
					excelFile = new File(Globals.REPORT_FILE_DIR + Globals.FS + fileName);
					//excelFile = new File("C:/Users/Fuwork/Desktop/" + fileName);
					WritableWorkbook workbook = createMonitorLineExcelFile(fileName);
					try {
						Date beginTimeDate = Globals.DateFormatWithHM.parse(beginTime);
						long beginTimeLong = beginTimeDate.getTime();
						Date endTimeTimeDate = Globals.DateFormatWithHM.parse(endTime); 
						long endTimeTimeLong = endTimeTimeDate.getTime();

						List<InitialValue> initTimes = initialValueSettingDao.initModifyTime(monitorlineid,beginTimeLong,endTimeTimeLong);
						int sheetIndex = 0;
						for(InitialValue init:initTimes){//获取时间段内的时间结点，通过时间结点循环查询监测线的数据
							long initTime = init.getCollectingTime();
							List<InitialValue> initvalues = initialValueSettingDao.initValueLine(monitorLine.getId(),initTime);
							String collectTime = Globals.DateTimeFormatLineReport.format(initTime);
							workbook.createSheet(collectTime, sheetIndex);
							WritableSheet excelSheet = workbook.getSheet(sheetIndex);
							addHeader4MonitorData(excelSheet, sensorType);
							if(null != initvalues && !initvalues.isEmpty()){
								for(InitialValue initValue:initvalues){
									addContent4MonitorData(excelSheet, initValue.getSensorName(), initvalues);
								}
								sheetIndex++;
							}
						}
						if(sheetIndex == 0){
							workbook.createSheet("null", 0);
						}
						workbook.write();
					} catch (IOException ioe){
						throw ioe;
					} catch (WriteException we){
						throw we;
					} catch (ParseException e) {
						System.err.println("[MonitorLineExcelReport] Exception when get monitor data value for monitorline:" + monitorlineid + ", " + e.getMessage());
						// e.printStackTrace();
					}
					finally {
						if(null != workbook){
							workbook.close();
						}
					}
				}
			}

			// create excel and return as stream
			//1. set ContentType for file, 这样设置，会自动判断下载文件类型  
			response.setContentType("multipart/form-data");  
			//2. 设置文件头：the last parameter is file name to download
			String fileName = (null == excelFile) ? "monitorline_null.xls" : excelFile.getName();
			response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);  
			if(null != excelFile){
				inputStream = new FileInputStream(excelFile);  
				//3.通过response获取ServletOutputStream对象(out)  
				out = response.getOutputStream();  

				byte[] buffer = new byte[512];  
				int len;
				while ((len = inputStream.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				out.flush();  
			}
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if(null != inputStream){
					inputStream.close();
				}
				if(null != out){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File createSensorExcelFile(String sensorName, List<InitialValue> results, SensorType sensorType) throws IOException, WriteException {
		String currentTime = Globals.DateTimeFormat4File.format(new Date());
		File file = new File(Globals.REPORT_FILE_DIR + Globals.FS + sensorName + "_" + currentTime + ".xls");
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("zh", "CN"));
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(file, wbSettings);
			workbook.createSheet(sensorName, 0);
			WritableSheet excelSheet = workbook.getSheet(0);
			addHeader4MonitorData(excelSheet, sensorType);
			addContent4MonitorData(excelSheet, sensorName, results);
			workbook.write();
		} catch (IOException ioe){
			throw ioe;
		} catch (WriteException we){
			throw we;
		} finally {
			if(null != workbook){
				workbook.close();	
			}
		}
		return file;
	}

	public WritableWorkbook createMonitorLineExcelFile(String filename) throws IOException, WriteException {
		File file = new File(Globals.REPORT_FILE_DIR + Globals.FS + filename);
		//	File file = new File("C:/Users/Fuwork/Desktop/" + filename);

		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("zh", "CN"));
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(file, wbSettings);
		} catch (IOException ioe){
			throw ioe;
		}
		return workbook;
	}

	private void addHeader4MonitorData(WritableSheet sheet, SensorType sensorType) throws WriteException {
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 20);
		sheet.setColumnView(2, 20);
		sheet.setColumnView(3, 20);
		sheet.setColumnView(4, 20);
		sheet.setColumnView(5, 20);
		sheet.setColumnView(6, 20);
		// Write a few headers
		JXLReportUtil.addHeaderLabel(sheet, 0, 0, "传感器名称");
		JXLReportUtil.addHeaderLabel(sheet, 1, 0, "初值一");
		JXLReportUtil.addHeaderLabel(sheet, 2, 0, "初值二");
		JXLReportUtil.addHeaderLabel(sheet, 3, 0, "初值三");
		JXLReportUtil.addHeaderLabel(sheet, 4, 0, "最后修改时间");
		JXLReportUtil.addHeaderLabel(sheet, 5, 0, "初值类型编号");
		JXLReportUtil.addHeaderLabel(sheet, 6, 0, "状态");
	}

	private void addContent4MonitorData(WritableSheet sheet, String sensorName, List<InitialValue> results) throws WriteException {
		if(null != results && !results.isEmpty()){
			int i = 1;
			for(InitialValue init : results){
				JXLReportUtil.addStringLabel(sheet, 0, i, init.getSensorName());
				String modifyTime = Globals.DateTimeFormat.format(new Date(init.getCollectingTime()));
				JXLReportUtil.addNumberLabel(sheet, 1, i, init.getDeviceData0());
				JXLReportUtil.addNumberLabel(sheet, 2, i, init.getDeviceData1());
				JXLReportUtil.addNumberLabel(sheet, 3, i, init.getDeviceData2());
				JXLReportUtil.addStringLabel(sheet, 4, i, modifyTime);
				JXLReportUtil.addNumberLabel(sheet, 5, i, init.getFirstValueGetType());
				int initSetStatus = init.getInitSetStatus();
				String initStatus = ""; 
				if( initSetStatus == 1){
					initStatus =  "等待下发";
				}else if( initSetStatus == 2){
					initStatus =  "-";
				}else if( initSetStatus == 3){
					initStatus =  "已确认";
				}else{
					initStatus =  "已下发";
				}
				JXLReportUtil.addStringLabel(sheet, 6, i, initStatus);
				i++;
			}
		}
	}
}
