package com.monitor.action.deviceapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import com.monitor.core.tools.Logger;
import com.monitor.foundation.dao.AccumulativeDao;
import com.monitor.foundation.dao.MonitorDao;
import com.monitor.foundation.domain.Accumulative;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IMonitorDataService;
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
public class AccumulativeAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	public MonitorDao monitorDao;
	
	@Autowired
	public AccumulativeDao accumulativeDao;


	@RequestMapping({ "/deviceapp/sumAccumulativeData.htm" })
	public ModelAndView appAccumulatModify(HttpServletRequest request, HttpServletResponse response, String currentPage, 
			String sensorTypeName, String monitorlineid, String sensorid) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("deviceapp/sumAccumulativeData.html", 0, request, response);

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

	@RequestMapping({ "/deviceapp/ipRecord.htm" })
	public void ipRecord(HttpServletRequest request, HttpServletResponse response) {
		String modifyTime = Globals.DateTimeFormat.format(System.currentTimeMillis()); 
		Date mTime = null;
		long modifyT = 0;
		try {
			mTime = Globals.DateTimeFormat.parse(modifyTime);
			modifyT = mTime.getTime();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		/*		InetAddress address = null;
		try {
			//获取本机ip地址
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}  
		String ipAddress = address.getHostAddress(); */
		String ipAddress = request.getHeader("x-forwarded-for");  
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
			ipAddress = request.getHeader("Proxy-Client-IP");  
		}  
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
			ipAddress = request.getHeader("WL-Proxy-Client-IP");  
		}  
		if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
			ipAddress = request.getRemoteAddr();  
			if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
				//根据网卡取本机配置的IP  
				InetAddress inet=null;  
				try {  
					inet = InetAddress.getLocalHost();  
				} catch (UnknownHostException e) {  
					e.printStackTrace();  
				}  
				ipAddress= inet.getHostAddress();  
			}  
		}  
		//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
		if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
			if(ipAddress.indexOf(",")>0){  
				ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
			}  
		}  
		accumulativeDao.ipRecord(ipAddress, modifyT);
	}

	@RequestMapping({ "/deviceapp/sumDatamodify.htm" })
	public void sumDataModify(HttpServletRequest request, HttpServletResponse response,String sensorid,String sumData) {
		Map map = new HashMap();
		long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
		Sensor sensor = this.sensorService.getObjById(sensorIdLong);
		String Time = Globals.DateTimeFormat.format(System.currentTimeMillis()); 
		Date mTime = null;
		long modifyTime = 0;
		try {
			mTime = Globals.DateTimeFormat.parse(Time);
			modifyTime = mTime.getTime();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		double sum_Data = 0;
		int status = 0;
		try {
			if(sumData.matches("^[\\+\\-]?[\\d]+(\\.[\\d]+)?$")){
				Long monitorlineid = sensor.getMonitorLine().getId();
				double sumDataInt = CommUtil.roundDouble(Double.parseDouble(sumData));			//传入数值
				long modifyTimeSel = accumulativeDao.accumulateTime(sensor.getName());
				String sumDataSel = accumulativeDao.accumulateData(sensor.getName(),modifyTimeSel);	
				double sumDataSelInt = CommUtil.roundDouble(Double.parseDouble(sumDataSel));	//表中已经存在的值
				if(sumDataInt == sumDataSelInt){
					status = 1;
				}else{
					sum_Data = sumDataInt;
					status = accumulativeDao.accumulateModify(monitorlineid,sensor.getName(), sum_Data, modifyTime);
				}
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
	        Utils.setResponse(response, "保存失败！");
		}

		List notifylist = new ArrayList();
		map.put("sumDatamodify", status);
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

	@RequestMapping({ "/deviceapp/load_sumData.htm" })
	public void loadCodff(HttpServletRequest request, HttpServletResponse response,String monitorlineid,String sensorid) {
		String sumData = null;
		long modifyTime = 0;
		long first_modifyTime = 0;
		List notifylist = new ArrayList();
		int i = 0;
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			if(null != sensorid && !sensorid.isEmpty()){
				long sensorIdLong = Long.valueOf(Long.parseLong(sensorid));
				Sensor sensor = this.sensorService.getObjById(sensorIdLong);
				//获取矫正系数
				modifyTime = accumulativeDao.accumulateTime(sensor.getName());
				sumData = accumulativeDao.accumulateData(sensor.getName(),modifyTime);
				String modifyTimeShow = Globals.DateTimeFormat.format(modifyTime);
				first_modifyTime = accumulativeDao.accumulateFirstTime(sensor.getName());				//首次修改时间
				String firstModifyTimeShow = Globals.DateTimeFormat.format(first_modifyTime);
				Map map = new HashMap();
				map.put("id", sensor.getId());
				map.put("sensor", sensor.getName());
				map.put("sumData", CommUtil.roundDouble(Double.parseDouble(sumData)));
				map.put("modifyTime", modifyTimeShow);
				map.put("first_modifyTime", firstModifyTimeShow);
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
					//累计形变矫正系数是否需要创建,默认为0
					long monitorline_id = sensor.getMonitorLine().getId(); 
					int accumulateBack =  accumulativeDao.accumulateAdd(monitorline_id,sensorName,modifyTimeNow);
					if(accumulateBack == 0){
						Logger.printlnWithTime("The accumulative of Sensor:[" + sensorName+ "]  add success!");
					}else if(accumulateBack == 1){
						Logger.printlnWithTime("The accumulative of Sensor:[" + sensorName+ "]  is existing!");
					}
					//获取矫正系数
					modifyTime = accumulativeDao.accumulateTime(sensor.getName());
					sumData = accumulativeDao.accumulateData(sensor.getName(),modifyTime);
					String modifyTimeShow = Globals.DateTimeFormat.format(modifyTime);
					first_modifyTime = accumulativeDao.accumulateFirstTime(sensor.getName());				//首次修改时间
					String firstModifyTimeShow = Globals.DateTimeFormat.format(first_modifyTime);
					Map map = new HashMap();
					map.put("id", sensor.getId());
					map.put("sensor", sensor.getName());
					map.put("sumData", CommUtil.roundDouble(Double.parseDouble(sumData)));
					map.put("modifyTime", modifyTimeShow);
					map.put("first_modifyTime", firstModifyTimeShow);
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

	@RequestMapping({ "/picreport/accumulativeSensorReport.htm" })
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
				List<Accumulative> results = accumulativeDao.accumulative(sensor.getName(),beginTimeLong,endTimeTimeLong);
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

	@RequestMapping({ "/picreport/accumulativeLineReport.htm" })
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

						List<Accumulative> monitorTimes = accumulativeDao.disTime(monitorlineid,beginTimeLong,endTimeTimeLong);
						int sheetIndex = 0;
						for(Accumulative accl:monitorTimes){//获取时间段内的时间结点，通过时间结点循环查询监测线的数据
							long monitorT = (long)accl.getModifyTime();
							List<Accumulative> monitors = accumulativeDao.accumulativeLine(monitorLine.getId(),monitorT);
							String collectTime = Globals.DateTimeFormatLineReport.format(monitorT);
							workbook.createSheet(collectTime, sheetIndex);
							WritableSheet excelSheet = workbook.getSheet(sheetIndex);
							addHeader4MonitorData(excelSheet, sensorType);
							if(null != monitors && !monitors.isEmpty()){
								for(Accumulative al:monitors){
									addContent4MonitorData(excelSheet, al.getSensorName(), monitors);
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

	public File createSensorExcelFile(String sensorName, List<Accumulative> results, SensorType sensorType) throws IOException, WriteException {
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
		// Write a few headers
		JXLReportUtil.addHeaderLabel(sheet, 0, 0, "传感器名称");
		JXLReportUtil.addHeaderLabel(sheet, 1, 0, "当前调整系数");
		JXLReportUtil.addHeaderLabel(sheet, 2, 0, "本次修改时间");
		JXLReportUtil.addHeaderLabel(sheet, 3, 0, "上次调整系数");
		JXLReportUtil.addHeaderLabel(sheet, 4, 0, "上次修改时间");
		JXLReportUtil.addHeaderLabel(sheet, 5, 0, "本次增加系数");
	}

	private void addContent4MonitorData(WritableSheet sheet, String sensorName, List<Accumulative> results) throws WriteException {
		if(null != results && !results.isEmpty()){
			int i = 1;
			for(Accumulative al : results){
				JXLReportUtil.addStringLabel(sheet, 0, i, al.getSensorName());
				String modifyTime = Globals.DateTimeFormat.format(new Date(al.getModifyTime()));
				String his_modifyTime = Globals.DateTimeFormat.format(new Date(al.getHis_modifyTime()));
				JXLReportUtil.addNumberLabel(sheet, 1, i, al.getSumData());
				JXLReportUtil.addStringLabel(sheet, 2, i, modifyTime);
				JXLReportUtil.addNumberLabel(sheet, 3, i, al.getHis_sumData());
				JXLReportUtil.addStringLabel(sheet, 4, i, his_modifyTime);
				JXLReportUtil.addNumberLabel(sheet, 5, i, al.getMdata());
				i++;
			}
		}
	}
}
