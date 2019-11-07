package com.monitor.action.picreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.JXLReportUtil;
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
import com.sun.java.swing.plaf.motif.resources.motif;
import com.sun.org.apache.xerces.internal.impl.dv.xs.MonthDayDV;

@Controller
public class MonitorDataReportAction {
	// public variables
	//	private WritableCellFormat timesBoldCellFormat; TODO del
	//	private WritableCellFormat times;

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


	@RequestMapping({ "/picreport/report.htm" })
	public ModelAndView report(HttpServletRequest request, HttpServletResponse response,
			String sensorTypeNameForSensorReport, String monitorlineidforsensor, String sensorid, String beginTime, String endTime) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("picreport/report.html", 0, request, response);
		// set query condition to front page back
		mv.addObject("sensorTypeNameForSensorReport", sensorTypeNameForSensorReport);
		mv.addObject("monitorlineidforsensor", monitorlineidforsensor);
		mv.addObject("sensorid", sensorid);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);

		// for condition selecting
		List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);
		mv.addObject("sensorTypes", sensorTypes);
		if(null != sensorTypeNameForSensorReport && !sensorTypeNameForSensorReport.isEmpty()){
			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj", null, -1, -1);
			mv.addObject("monitorLines", monitorLines);
		}
		// for condition selecting
		if(null != monitorlineidforsensor && !monitorlineidforsensor.isEmpty()){
			List<Sensor> sensors = this.sensorService
					.query("select obj from Sensor obj where obj.base=false and obj.monitorLine.id="+monitorlineidforsensor, null, -1, -1);
			mv.addObject("sensors", sensors);
		}

		mv.addObject(Globals.MV_ATTR_USERROLE, user.getUserRole()); // control the user operation based on role
		return mv;
	}


	/**
	 * a jquery request
	 * 
	 * @param request
	 * @param response
	 * @param sensorid
	 * @param beginTime
	 * @param endTime
	 */
	@RequestMapping({ "/picreport/sensorreport.htm" })
	public void sensorExcelReport(HttpServletRequest request, HttpServletResponse response,
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

				List<MonitorDataView> results = com.monitor.action.picreport.Utils
						.querySensorMonitorData(dataViewService, sensorIdLong, beginTimeLong, endTimeTimeLong);
				if(null != results && !results.isEmpty()){
					Sensor sensor = this.sensorService.getObjById(sensorIdLong);
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


	/**
	 * a jquery request
	 * 
	 * @param request
	 * @param response
	 * @param sensorid
	 * @param beginTime
	 * @param endTime
	 */
	@RequestMapping({ "/picreport/monitorlinereport.htm" })
	public void monitorlineExcelReport(HttpServletRequest request, HttpServletResponse response,
			String monitorlineid, String beginTime, String endTime, String startTime, String stopTime) {
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
						//截取时间段
						int startSeconds = 0;
						int stopSeconds = 0;
						if(startTime!=null && !startTime.isEmpty() && stopTime!=null && !stopTime.isEmpty()){
							int startHour = Integer.parseInt(startTime.substring(0, 2));
							int startMin = Integer.parseInt(startTime.substring(3, 5));
							int stopHour = Integer.parseInt(stopTime.substring(0, 2));
							int stopMin = Integer.parseInt(stopTime.substring(3, 5));
							startSeconds = (startHour * 3600) + (startMin * 60);
							stopSeconds = (stopHour * 3600) + (stopMin * 60);
						}

						List<Object> monitorTimes = this.dataViewService.queryTime("select distinct obj.collectingTime from  MonitorDataView obj where obj.collectingTime >='"
								+ beginTimeLong +"' and obj.collectingTime <='" + endTimeTimeLong + "'and monitorLine_id =" + monitorlineid , null, -1, -1);

						int sheetIndex = 0;
						for(Object monitorTime :monitorTimes){//获取时间段内的时间结点，通过时间结点循环查询监测线的数据
							long monitorT = (long) monitorTime;
							String monitorDateString = Globals.DateFormatWithHM.format(monitorT);
							//历史数据的时间（小时分）
							int monitorHour = Integer.parseInt(monitorDateString.substring(11, 13));
							int monitorMin = Integer.parseInt(monitorDateString.substring(14, 16));
							int monitorSeconds = (monitorHour * 3600) + (monitorMin * 60);
							if(startTime!=null && !startTime.isEmpty() && stopTime!=null && !stopTime.isEmpty()){
								if(monitorSeconds > startSeconds && monitorSeconds < stopSeconds){
									List<MonitorDataView> monitors = com.monitor.action.picreport.Utils
											.queryMonitorData(dataViewService, monitorlineIdLong, monitorT);
									String collectTime = Globals.DateTimeFormatLineReport.format(new Date((long) monitorTime));
									workbook.createSheet(collectTime, sheetIndex);
									WritableSheet excelSheet = workbook.getSheet(sheetIndex);
									addHeader4MonitorData(excelSheet, sensorType);
									if(null != monitors && !monitors.isEmpty()){
										for(MonitorDataView monitor:monitors){
											addContent4MonitorData(excelSheet, monitor.getSensor().getName(), monitors);
										}
										sheetIndex++;
									}
								}
							}else{
								List<MonitorDataView> monitors = com.monitor.action.picreport.Utils
										.queryMonitorData(dataViewService, monitorlineIdLong, monitorT);
								String collectTime = Globals.DateTimeFormatLineReport.format(new Date((long) monitorTime));
								workbook.createSheet(collectTime, sheetIndex);
								WritableSheet excelSheet = workbook.getSheet(sheetIndex);
								addHeader4MonitorData(excelSheet, sensorType);
								if(null != monitors && !monitors.isEmpty()){
									for(MonitorDataView monitor:monitors){
										addContent4MonitorData(excelSheet, monitor.getSensor().getName(), monitors);
									}
									sheetIndex++;
								}
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

	@RequestMapping({ "/picreport/monitorlinereport2.htm" })
	public void monitorlineExcelReport2(HttpServletRequest request, HttpServletResponse response,
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


						int sheetIndex = 0;
						for(Sensor sensor : sensors){
							// 20160926 do no create sheet for base point
							// 20161015 if user is viewer, do no create sheet for base point, else create sheet for base point
							if(user.getUserRole().equals(Globals.USERROLE_VIEWER) && sensor.isBase()) { 
								continue;
							}
							List<MonitorDataView> monitorDatas = com.monitor.action.picreport.Utils
									.querySensorMonitorData(dataViewService, sensor.getId(), beginTimeLong, endTimeTimeLong);

							workbook.createSheet(sensor.getName(), sheetIndex);
							WritableSheet excelSheet = workbook.getSheet(sheetIndex);
							addHeader4MonitorData(excelSheet, sensorType);
							if(null != monitorDatas && !monitorDatas.isEmpty()){
								addContent4MonitorData(excelSheet, sensor.getName(), monitorDatas);
							}
							sheetIndex++;
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

	/**
	 * a jquery request
	 * 
	 * @param request
	 * @param response
	 * @param sensorid
	 * @param beginTime
	 * @param endTime
	 */
	@RequestMapping({ "/picreport/sinkingstatisticreport.htm" })
	public void sinkingOffsetStatisticReport(HttpServletRequest request, HttpServletResponse response,
			String beginTime, String endTime) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return; // not login
		}
		File excelFile = null;
		ServletOutputStream out = null;
		FileInputStream inputStream = null;
		try{
			List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.base=false order by obj.name asc", null, -1, -1);
			if(null != sensors){
				double[] sinkingOffsetDatas = new double[sensors.size()];
				int i = 0;
				for(Sensor sensor : sensors){
					sinkingOffsetDatas[i] = this.querySinkingOffsetStatData(sensor.getId(), beginTime, endTime);
					i++;
				}
				excelFile = this.createSinkingOffsetStatisticExcelFile(sensors, sinkingOffsetDatas, beginTime, endTime);
			}

			// create excel and return as stream
			//1. set ContentType for file, 这样设置，会自动判断下载文件类型  
			response.setContentType("multipart/form-data");  
			//2. 设置文件头：the last parameter is file name to download
			String fileName = (null == excelFile) ? "sinkingstat_null.xls" : excelFile.getName();
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


	private double querySinkingOffsetStatData(long sensorid, String beginTime, String endTime) {
		try {
			Date beginTimeDate = Globals.DateFormat.parse(beginTime);
			long beginTimeLong = beginTimeDate.getTime();
			Date endTimeTimeDate = Globals.DateFormat.parse(endTime); 
			long endTimeTimeLong = endTimeTimeDate.getTime();
			endTimeTimeLong = endTimeTimeLong + (23*3600+59*60+59)*1000;
			return this.dataViewService.getSinkingOffsetStat(sensorid, beginTimeLong, endTimeTimeLong);
		} catch (ParseException e) {
			e.printStackTrace();
			return Sensor.INIT_MONITORVALUE;
		}
	}


	public File createSensorExcelFile(String sensorName, List<MonitorDataView> results, SensorType sensorType) throws IOException, WriteException {
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

	public File createSinkingOffsetStatisticExcelFile(List<Sensor> sensors, double[] sinkingOffsetDatas, String beginTime, String endTime) 
			throws IOException, WriteException {
		String currentTime = Globals.DateTimeFormat4File.format(new Date());
		File file = new File(Globals.REPORT_FILE_DIR + Globals.FS + "Sinkingstat_" + currentTime + ".xls");
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("zh", "CN"));
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(file, wbSettings);
			workbook.createSheet("沉降量统计", 0);
			WritableSheet excelSheet = workbook.getSheet(0);
			addHeader4SinkingOffsetStat(excelSheet, beginTime, endTime);
			addContent4SinkingOffsetStat(excelSheet, sensors, sinkingOffsetDatas);
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

	//	private void initReport() throws WriteException {
	//		File dir = new File(REPORTDIR);
	//		if(!dir.exists()){
	//			dir.mkdirs();
	//		}
	//		if(null == times){
	//			// Lets create a times font
	//			WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
	//			// Define the cell format
	//			times = new WritableCellFormat(times10pt);
	//			//left alignment
	//			times.setAlignment(Alignment.LEFT);
	//			// Lets automatically wrap the cells
	//			times.setWrap(true);
	//		}
	//		if(null == timesBoldCellFormat){
	//			// create create a bold font with unterlines
	//			WritableFont times10ptBoldUnderline = new WritableFont(
	//					WritableFont.TIMES, 10, WritableFont.BOLD, false,
	//					UnderlineStyle.NO_UNDERLINE);
	//			timesBoldCellFormat = new WritableCellFormat(times10ptBoldUnderline);
	//			timesBoldCellFormat.setAlignment(Alignment.LEFT);
	//			// Lets automatically wrap the cells
	//			timesBoldCellFormat.setWrap(true);
	//
	//			CellView cv = new CellView();
	//			cv.setFormat(times);
	//			cv.setFormat(timesBoldCellFormat);
	//			cv.setAutosize(true);
	//		}
	//	}

	private void addContent4MonitorData(WritableSheet sheet, String sensorName, List<MonitorDataView> results) throws WriteException {
		if(null != results && !results.isEmpty()){
			int i = 1;
			for(MonitorDataView monitorData : results){
				if("InclinoMeter".equals(monitorData.getSensorType()) || "FixedInclinoMeter".equals(monitorData.getSensorType())){
					NumberFormat nf=NumberFormat.getNumberInstance() ;
					DecimalFormat df = new DecimalFormat("0.0000");
					nf.setMinimumIntegerDigits(3);//保留三位小数
					/*					//将角度转为弧度
					double device= Math.toRadians(monitorData.getDeviceData());
					double sinKing= Math.toRadians(monitorData.getSinkingData());
					//计算tan值
					double tanX = Double.parseDouble(df.format(Math.tan(device)));
					double tanY = Double.parseDouble(df.format(Math.tan(sinKing)));*/

					JXLReportUtil.addStringLabel(sheet, 0, i, monitorData.getMonitorLine().getName());
					JXLReportUtil.addStringLabel(sheet, 1, i, monitorData.getSensor().getName());
					String collectingTimeS = Globals.DateTimeFormat.format(new Date(monitorData.getCollectingTime()));
					JXLReportUtil.addStringLabel(sheet, 2, i, collectingTimeS);
					JXLReportUtil.addNumberLabel(sheet, 3, i, monitorData.getDeviceData());
					JXLReportUtil.addNumberLabel(sheet, 4, i, monitorData.getSinkingData());
					JXLReportUtil.addNumberLabel(sheet, 5, i, monitorData.getSinkingAccumulation());
					//					JXLReportUtil.addNumberLabel(sheet, 6, i, tanX);
					//					JXLReportUtil.addNumberLabel(sheet, 7, i, tanY);
					i++;

				}else if("AccelerateMeter".equals(monitorData.getSensorType())){
					JXLReportUtil.addStringLabel(sheet, 0, i, monitorData.getMonitorLine().getName());
					JXLReportUtil.addStringLabel(sheet, 1, i, monitorData.getSensor().getName());
					String collectingTimeS = Globals.DateTimeFormat.format(new Date(monitorData.getCollectingTime()));
					JXLReportUtil.addStringLabel(sheet, 2, i, collectingTimeS);
					JXLReportUtil.addNumberLabel(sheet, 3, i, monitorData.getDeviceData());
					JXLReportUtil.addNumberLabel(sheet, 4, i, monitorData.getSinkingData());
					JXLReportUtil.addNumberLabel(sheet, 5, i, monitorData.getSinkingAccumulation());
					/*					JXLReportUtil.addNumberLabel(sheet, 6, i, monitorData.getDeviceData()*5);
					JXLReportUtil.addNumberLabel(sheet, 7, i, monitorData.getSinkingData()*5);
					JXLReportUtil.addNumberLabel(sheet, 8, i, monitorData.getSinkingAccumulation()*5);*/
					i++;
				}else{
					JXLReportUtil.addStringLabel(sheet, 0, i, monitorData.getMonitorLine().getName());
					JXLReportUtil.addStringLabel(sheet, 1, i, monitorData.getSensor().getName());
					String collectingTimeS = Globals.DateTimeFormat.format(new Date(monitorData.getCollectingTime()));
					JXLReportUtil.addStringLabel(sheet, 2, i, collectingTimeS);
					JXLReportUtil.addNumberLabel(sheet, 3, i, monitorData.getDeviceData());
					JXLReportUtil.addNumberLabel(sheet, 4, i, monitorData.getSinkingData());
					JXLReportUtil.addNumberLabel(sheet, 5, i, monitorData.getSinkingAccumulation());
					i++;
				}
			}
		}
	}

	private void addContent4SinkingOffsetStat(WritableSheet sheet, List<Sensor> sensors, double[] sinkingOffsetDatas) throws WriteException {
		if(null != sensors && !sensors.isEmpty()){
			int i = 0;
			for(Sensor sensor : sensors){
				SensorType sensorType = this.sensorTypeService.getObjByName(sensor.getSensorType());
				JXLReportUtil.addStringLabel(sheet, 0, i+3, sensor.getMonitorLine().getName());
				JXLReportUtil.addStringLabel(sheet, 1, i+3, sensor.getName());
				//		    	JXLReportUtil.addNumberLabel(sheet, 2, i+3, sensor.getLastSinkingData());
				JXLReportUtil.addStringLabel(sheet, 2, i+3, sensor.getLastSinkingData() + "("+sensorType.getUnit() + ")");
				if(sinkingOffsetDatas[i] != Sensor.INIT_MONITORVALUE){
					//			    	JXLReportUtil.addNumberLabel(sheet, 3, i+3, sinkingOffsetDatas[i]);
					JXLReportUtil.addStringLabel(sheet, 3, i+3, CommUtil.roundDouble(sinkingOffsetDatas[i]) + "("+sensorType.getUnit() + ")");
				} else {
					JXLReportUtil.addStringLabel(sheet, 3, i+3, "");
				}
				i++;
			}
		}
	}

	private void addHeader4MonitorData(WritableSheet sheet, SensorType sensorType) throws WriteException {
		if( "InclinoMeter".equals(sensorType.getName()) || "FixedInclinoMeter".equals(sensorType.getName()) ){
			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 20);
			// Write a few headers
			JXLReportUtil.addHeaderLabel(sheet, 0, 0, "监测线名称");
			JXLReportUtil.addHeaderLabel(sheet, 1, 0, "传感器名称");
			JXLReportUtil.addHeaderLabel(sheet, 2, 0, "采集时间");
			JXLReportUtil.addHeaderLabel(sheet, 3, 0, "X(" + sensorType.getUnit() + ")");
			JXLReportUtil.addHeaderLabel(sheet, 4, 0, "Y(" + sensorType.getUnit() + ")");
			JXLReportUtil.addHeaderLabel(sheet, 5, 0, "Z(" + sensorType.getUnit() + ")");
			//			JXLReportUtil.addHeaderLabel(sheet, 6, 0, "tan(X)");
			//			JXLReportUtil.addHeaderLabel(sheet, 7, 0, "tan(Y)");
		}else if("AccelerateMeter".equals(sensorType.getName())){
			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 20);
			sheet.setColumnView(8, 20);
			// Write a few headers
			JXLReportUtil.addHeaderLabel(sheet, 0, 0, "监测线名称");
			JXLReportUtil.addHeaderLabel(sheet, 1, 0, "传感器名称");
			JXLReportUtil.addHeaderLabel(sheet, 2, 0, "采集时间");
			JXLReportUtil.addHeaderLabel(sheet, 3, 0, "X(" + sensorType.getUnit() + ")");
			JXLReportUtil.addHeaderLabel(sheet, 4, 0, "Y(" + sensorType.getUnit() + ")");
			JXLReportUtil.addHeaderLabel(sheet, 5, 0, "Z(" + sensorType.getUnit() + ")");
			/*			JXLReportUtil.addHeaderLabel(sheet, 6, 0, "dx");
			JXLReportUtil.addHeaderLabel(sheet, 7, 0, "dy");
			JXLReportUtil.addHeaderLabel(sheet, 8, 0, "dz");*/
		}else{
			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			// Write a few headers
			JXLReportUtil.addHeaderLabel(sheet, 0, 0, "监测线名称");
			JXLReportUtil.addHeaderLabel(sheet, 1, 0, "传感器名称");
			JXLReportUtil.addHeaderLabel(sheet, 2, 0, "采集时间");
			//			addHeaderLabel(sheet, 3, 0, "沉降量(" + sensorType.getUnit() + ")");
			//			addHeaderLabel(sheet, 4, 0, "仪器值(" + sensorType.getUnit() + ")");
			JXLReportUtil.addHeaderLabel(sheet, 3, 0, "监测点读数(" + sensorType.getUnit() + ")");
			JXLReportUtil.addHeaderLabel(sheet, 4, 0, "即时形变(" + sensorType.getUnit() + ")");
			JXLReportUtil.addHeaderLabel(sheet, 5, 0, "累计形变(" + sensorType.getUnit() + ")");
		}
	}

	private void addHeader4SinkingOffsetStat(WritableSheet sheet, String beginTime, String endTime) throws WriteException {
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 20);
		sheet.setColumnView(2, 20);
		sheet.setColumnView(3, 20);
		// Write a few headers
		// (1) header for date
		JXLReportUtil.addStringLabel(sheet, 0, 0, "统计生成时间");
		JXLReportUtil.addStringLabel(sheet, 1, 0, Globals.DateFormat.format(new Date()));
		JXLReportUtil.addStringLabel(sheet, 0, 1, "起始时间");
		JXLReportUtil.addStringLabel(sheet, 1, 1, beginTime);
		JXLReportUtil.addStringLabel(sheet, 2, 1, "结束时间");
		JXLReportUtil.addStringLabel(sheet, 3, 1, endTime);

		// (2) header for data
		JXLReportUtil.addHeaderLabel(sheet, 0, 2, "监测线名称");
		JXLReportUtil.addHeaderLabel(sheet, 1, 2, "传感器名称");
		JXLReportUtil.addHeaderLabel(sheet, 2, 2, "当前沉降量");
		JXLReportUtil.addHeaderLabel(sheet, 3, 2, "累积沉降量");
	}
}
