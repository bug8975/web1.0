package com.monitor.action.monitordata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.JXLReportUtil;
import com.monitor.core.tools.Logger;
import com.monitor.core.tools.database.PublicMethod;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorDataTrendAnalysis;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.SensorQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class DataAnalysisAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	private IMonitorDataService monitorDataService;

	@Autowired
	private IMonitorDataViewService monitorDataViewService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private PublicMethod publicMethod;

	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	String sensorTypeNameNorm = "a";
	String monitorlineidNorm = "b";
	String sensoridNorm = "c";
	String beginTimeNorm = "d";
	String endTimeNorm = "e";
	String hourSegmentNorm = "-1";
	ArrayList trendList = new ArrayList<>();
	//    @SecurityMapping(title = "数据查看", value = "/monitordata/monitordatalist.htm*", rtype = "monitordata", rname = "监测数据", rcode = "monitordata", rgroup = "监测数据")
	@RequestMapping({ "/monitordata/sensortrendanalysislist.htm" })
	public ModelAndView List(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String sensorTypeName, String monitorlineid, String sensorid, String beginTime, String endTime, String hourSegment) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("monitordata/sensortrendanalysislist.html",
				0, request, response);
		mv.addObject(Globals.MV_ATTR_USERROLE, user.getUserRole()); // control the user operation based on role. added on 20161009
		mv.addObject("SensorTypes", sensorTypeService.getSensorTypeDisplayNames()); // for display in UI
		mv.addObject("SensorUnits", sensorTypeService.getSensorUnits()); // for display in UI

		// return query condition
		mv.addObject("sensorTypeName", sensorTypeName);
		mv.addObject("monitorlineid", monitorlineid);
		mv.addObject("sensorid", sensorid);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("hourSegment", hourSegment);

		// for condition selecting
		//        List<SensorType> sensorTypes = this.sensorTypeService.getUsingSensorTypes();
		//        mv.addObject("sensorTypes", sensorTypes);
		List<SensorType> sensorTypesForAnalysis = new ArrayList<SensorType>(3);
		/*SensorType cSensorType = this.sensorTypeService.getObjByName(Globals.SENSORTYPE_CTRANSDUCER);
		if(null != cSensorType && cSensorType.getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(cSensorType);
		}
		SensorType iSensorType = this.sensorTypeService.getObjByName(Globals.SENSORTYPE_ITRANSDUCER);
		if(null != iSensorType && iSensorType.getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(iSensorType);
		}
		SensorType pSensorType = this.sensorTypeService.getObjByName(Globals.SENSORTYPE_PTRANSDUCER);
		if(null != pSensorType && pSensorType.getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(pSensorType);
		}*/
		List<SensorType> cSensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name = '"+Globals.SENSORTYPE_CTRANSDUCER+"'", null);
		if(null != cSensorType && cSensorType.get(0).getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(cSensorType.get(0));
		}
		List<SensorType> iSensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name = '"+Globals.SENSORTYPE_ITRANSDUCER+"'", null);
		if(null != iSensorType && iSensorType.get(0).getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(iSensorType.get(0));
		}
		List<SensorType> pSensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name = '"+Globals.SENSORTYPE_PTRANSDUCER+"'", null);
		if(null != pSensorType && pSensorType.get(0).getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(pSensorType.get(0));
		}
		mv.addObject("sensorTypes", sensorTypesForAnalysis);
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
			mv.addObject("monitorLines", monitorLines);
		}
		// for condition selecting
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
			mv.addObject("sensors", sensors);
		}

		long beginTimeInMs = -1;
		long endTimeInMs = -1;
		long beginTimeLong = -1;
		long endTimeLong = -1;
		Double hourSegmentInt = 4.0; // default hour segment is 4
		if(null != beginTime && !beginTime.isEmpty()){
			Date beginTimeDate;
			Date beginAllDate;
			try {
				beginTimeDate = dateFormat.parse(beginTime);
				beginTimeInMs = beginTimeDate.getTime();
				beginAllDate = Globals.DateFormatYMD.parse(beginTime);
				beginTimeLong = beginAllDate.getTime();
			} catch (ParseException e) {
				mv.addObject(Globals.WEB_RESULTSTRING, "时间格式不正确!");
				return mv;
			}
		} else {
			mv.addObject(Globals.WEB_RESULTSTRING, "时间不能为空!");
			return mv; // query condition is not complete
		}
		if(null != endTime && !endTime.isEmpty()){
			Date endTimeDate;
			Date endAllDate;
			try {
				endTimeDate = dateFormat.parse(endTime);
				endTimeInMs = endTimeDate.getTime();
				endAllDate = Globals.DateFormatYMD.parse(endTime);
				endTimeLong = endAllDate.getTime();
			} catch (ParseException e) {
				mv.addObject(Globals.WEB_RESULTSTRING, "时间格式不正确!");
				return mv;
			}
		} else {
			mv.addObject(Globals.WEB_RESULTSTRING, "时间不能为空!");
			return mv; // query condition is not complete
		}
		if(null != hourSegment && !hourSegment.isEmpty()){
			hourSegmentInt = Double.valueOf(hourSegment);
		}

		// Query object
		SensorQueryObject qo = new SensorQueryObject(currentPage, mv, "name", "ASC");
		qo.setPageSize(Integer.valueOf(15));
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			qo.addQuery("obj.sensorType",
					new SysMap("sensorType", sensorTypeName), "=");
		} else {
			qo.addQuery(" and sensorType in " + Globals.SENSORTYPE_CONDITION_IN, null);
		}
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			qo.addQuery("obj.monitorLine.id",
					new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
		}
		if(null != sensorid && !sensorid.isEmpty()){
			qo.addQuery("obj.id",
					new SysMap("sensorid", Long.valueOf(Long.parseLong(sensorid))), "=");
		}

		IPageList pList = this.sensorService.list(qo);
		List<Sensor> sensorList = pList.getResult();
		int size = (null != sensorList) ? sensorList.size() : 0;

		List<SensorDataTrendAnalysis> trendDataList = new ArrayList<SensorDataTrendAnalysis>(size);
		if(null != sensorList){
			try {
				List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj", null, -1, -1);
				Long monitorLine_id = null; 
				long beginInterval = 0;
				long endInterval = 0;
				for(MonitorLine monitor:monitorLines){
					monitorLine_id = monitor.getId();
					if(null != beginTime && !beginTime.isEmpty()){
						List<Object> monitorTime = this.monitorDataViewService.queryTime("select distinct obj.collectingTime from  MonitorDataView obj where obj.collectingTime >='"
								+ (beginTimeInMs-24*3600*1000) +"' and obj.collectingTime <='" + beginTimeInMs + "'and monitorLine_id =" + monitorLine_id , null, -1, -1);
						long lastCollectTime = 0;
						for(Object m : monitorTime){
							long CollectTime = (long) m;
							beginInterval = CollectTime-lastCollectTime;
							lastCollectTime = (long) m;
						}
					}
					if(null != endTime && !endTime.isEmpty()){
						List<Object> monitorTime = this.monitorDataViewService.queryTime("select distinct obj.collectingTime from  MonitorDataView obj where obj.collectingTime >='"
								+ (endTimeInMs-24*3600*1000) +"' and obj.collectingTime <='" + endTimeInMs + "'and monitorLine_id =" + monitorLine_id , null, -1, -1);
						long lastCollectTime = 0;
						for(Object m : monitorTime){
							long CollectTime = (long) m;
							endInterval = CollectTime-lastCollectTime;
							lastCollectTime = (long) m;
						}
					}
					for(Sensor sensor: sensorList){
						if(sensor.getMonitorLine().getId() == monitorLine_id){
							SensorDataTrendAnalysis dataTrendAnalysis = createDataTrendAnalysis(sensor, beginTimeInMs, endTimeInMs, beginInterval, endInterval, hourSegmentInt);
							trendDataList.add(dataTrendAnalysis);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("objs", trendDataList); //  change the showing data
		return mv;
	}


	@RequestMapping({ "/monitordata/sensortrendanalysisreport.htm" })
	public void getTrendAnalysisExcelReport(HttpServletRequest request, HttpServletResponse response,
			String sensorTypeName, String monitorlineid, String sensorid, String beginTime, String endTime, String hourSegment) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return; // not login
		}

		// for condition selecting
		List<SensorType> sensorTypesForAnalysis = new ArrayList<SensorType>(3);
		SensorType cSensorType = this.sensorTypeService.getObjByName(Globals.SENSORTYPE_CTRANSDUCER);
		if(null != cSensorType && cSensorType.getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(cSensorType);
		}
		SensorType iSensorType = this.sensorTypeService.getObjByName(Globals.SENSORTYPE_ITRANSDUCER);
		if(null != iSensorType && iSensorType.getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(iSensorType);
		}
		SensorType pSensorType = this.sensorTypeService.getObjByName(Globals.SENSORTYPE_PTRANSDUCER);
		if(null != pSensorType && pSensorType.getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAnalysis.add(pSensorType);
		}

		long beginTimeInMs = -1;
		long endTimeInMs = -1;
		long beginTimeLong = -1;
		long endTimeLong = -1;
		Double hourSegmentInt = 4.0; // default hour segment is 4
		if(null != beginTime && !beginTime.isEmpty()){
			Date beginTimeDate;
			Date beginAllDate;
			try {
				beginTimeDate = dateFormat.parse(beginTime);
				beginTimeInMs = beginTimeDate.getTime();
				beginAllDate = Globals.DateFormatYMD.parse(beginTime);
				beginTimeLong = beginAllDate.getTime();
			} catch (ParseException e) {
				Logger.printlnWithTime("--Error about trend analysis excel report: time format is wrong!");
				return;
			}
		} else {
			Logger.printlnWithTime("--Error about trend analysis excel report: time cannot be null!");
			return;
		}
		if(null != hourSegment && !hourSegment.isEmpty()){
			hourSegmentInt = Double.valueOf(hourSegment);
		}
		if(null != endTime && !endTime.isEmpty()){
			Date endTimeDate;
			Date endAllDate;
			try {
				endTimeDate = dateFormat.parse(endTime);
				endTimeInMs = endTimeDate.getTime();
				endAllDate = Globals.DateFormatYMD.parse(endTime);
				endTimeLong = endAllDate.getTime();
			} catch (ParseException e) {
				Logger.printlnWithTime("--Error about trend analysis excel report: time format is wrong!");
				return;
			}
		} else {
			Logger.printlnWithTime("--Error about trend analysis excel report: time cannot be null!");
			return;
		}

		// Query object
		///// mv is useless, just to satisfy the parameter limit of method SensorQueryObject()
		ModelAndView mv = new JModelAndView("monitordata/sensortrendanalysisreport.html", 0, request, response); 
		SensorQueryObject qo = new SensorQueryObject("1", mv, "name", "ASC");
		qo.setPageSize(Integer.valueOf(Integer.MAX_VALUE));
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			qo.addQuery("obj.sensorType",
					new SysMap("sensorType", sensorTypeName), "=");
		} else {
			qo.addQuery(" and sensorType in " + Globals.SENSORTYPE_CONDITION_IN, null);
		}
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			qo.addQuery("obj.monitorLine.id",
					new SysMap("monitorlineid", Long.valueOf(Long.parseLong(monitorlineid))), "=");
		}
		if(null != sensorid && !sensorid.isEmpty()){
			qo.addQuery("obj.id",
					new SysMap("sensorid", Long.valueOf(Long.parseLong(sensorid))), "=");
		}

		IPageList pList = this.sensorService.list(qo);
		List<Sensor> sensorList = pList.getResult();
		int size = (null != sensorList) ? sensorList.size() : 0;

		List<SensorDataTrendAnalysis> trendDataList = new ArrayList<SensorDataTrendAnalysis>(size);
		if(null != sensorList){
			try {
				List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj", null, -1, -1);
				Long monitorLine_id = null; 
				long beginInterval = 0;
				long endInterval = 0;
				for(MonitorLine monitor:monitorLines){
					monitorLine_id = monitor.getId();
					if(null != beginTime && !beginTime.isEmpty()){
						List<Object> monitorTime = this.monitorDataViewService.queryTime("select distinct obj.collectingTime from  MonitorDataView obj where obj.collectingTime >='"
								+ (beginTimeInMs-24*3600*1000) +"' and obj.collectingTime <='" + beginTimeInMs + "'and monitorLine_id =" + monitorLine_id , null, -1, -1);
						long lastCollectTime = 0;
						for(Object m : monitorTime){
							long CollectTime = (long) m;
							beginInterval = CollectTime-lastCollectTime;
							lastCollectTime = (long) m;
						}
					}
					if(null != endTime && !endTime.isEmpty()){
						List<Object> monitorTime = this.monitorDataViewService.queryTime("select distinct obj.collectingTime from  MonitorDataView obj where obj.collectingTime >='"
								+ (endTimeInMs-24*3600*1000) +"' and obj.collectingTime <='" + endTimeInMs + "'and monitorLine_id =" + monitorLine_id , null, -1, -1);
						long lastCollectTime = 0;
						for(Object m : monitorTime){
							long CollectTime = (long) m;
							endInterval = CollectTime-lastCollectTime;
							lastCollectTime = (long) m;
						}
					}
					for(Sensor sensor: sensorList){
						if(sensor.getMonitorLine().getId() == monitorLine_id){
							SensorDataTrendAnalysis dataTrendAnalysis = createDataTrendAnalysis(sensor, beginTimeInMs, endTimeInMs, beginInterval, endInterval, hourSegmentInt);
							trendDataList.add(dataTrendAnalysis);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		File excelFile = null;
		ServletOutputStream out = null;
		FileInputStream inputStream = null;
		try{
			if(null != trendDataList && !trendDataList.isEmpty()){
				excelFile = this.createTrendAnalysisExcelFile(trendDataList,hourSegment);
			}

			// create excel and return as stream
			//1. set ContentType for file, 这样设置，会自动判断下载文件类型  
			response.setContentType("multipart/form-data");  
			//2. 设置文件头：the last parameter is file name to download
			String fileName = (null == excelFile) ? "analysisreport_null.xls" : excelFile.getName();
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

	private File createTrendAnalysisExcelFile(List<SensorDataTrendAnalysis> trendDataList,String hourSegment) throws IOException, WriteException {
		// TODO del 
		List<SensorDataTrendAnalysis> trendDataList1 = new ArrayList<SensorDataTrendAnalysis>(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		trendDataList1.addAll(trendDataList);
		String currentTime = Globals.DateTimeFormat4File.format(new Date());
		File file = new File(Globals.REPORT_FILE_DIR + Globals.FS + "analysisreport_" + currentTime + ".xls");
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("zh", "CN"));
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(file, wbSettings);
			workbook.createSheet("analysisreport", 0);
			WritableSheet excelSheet = workbook.getSheet(0);
			int dataSize = trendDataList.size();
			int blockSize = dataSize / 42; // 42 = 14 * 3
			int lastBlockCount = dataSize % 42;
			if(lastBlockCount != 0){
				blockSize++;
			}
			for (int blockNumber=0; blockNumber<blockSize; blockNumber++){
				addHeader(excelSheet);
				addHeaderSecond(excelSheet,hourSegment);
				addHeader4OneBlock(excelSheet, blockNumber * 15); // 15 is data+header row count in one block
				addData4OneBlock(excelSheet, trendDataList, blockNumber);
			}
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

	private void addHeader(WritableSheet sheet) throws RowsExceededException, WriteException {
		WritableFont wf = new WritableFont(WritableFont.TIMES,10,WritableFont.BOLD,false,
				UnderlineStyle.NO_UNDERLINE);
		WritableCellFormat fontFormat=new WritableCellFormat(wf);   
		fontFormat.setAlignment(jxl.format.Alignment.CENTRE);//设置为水平居中  
		fontFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//设置为垂直居中  
		fontFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
		fontFormat.setWrap(true);
		sheet.mergeCells(0,0,7,0); 
		sheet.setRowView(0,600);
		sheet.addCell(new Label(0,0,"静力水准仪趋势分析报表",fontFormat));
	}

	private void addHeaderSecond(WritableSheet sheet,String hourSegment) throws RowsExceededException, WriteException {
		WritableFont fontSecond = new WritableFont(WritableFont.TIMES,10, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE);
		WritableCellFormat formatSecondTitle = new WritableCellFormat(fontSecond);
		formatSecondTitle.setAlignment(jxl.format.Alignment.CENTRE);//设置为水平居中  
		formatSecondTitle.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//设置为垂直居中
		formatSecondTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
		formatSecondTitle.setWrap(true);
		String time = Globals.DateTimeFormat.format(new Date(System.currentTimeMillis()));

		//合并单元格
		sheet.mergeCells(0, 1, 3, 1);
		sheet.mergeCells(4, 1, 7, 1);
		sheet.setRowView(1, 400);
		sheet.addCell(new Label(0,1,"导出日期: "+time,formatSecondTitle));
		sheet.addCell(new Label(4,1,"时间段:"+hourSegment,formatSecondTitle));
	}


	private void addHeader4OneBlock(WritableSheet sheet, int headerRow) throws WriteException {
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 15);
		sheet.setColumnView(2, 25);
		sheet.setColumnView(3, 12);
		sheet.setColumnView(4, 25);
		sheet.setColumnView(5, 12);
		sheet.setColumnView(6, 12);
		sheet.setColumnView(7, 12);
		// Write a few headers
		JXLReportUtil.addHeaderLabel(sheet, 0, 2, "传感器类型"); // headerRow is start with 0
		JXLReportUtil.addHeaderLabel(sheet, 1, 2, "传感器名称");
		JXLReportUtil.addHeaderLabel(sheet, 2, 2, "起始时间");
		JXLReportUtil.addHeaderLabel(sheet, 3, 2, "起始值");
		JXLReportUtil.addHeaderLabel(sheet, 4, 2, "采样时间");
		JXLReportUtil.addHeaderLabel(sheet, 5, 2, "采样值");
		JXLReportUtil.addHeaderLabel(sheet, 6, 2, "沉降量");
		JXLReportUtil.addHeaderLabel(sheet, 7, 2, "单位");
	}

	private void addData4OneBlock(WritableSheet sheet, List<SensorDataTrendAnalysis> trendDataList, int blockNumber) throws WriteException {
		int i = 3;
		for(SensorDataTrendAnalysis sensorDataTrendAnalysis:trendDataList){
			SensorType sensorTypes = this.sensorTypeService.getObjByName(sensorDataTrendAnalysis.getSensorType());
			String startTimeS = Globals.DateTimeFormat.format(new Date(sensorDataTrendAnalysis.getBaseStartTime()));
			String simpleTimeS = Globals.DateTimeFormat.format(new Date(sensorDataTrendAnalysis.getSampleStartTime()));

			JXLReportUtil.addStringLabel(sheet, 0, i, sensorTypes.getDisplayName());
			JXLReportUtil.addStringLabel(sheet, 1, i, sensorDataTrendAnalysis.getSensorName());
			JXLReportUtil.addStringLabel(sheet, 2, i, startTimeS);
			JXLReportUtil.addNumberLabel(sheet, 3, i, sensorDataTrendAnalysis.getBaseAverageValue());
			JXLReportUtil.addStringLabel(sheet, 4, i, simpleTimeS);
			JXLReportUtil.addNumberLabel(sheet, 5, i, sensorDataTrendAnalysis.getSampleAverageValue());
			JXLReportUtil.addNumberLabel(sheet, 6, i, sensorDataTrendAnalysis.getSinkingData());
			JXLReportUtil.addStringLabel(sheet, 7, i, sensorDataTrendAnalysis.getUnit());
			i++;
		}
	}


	private SensorDataTrendAnalysis createDataTrendAnalysis(Sensor sensor, long baseTime, long sampleTime, long beginInterval, long endInterval, Double hourSegmentInt) throws Exception {
		SensorDataTrendAnalysis dataTrendAnalysis = new SensorDataTrendAnalysis();
		try {
			setAverageMonitorData(sensor.getId(), baseTime, hourSegmentInt, beginInterval, dataTrendAnalysis, true);
			setAverageMonitorData(sensor.getId(), sampleTime, hourSegmentInt, endInterval, dataTrendAnalysis, false);
			dataTrendAnalysis.setSinkingData(CommUtil.roundDouble(dataTrendAnalysis.getSampleAverageValue() - dataTrendAnalysis.getBaseAverageValue()));
			dataTrendAnalysis.setSensorName(sensor.getName());
			dataTrendAnalysis.setSensorType(sensor.getSensorType());
			dataTrendAnalysis.setBase(sensor.isBase());
			dataTrendAnalysis.setUnit(sensorTypeService.getObjByName(sensor.getSensorType()).getUnit());    		
		} catch (Exception e) {
			throw e;
		}
		return dataTrendAnalysis;
	}

	private void setAverageMonitorData(long sensorId, long time, Double hourSegmentInt,long interval, SensorDataTrendAnalysis dataTrendAnalysis, boolean isBase) throws Exception{
		//    	String sql = "select sum(obj.deviceData) from monitor_data obj where obj.sensor_id=:sensorId and collectingTime>:timeSegmentBegain and collectingTime<:timeSegmentEnd";
		/*		StringBuffer sqlSB = new StringBuffer("select sum(obj.sinkingAccumulation), count(obj.id), min(obj.collectingTime) from monitor_dataview obj where obj.sensor_id=");
		sqlSB.append(sensorId);
		sqlSB.append(" and collectingTime>");
		if(hourSegmentInt == 0){
			sqlSB.append(time - interval);
		}else{
			sqlSB.append(time - hourSegmentInt * 3600 * 1000);
		}
		sqlSB.append(" and collectingTime<");
		sqlSB.append(time);*/
		StringBuffer sqlSB;
		if(hourSegmentInt == 0){
			sqlSB = new StringBuffer("select sum(obj.sinkingAccumulation), count(obj.id), min(obj.collectingTime) from monitor_dataview obj where obj.sensor_id=");
			sqlSB.append(sensorId);
			sqlSB.append(" and collectingTime>");
			sqlSB.append(time - interval);
			sqlSB.append(" and collectingTime<");
			sqlSB.append(time);
		}else{
			sqlSB = new StringBuffer("select sum(obj.sinkingAccumulation), count(obj.id), min(obj.collectingTime) from (select * from monitor_dataview where sensor_id=");
			sqlSB.append(sensorId);
			sqlSB.append(" and collectingTime>");
			sqlSB.append(time - hourSegmentInt * 3600 * 1000);
			sqlSB.append(" and collectingTime<");
			sqlSB.append(time);
			int limitSize =  jdbcTemplate.queryForInt("SELECT COUNT(*) from monitor_dataview obj where obj.sensor_id= '"+sensorId+"' and collectingTime < "+time+" and collectingTime > " + (time - (hourSegmentInt * 3600 * 1000)));
			if(limitSize == 0){
				limitSize = 2;
			}
			//			sqlSB.append(" order by ABS(sinkingAccumulation) asc limit 0,"+(limitSize-1)+") as obj");//去除最大值（绝对值）
			sqlSB.append(" order by sinkingAccumulation desc limit 0,"+(limitSize-1)+") as obj");
		}
		try {
			ResultSet resultSet = publicMethod.queryResult(sqlSB.toString());
			if (resultSet.next()){ // get one row
				double summaryValue = resultSet.getDouble(1);
				int summarySensorCount = resultSet.getInt(2);
				double averageValue = 0;
				if(summarySensorCount != 0){
					averageValue = CommUtil.roundDouble(summaryValue / summarySensorCount);
				}
				if(isBase){
					dataTrendAnalysis.setBaseAverageValue(averageValue);
					dataTrendAnalysis.setBaseStartTime(resultSet.getLong(3));
				} else {
					dataTrendAnalysis.setSampleAverageValue(averageValue);
					dataTrendAnalysis.setSampleStartTime(resultSet.getLong(3));
				}
			}
		} catch (Exception e) {
			System.out.println("Exception when get average value for sensor:" + sensorId + ", " + e.getMessage());
			throw e;
		}
	}

}
