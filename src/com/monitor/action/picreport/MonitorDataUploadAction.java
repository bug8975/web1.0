package com.monitor.action.picreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.jspsmart.upload.SmartUpload;
import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Logger;
import com.monitor.foundation.dao.MonitorDao;
import com.monitor.foundation.dao.MonitorLineDAO;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataImport;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.domain.query.SensorQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

import jcifs.dcerpc.msrpc.samr;

@Controller
public class MonitorDataUploadAction implements ServletConfigAware,ServletContextAware{  

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	public MonitorDao monitorDao;

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorDataService monitorDataService;

	private ServletContext servletContext;  
	@Override  
	public void setServletContext(ServletContext arg0) {  
		this.servletContext = arg0;  
	}  
	private ServletConfig servletConfig;  
	@Override  
	public void setServletConfig(ServletConfig arg0) {  
		this.servletConfig = arg0;  
	}  

	@RequestMapping({ "/picreport/import.htm" })
	public ModelAndView Import(HttpServletRequest request, HttpServletResponse response,String currentPage,HttpSession session) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		String realPath = "";
		importDataCancel(request,response,session,realPath);
		ModelAndView mv = new JModelAndView("picreport/import.html", 0, request, response);

		return mv;
	}

	com.jspsmart.upload.File initDataSave;
	@RequestMapping(value = "/picreport/importData.htm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView toimportdata(HttpServletRequest request, HttpSession session,HttpServletResponse response,String currentPage) {
		ModelAndView mv = new JModelAndView("picreport/import.html", 0, request, response);
		try {        
			SmartUpload smartupload = new SmartUpload();
			smartupload.initialize(servletConfig, request, response);
			smartupload.setAllowedFilesList("sql,txt,xls");
			smartupload.upload();
			currentPage = smartupload.getRequest().getParameter("currentPage");
			com.jspsmart.upload.File uploadFile = smartupload.getFiles().getFile(0);
			if(uploadFile.isMissing()){
				if(initDataSave == null || "".equals(initDataSave)){
					mv = new JModelAndView("picreport/import.html", 0, request, response);
					mv.addObject("errorMsg", "请选择数据文件！");
					return mv;
				}else{
					uploadFile = initDataSave;
				}
			}else{
				initDataSave = uploadFile;
			}
			String realPath = "";
			if (! uploadFile.isMissing()) {
				String extensionName = uploadFile.getFileExt();
				String uploadPostFix = "/upload/docs/data/";
				realPath = session.getServletContext().getRealPath(uploadPostFix); 
				String fileName = UUID.randomUUID().toString().replace("-", "");
				uploadPostFix = uploadPostFix + fileName + "." + extensionName;
				uploadFile.saveAs(uploadPostFix);

				String realPathFileName = session.getServletContext().getRealPath(uploadPostFix);                

				File newfile = new File(realPathFileName);
				if (newfile.exists()) {
					//System.out.println("extension name: " + extensionName);

					if (extensionName.equals("xls")) {                        
						InputStream is = new FileInputStream(newfile);
						HSSFWorkbook wb = new HSSFWorkbook(is);
						HSSFSheet sheet = wb.getSheetAt(0);

						List<MonitorDataImport> dataList = new ArrayList<MonitorDataImport>();
						ArrayList showlist = new ArrayList();
						List sensorList = new ArrayList();
						List lineList = new ArrayList();

						//System.out.println("row count: " + sheet.getLastRowNum());
						for (int i=1; i<=sheet.getLastRowNum(); i++) {
							Row row = sheet.getRow(i);

							// 若首列内为空, 则认为空行，结束解析并退出
							String DisplayName = null;
							String SensorName = null;;
							String MonitorLineName = null;;
							//String collectTime = row.getCell(3).toString().trim();
							String collectTime = null;;
							String deviceData = null;;
							String sinkingData = null;;
							String sinkingAccumulation = null;;
							try {
								DisplayName = row.getCell(0).toString().trim();
								SensorName = row.getCell(1).toString().trim();
								MonitorLineName = row.getCell(2).toString().trim();
								Cell cell = row.getCell(3);
								if(cell != null){
									switch (cell.getCellType()) {
									case Cell.CELL_TYPE_STRING:
										collectTime = cell.getStringCellValue();
										break;
									case Cell.CELL_TYPE_BOOLEAN:
										Boolean val1 = cell.getBooleanCellValue();
										collectTime = val1.toString();
										break;
									case Cell.CELL_TYPE_NUMERIC:
										if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {   
											Date theDate = cell.getDateCellValue();
											SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											collectTime = dff.format(theDate);
										}else{
											DecimalFormat df = new DecimalFormat("0");  
											collectTime = df.format(cell.getNumericCellValue());
										}
										break;
									case Cell.CELL_TYPE_BLANK:
										break;
									default:
										mv.addObject("errorMsg","传感器【"+ SensorName + "】时间格式错误,请检查并输入正确的格式:2000-01-01 00:00:00");
										return mv;
									}
								}
								deviceData = row.getCell(4).toString().trim();
								sinkingData = row.getCell(5).toString().trim();
								sinkingAccumulation = row.getCell(6).toString().trim();
							} catch (Exception e1) {
								if (dataList.size() > 0) {            
									mv = new JModelAndView("picreport/import.html", 0, request, response);
									request.setAttribute("importdatalist", dataList);
									session.setAttribute("importdatalist", dataList);

									SensorQueryObject qo = new SensorQueryObject(currentPage, mv, "name", "ASC");
									qo.setPageSize(Integer.valueOf(16));
									PageUtils pageUtils = new PageUtils(showlist, qo.getPageSize(), currentPage);
									mv.addObject("realPath", realPath);
									mv.addObject("showData", pageUtils.getArrayList());
									mv.addObject("totalPage", new Integer(pageUtils.getPageCount()));
									mv.addObject("pageSize", Integer.valueOf(pageUtils.getPageSize()));
									mv.addObject("rows", new Integer(pageUtils.getRowCount()));
									mv.addObject("gotoPageFormHTML",CommUtil.showPageFormHtml(pageUtils.getCurrentPage(),pageUtils.getPageCount()));
									mv.addObject("currentPage", new Integer(pageUtils.getCurrentPage()));
									mv.addObject("gotoPageHTML", CommUtil.showPageHtml("", "",pageUtils.getCurrentPage(), pageUtils.getPageCount()));
									is.close();
									sheet = null;
									wb = null;
									return mv;
								}else{
									e1.printStackTrace();
								}
							}
							if (null==DisplayName && "".equals(DisplayName)) {
								//System.out.println("类型");
								mv.addObject("errorMsg", SensorName + "传感器类型不能为空");
								return mv;
							}else if (SensorName.equals("")) {
								//System.out.println("传感器名称");
								mv.addObject("errorMsg", SensorName + "传感器名称不能为空");
								return mv;
							}else if (MonitorLineName.equals("")) {
								//System.out.println("监测线名称");
								mv.addObject("errorMsg",SensorName + "监测线名称不能为空");
								return mv;
							}else if (collectTime.equals("")) {
								//System.out.println("采样时间");
								mv.addObject("errorMsg",SensorName +  "采样时间不能为空");
								return mv;
							}else if (deviceData.equals("")) {
								//System.out.println("监测点");
								mv.addObject("errorMsg",SensorName + "监测点度数不能为空");
								return mv;
							}else if (sinkingData.equals("")) {
								//System.out.println("即时形变");
								mv.addObject("errorMsg", SensorName + "即时形变不能为空");
								return mv;
							}else if (sinkingAccumulation.equals("")) {
								//System.out.println("累计形变");
								mv.addObject("errorMsg", SensorName + "累计形变不能为空");
								return mv;
							}else{
								Pattern a=Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$"); 
								Matcher b=a.matcher(collectTime);
								Long collectingTime= null;
								if( b.matches()) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date time = null;
									try {
										time = sdf.parse(collectTime);
									} catch (Exception e) {
										mv.addObject("errorMsg","传感器【"+ SensorName + "】时间为【"+collectTime+"】,格式错误!请检查并输入正确的格式:2000-01-01 00:00:00");
										return mv;
									}
									collectingTime= time.getTime();
									List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0 and obj.displayName='"+DisplayName+"'", null);
									String SensorTypeName = null;
									if( sensorTypes.isEmpty() ){
										importDataCancel(request,response,session,realPath);
										mv.addObject("errorMsg", "传感器类型【"+ DisplayName + "】不存在");
										return mv;
									}else{
										for(SensorType s:sensorTypes){
											SensorTypeName = s.getName();
										}
										Long monitorLineId = null;
										if( MonitorLineName.matches("^[A-Z]+[-]+[0-9]+$")){
											List<MonitorLine> monitorLines =  this.monitorLineService.query("select obj from MonitorLine obj where obj.name ='"+MonitorLineName+"'", null, -1, -1);
											if( monitorLines.isEmpty() ){
												/*MonitorLine monitorline = new MonitorLine();
												monitorline.setName(MonitorLineName);
												monitorline.setHostName("10.29.222.71");
												monitorline.setPort(2);
												monitorline.setSinkingthresholdl1(6);
												monitorline.setSinkingthresholdl2(8);
												monitorline.setSinkingthresholdl3(10);
												monitorline.setSensorType(SensorTypeName);
												this.monitorLineService.save(monitorline);
												List<MonitorLine> addmonitorLines =  this.monitorLineService.query("select obj from MonitorLine obj where obj.name ='"+MonitorLineName+"'", null, -1, -1);
												for(MonitorLine addM:addmonitorLines){
													monitorLineId = addM.getId();
													MonitorLine delMonitorLine = new MonitorLine();
													delMonitorLine.setId(monitorLineId);
													lineList.add(delMonitorLine);
													session.setAttribute("lineList", lineList);
												}*/
												mv.addObject("errorMsg","监测线【"+ MonitorLineName + "】不存在");
												return mv;
											}else{
												for(MonitorLine oldM:monitorLines){
													if(SensorTypeName.equals(oldM.getSensorType())){
														monitorLineId = oldM.getId();
													}else{
														mv.addObject("errorMsg","监测线【"+ MonitorLineName + "】已经存在，但传感器类型不符");
														return mv;
													}
												}
											}
											Long sensorId = null;
											if(null!=monitorLineId && !"".equals(monitorLineId)){
												List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.name ='"+ SensorName +"'", null, -1, -1);
												//												if( SensorName.matches("^[A-Z]+[-]+[0-9]+[-]+[0-9]+$")){
												if( SensorName.matches("^"+ MonitorLineName +"[-]+[0-9]+$")){
													if( sensors.isEmpty() ){
														/*MonitorLine oldMonitorLines = this.monitorLineService.getObjByProperty("name", MonitorLineName);
														Sensor sensor = new Sensor();
														sensor.setName(SensorName);
														sensor.setMonitorLine(oldMonitorLines);
														sensor.setSensorType(SensorTypeName);
														sensor.setBase(false);
														sensor.setAlarmLevel(Globals.ALARMLEVEL_NORMAL);
														this.sensorService.save(sensor);
														List<Sensor> addsensor= this.sensorService.query("select obj from Sensor obj where obj.name ='"+ SensorName +"'", null, -1, -1);
														for(Sensor addS:addsensor){
															sensorId = addS.getId();
															Sensor delSensor = new Sensor();
															delSensor.setId(sensorId);
															sensorList.add(delSensor);
															session.setAttribute("sensorList", sensorList);
														}*/
														mv.addObject("errorMsg", "传感器【"+SensorName+"】不存在");
													}else{
														for(Sensor sensor:sensors){
															sensorId = sensor.getId();
														}
													}
												}else{
													importDataCancel(request,response,session,realPath);
													mv.addObject("errorMsg", "传感器【"+SensorName+"】名称格式错误，正确格式如：AA-00-01(监测线名称-数字)");
													return mv;
												}
												if(sensorId!=null&&!"".equals(sensorId)){
													MonitorDataImport importdata = new MonitorDataImport();
													importdata.setAddTime(System.currentTimeMillis());
													importdata.setDeleteStatus(false);
													importdata.setSenser_id(sensorId);
													importdata.setMonitorLine_id(monitorLineId);
													importdata.setCollectingTime(collectingTime);
													importdata.setDeviceData(CommUtil.roundDouble(Double.parseDouble(deviceData.toString())));
													importdata.setSinkingData(CommUtil.roundDouble(Double.parseDouble(sinkingData.toString())));
													importdata.setSinkingOffset(0);
													importdata.setSensorType(SensorTypeName);
													importdata.setSinkingAccumulation(CommUtil.roundDouble(Double.parseDouble(sinkingAccumulation.toString())));

													Map map = new HashMap();
													map.put("sensorTypeName",DisplayName);
													map.put("sensorName",SensorName);
													map.put("monitorLineName",MonitorLineName);
													map.put("collectingTime",collectTime);
													map.put("deviceData",CommUtil.roundDouble(Double.parseDouble(deviceData)));
													map.put("sinkingData",CommUtil.roundDouble(Double.parseDouble(sinkingData)));
													map.put("sinkingAccumulation",CommUtil.roundDouble(Double.parseDouble(sinkingAccumulation)));

													showlist.add(map);
													dataList.add(importdata);
												}else{
													importDataCancel(request,response,session,realPath);
													mv.addObject("errorMsg","传感器【"+ SensorName + "】不存在");
													return mv;
												}
											}else{
												importDataCancel(request,response,session,realPath);
												mv.addObject("errorMsg", "监测线【"+MonitorLineName+"】不存在");
												return mv;
											}
										}else{
											mv.addObject("errorMsg", "监测线【"+MonitorLineName+"】名称格式错误，请检查并输入正确的格式如：AA-00(大写英文字母-数字)");
											return mv;
										}
									}
								} else {
									mv.addObject("errorMsg","传感器【"+ SensorName + "】时间格式错误,请检查并输入正确的格式:2000-01-01 00:00:00");
									return mv;
								}
							}
						}
						if (dataList.size() > 0) {            
							mv = new JModelAndView("picreport/import.html", 0, request, response);

							request.setAttribute("importdatalist", dataList);
							session.setAttribute("importdatalist", dataList);
							SensorQueryObject qo = new SensorQueryObject(currentPage, mv, "name", "ASC");
							qo.setPageSize(Integer.valueOf(16));
							PageUtils pageUtils = new PageUtils(showlist, qo.getPageSize(), currentPage);
							mv.addObject("realPath", realPath);
							mv.addObject("showData",pageUtils.getArrayList());
							mv.addObject("totalPage", new Integer(pageUtils.getPageCount()));
							mv.addObject("pageSize", Integer.valueOf(pageUtils.getPageSize()));
							mv.addObject("rows", new Integer(pageUtils.getRowCount()));
							mv.addObject("gotoPageFormHTML",CommUtil.showPageFormHtml(pageUtils.getCurrentPage(),pageUtils.getPageCount()));
							mv.addObject("currentPage", new Integer(pageUtils.getCurrentPage()));
							mv.addObject("gotoPageHTML", CommUtil.showPageHtml("", "",pageUtils.getCurrentPage(), pageUtils.getPageCount()));
						}
						is.close();
						sheet = null;
						wb = null;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	@RequestMapping({ "/importDataOk.htm" })
	public  void  importDataOk(HttpServletRequest request,HttpServletResponse response, HttpSession session,String realPath) {
		int count_update = 0;
		int count_insert = 0;

		List<MonitorDataImport> list = (List<MonitorDataImport>)session.getAttribute("importdatalist");
		Map<String, MonitorLine> monitorLinesWithName = new HashMap<String, MonitorLine>();// monitorline_Id->MonitorLine, to decrease query
		Map<Long, Sensor> sensorsWithId = new HashMap<Long, Sensor>();// sensor_Id->Sensor, to calculate sinking offset

		for(MonitorDataImport monitorDataImport : list){
			List<MonitorLine> newmonitorLines =  this.monitorLineService.query("select obj from MonitorLine obj where id ='"+ monitorDataImport.getMonitorLine_id() +"'", null, -1, -1);
			String monitorLineName = null;
			for(MonitorLine ml:newmonitorLines){
				monitorLineName = ml.getName();
			}
			List<Sensor> newsensor= this.sensorService.query("select obj from Sensor obj where obj.id = '"+ monitorDataImport.getSenser_id() +"'", null, -1, -1);
			String snesorName = null;
			for(Sensor sensor:newsensor){
				snesorName = sensor.getName();
			}
			Sensor sensor = this.sensorService.getObjByProperty("name", snesorName);
			MonitorLine monitorLine = monitorLinesWithName.get(monitorLineName);
			if(null == monitorLine){
				monitorLine = this.monitorLineService.getObjByProperty("name", monitorLineName);
				if(null != monitorLine) {
					monitorLinesWithName.put(monitorLineName, monitorLine); // cache monitor line
				}
			}

			if(!"".equals(sensor)){
				sensorsWithId.put(sensor.getId(), sensor);
				monitorDataImport.setSensor(sensor);
				monitorDataImport.setSensorType(sensor.getSensorType()); // peek from sensor
				boolean modifySensor = false;
				if(sensor.getLastCollectingTime() <= monitorDataImport.getCollectingTime()){ // used to query
					sensor.setLastCollectingTime(monitorDataImport.getCollectingTime());
					sensor.setLastDeviceData(monitorDataImport.getDeviceData());
					sensor.setLastSinkingData(CommUtil.roundDouble(monitorDataImport.getSinkingData()*0.1));
					sensor.setLastSinkingAccumulation(CommUtil.roundDouble(monitorDataImport.getSinkingAccumulation()*0.1));
					if (!sensor.isBase()) {
						calculateAlarmLevel(monitorLine, sensor);
					}
					modifySensor = true;
				}
				if (sensor.getFirstDeviceData() == Sensor.INIT_MONITORVALUE){ // used to calculate the sinking offset
					sensor.setFirstDeviceData(monitorDataImport.getDeviceData());
					Logger.printlnWithTime("update FirstDeviceData for sensor "+sensor);
					modifySensor = true;
				}
				if (modifySensor) {
					this.sensorService.update(sensor);
				}
			}
		}
		for (int i=0; i<list.size(); i++) {
			if (monitorDao.UpdateData(list.get(i)) == 1) {
				count_update++;
			}
			else {
				count_insert++;
			}
		}
		session.removeAttribute("lineList");    
		session.removeAttribute("sensorList");  
		session.removeAttribute("importdatalist");
		String filePath = realPath;
		File file = new File(filePath);
		//调用静态方法删除缓存文件
		removeDir(file);
		List notifylist = new ArrayList();
		Map map = new HashMap();
		map.put("count", "导入数据成功！\r\n" + "总计: "+list.size()+"条\r\n更新: "+count_update+"条\r\n增加: " + count_insert+"条" );
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

	private void calculateAlarmLevel(MonitorLine monitorLine, Sensor sensor) {
		double thresholdl1 = monitorLine.getSinkingthresholdl1();
		double thresholdl2 = monitorLine.getSinkingthresholdl2();
		double thresholdl3 = monitorLine.getSinkingthresholdl3();

		if(thresholdl1 < 0){
			thresholdl1 = thresholdl1 * -1;
		}
		if(thresholdl2 < 0){
			thresholdl2 = thresholdl2 * -1;
		}
		if(thresholdl3 < 0){
			thresholdl3 = thresholdl3 * -1;
		}
		// comment on 20161215, 判断alarm，原来是根据即时形变，改成按照累计形变判断
		double nagetiveSinkingAccumulation = sensor.getLastSinkingAccumulation();
		if(nagetiveSinkingAccumulation < 0){
			nagetiveSinkingAccumulation = nagetiveSinkingAccumulation * -1;
		}
		int alarmLevel = calculateAlarmLevelWithData(thresholdl1, thresholdl2, thresholdl3, nagetiveSinkingAccumulation);
		if(Globals.SET_INCLINOMETER.contains(sensor.getSensorType())){
			double nagetiveXData = sensor.getLastDeviceData();
			if(nagetiveXData < 0){
				nagetiveXData = nagetiveXData * -1;
			}
			int alarmLevelX = calculateAlarmLevelWithData(thresholdl1, thresholdl2, thresholdl3, nagetiveXData);
			double nagetiveYData = sensor.getLastSinkingData();
			if(nagetiveYData < 0){
				nagetiveYData = nagetiveYData * -1;
			}
			int alarmLevelY = calculateAlarmLevelWithData(thresholdl1, thresholdl2, thresholdl3, nagetiveYData);
			int higherLevel = (alarmLevel > alarmLevelX) ? alarmLevel : alarmLevelX;
			higherLevel = (higherLevel > alarmLevelY) ? higherLevel : alarmLevelY;
			sensor.setAlarmLevel(higherLevel);
		} else {
			sensor.setAlarmLevel(alarmLevel);
		}		
	}

	private int calculateAlarmLevelWithData(double thresholdl1,
			double thresholdl2, double thresholdl3, double nagetiveSinkingData) {
		if(nagetiveSinkingData >= thresholdl3){
			return Globals.ALARMLEVEL_L3;
		} else if(nagetiveSinkingData >= thresholdl2){
			return Globals.ALARMLEVEL_L2;
		} else if(nagetiveSinkingData >= thresholdl1){
			return Globals.ALARMLEVEL_L1;
		} else {
			return Globals.ALARMLEVEL_NORMAL;
		}
	}

	@RequestMapping({ "/importDataCancel.htm" })
	public ModelAndView importDataCancel(HttpServletRequest request,HttpServletResponse response, HttpSession session,String realPath) {
		ModelAndView mv = new JModelAndView("picreport/import.html", 0, request, response);
		if(session.getAttribute("lineList")!=null){
			if(session.getAttribute("sensorList")!=null){
				delSensor(session);
				delMonitorLine(session);
				session.removeAttribute("lineList");    
				session.removeAttribute("sensorList");      
			}else{
				delMonitorLine(session);
				session.removeAttribute("lineList");     
			}
		}
		session.removeAttribute("importdatalist");
		String filePath = realPath;
		File file = new File(filePath);
		//调用静态方法删除缓存文件
		removeDir(file);
		return mv;
	}

	public static void removeDir(File file){
		if (!file.isDirectory()) {  
			file.delete();  
		} else if (file.isDirectory()) {  
			File[] fileList = file.listFiles();  
			for (int i = 0; i < fileList.length; i++) {  
				File delfile = fileList[i];  
				if (!delfile.isDirectory()) {  
					delfile.delete();  
				}else{
					return;
				}
			}  
		}  
	}

	public void delSensor(HttpSession session){
		List<Sensor> sensorList = (List<Sensor>) session.getAttribute("sensorList");
		for(int j=0; j<sensorList.size(); j++){
			monitorDao.DelSensor(sensorList.get(j));
		}
	}

	public void delMonitorLine(HttpSession session){
		List<MonitorLine>  lineList = (List<MonitorLine>) session.getAttribute("lineList");
		for(int i=0; i<lineList.size(); i++){
			monitorDao.DelMonitorLine(lineList.get(i));
		}
	}

}
