package com.monitor.action.apptransport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.monitor.core.apptransport.AppStatusNotification;
import com.monitor.core.apptransport.CollectMsgNotification;
import com.monitor.core.apptransport.FirstValueNotification;
import com.monitor.core.apptransport.InitialValueItem;
import com.monitor.core.apptransport.MonitorDataItem;
import com.monitor.core.apptransport.MonitorDataViewItem;
import com.monitor.core.apptransport.MonitorLineModifyNotification;
import com.monitor.core.apptransport.Response;
import com.monitor.core.apptransport.ResultNotification;
import com.monitor.core.apptransport.SensorModifyNotification;
import com.monitor.core.apptransport.ThresholdSetNotification;
import com.monitor.core.apptransport.ThresholdSetStatusNotification;
import com.monitor.core.constant.Globals;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Logger;
import com.monitor.exception.ExceptionUtil;
import com.monitor.foundation.dao.AccumulativeDao;
import com.monitor.foundation.dao.ArchCoefficientDao;
import com.monitor.foundation.dao.AxialForceCoefficientDao;
import com.monitor.foundation.dao.InitialValueSettingDao;
import com.monitor.foundation.dao.MonitorDao;
import com.monitor.foundation.dao.TrendAnalysisDao;
import com.monitor.foundation.domain.AxialForceCoefficient;
import com.monitor.foundation.domain.InitialCollect;
import com.monitor.foundation.domain.InitialValue;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.TrendAnalysis;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;


@Controller
public class AppTransportAction {

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorDataService monitorDataService;

	@Autowired
	private IMonitorDataViewService monitorDataViewService;

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	public MonitorDao monitorDao;

	@Autowired
	public TrendAnalysisDao trendanalysisDao;

	@Autowired
	public ArchCoefficientDao archCoefficientDao;

	@Autowired
	public AccumulativeDao accumulativeDao;

	@Autowired
	public InitialValueSettingDao initialValueSettingDao;

	@Autowired
	private AxialForceCoefficientDao axialForceCoefficientDao;

	Gson gs = new Gson();

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@RequestMapping({ "/app/monitorData.htm" })
	public void monitorData(HttpServletRequest request, HttpServletResponse response) {
		//    	String contentType = request.getContentType();
		try {
			//	    	if(null==contentType || !contentType.equalsIgnoreCase("application/json")){
			//	    		PrintWriter printWriter = response.getWriter();
			//	    		response.setContentType("application/json");
			//	    		printWriter.write("Wrong HTTP request");
			//	    	} else {
			String jsonString = getJsonString(request);	// get received JSON data from request
			Logger.printlnWithTime("Receive monitorData String=" + jsonString);
			/*String result = sendPost("http://114.55.59.23:8888/ucdy/app/monitorData.htm", jsonString);
		    Logger.printlnWithTime("reggs return information:" + result);*/
			Gson gs = new Gson();
			MonitorDataItem[] monitorDataItems = gs.fromJson(jsonString, MonitorDataItem[].class); // format data
			MonitorData[] monitorDatas = new MonitorData[monitorDataItems.length];
			//数据转存到新表中
			MonitorDataViewItem[] monitorDataViewItems = gs.fromJson(jsonString, MonitorDataViewItem[].class); // format data
			MonitorDataView[] monitorDataViews = new MonitorDataView[monitorDataViewItems.length];

			if(null != monitorDataItems && monitorDataItems.length > 0){
				Logger.printlnWithTime("Receive MonitorData size="+monitorDataItems.length);
				Map<String, MonitorLine> monitorLinesWithName = new HashMap<String, MonitorLine>();// monitorline_Id->MonitorLine, to decrease query
				Map<Long, Sensor> sensorsWithId = new HashMap<Long, Sensor>();// sensor_Id->Sensor, to calculate sinking offset
				Map<Long, Double> basePointSinkingOffsets = new HashMap<Long, Double>();// sensor_Id->SinkingOffset, to calculate sinking offset
				Set<String> noExistedMonitorLines = null;
				Set<String> noExistedSensors = null;

				/*				List<MonitorData> monitorDataold = null;			//查询数据是否已经存在
				List<MonitorDataView> monitorDataviewold = null;*/

				for(int i = 0; i<monitorDataItems.length; i++){
					MonitorData monitorData = monitorDataItems[i].toMonitorData();
					String sensorName = monitorDataItems[i].getSensorName();
					String monitorLineName = monitorDataItems[i].getMonitorLineName();
					String sensorType = monitorDataItems[i].getSensorType();
					MonitorLine monitorLine = monitorLinesWithName.get(monitorLineName);
					MonitorDataView monitorDataView = monitorDataViewItems[i].toMonitorDataView();

					if(null == monitorLine){
						monitorLine = this.monitorLineService.getObjByProperty("name", monitorLineName);
						if(null != monitorLine) {
							monitorLinesWithName.put(monitorLineName, monitorLine); // cache monitor line
						}
					}
					if(null != monitorLine){
						monitorData.setMonitorLine(monitorLine);
						monitorDataView.setMonitorLine(monitorLine);
					} else {
						if(null == noExistedMonitorLines){
							noExistedMonitorLines = new HashSet<String>();
						}
						noExistedMonitorLines.add(monitorLineName);
						continue;
					}
					long collecting = monitorDataItems[i].getCollectingTime();
					Sensor sensor = this.sensorService.getObjByProperty("name", sensorName);

					//弦类传感器计算
					if("AxialForceMeter".equals(sensorType)||"EarthPressureGauge".equals(sensorType)||"SteelStressMeter".equals(sensorType)||"StrainGauge".equals(sensorType)) {
						axialForceCoefficientDao.AF_coeffAdd(sensorName, monitorLine.getId().longValue());
						List<AxialForceCoefficient> Af_data = axialForceCoefficientDao.getAxialForceCoefficientData(sensorName);
						if((Af_data != null) && (!"".equals(Af_data))) {
							double calibratedValue = Af_data.get(0).getCalibratedValue();
							double fristFrequency = Af_data.get(0).getFristFrequency();
							if(0 != fristFrequency){
								double deviceData = monitorData.getDeviceData();
								double singkingAccumulation = calibratedValue * (deviceData * deviceData - fristFrequency * fristFrequency);
								double singkingData = singkingAccumulation - sensor.getLastSinkingAccumulation();
								monitorData.setSinkingData(CommUtil.roundDouble(singkingData));
								monitorData.setSinkingAccumulation(CommUtil.roundDouble(singkingAccumulation));
								monitorDataView.setSinkingData(CommUtil.roundDouble(singkingData));
								monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(singkingAccumulation));
							}
							double frequency = Af_data.get(0).getFrequency();
							if((0 != frequency) && (0 != fristFrequency)) {
								double deviceData = monitorData.getDeviceData();
								double singkingData = calibratedValue * (deviceData * deviceData - frequency * frequency);
								monitorData.setSinkingData(CommUtil.roundDouble(singkingData));
								monitorDataView.setSinkingData(CommUtil.roundDouble(singkingData));
							}
						}
					}

					//压力式静力水准仪趋势分析公式
					if(sensorType.equals("P-levelingTransducer")){
						List<TrendAnalysis>  trendanalysis= trendanalysisDao.getData(monitorLine.getId());
						if(null != trendanalysis && !trendanalysis.isEmpty()){
							long maxTime = 0;
							long minTime = 0;
							long compareTimeLong = 0;
							long collectingLong = 0;
							for(TrendAnalysis data:trendanalysis){
								String collectTime = Globals.DateTimeFormat.format(collecting);
								String collect_hms = collectTime.split(" ")[1];			//截取本次上传时间时分秒
								compareTimeLong = data.getCompareTime();
								String compareTime = Globals.DateTimeFormat.format(compareTimeLong);
								String compare = compareTime.split(" ")[0] + " " + collect_hms;		//对应时间拼接
								try {
									Date collctingTimeDate = Globals.DateFormat.parse(collectTime.split(" ")[0]);
									collectingLong = collctingTimeDate.getTime();
									Date compareTimeDate = Globals.DateTimeFormat.parse(compare);
									maxTime = compareTimeDate.getTime();
									minTime = maxTime - data.getTrendInterval();
								} catch (ParseException e) {
									System.err.println("时间转换错误");
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
					}
					//监测系数是否需要创建,默认为1
					int addBack =  archCoefficientDao.coeffAdd(sensorName);
					if(addBack == 0){
						Logger.printlnWithTime("The coefficients of Sensor:[" + sensorName+ "]  add success!");
					}else if(addBack == 1){
						Logger.printlnWithTime("The coefficients of Sensor:[" + sensorName+ "]  is existing!");
					}
					//分时段乘系数,获取时间结点，与预设的数据对比
					String ctime= sdf.format(collecting);
					String collectSubString = ctime.substring(11, 13);
					int collectSubint=Integer.parseInt(collectSubString);
					//默认添加系数时间间隔表的数据为6
					archCoefficientDao.timeAdd();
					String time= archCoefficientDao.time(collectSubint);
					int checkTime = Integer.parseInt(time);
					double coeff = 1;
					if(collectSubint < checkTime && collectSubint >= 00){
						//取出系数
						String count = archCoefficientDao.coefficient(monitorDataItems[i].getSensorName());
						coeff = Double.parseDouble(count);
						double sinkingAccumulation = monitorDataView.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(sinkingAccumulation*coeff));
					}else{
						//取出系数
						String count = archCoefficientDao.coeff(monitorDataItems[i].getSensorName());
						coeff = Double.parseDouble(count);
						double sinkingAccumulation = monitorDataView.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(sinkingAccumulation*coeff));
					}

					//数据调整
					double sumData;
					if(sensor!=null){
						//累计形变矫正系数是否需要创建,默认为0
						int accumulateBack =  accumulativeDao.accumulateAdd(sensor.getMonitorLine().getId(),sensorName,collecting);
						if(accumulateBack == 0){
							Logger.printlnWithTime("The accumulative of Sensor:[" + sensorName+ "]  add success!");
						}else if(accumulateBack == 1){
							Logger.printlnWithTime("The accumulative of Sensor:[" + sensorName+ "]  is existing!");
						}
						//获取累计形变 矫正值
						long modifyTime = accumulativeDao.accumulateTime(sensorName);
						String accumulateData = accumulativeDao.accumulateData(sensorName,modifyTime);
						sumData = Double.parseDouble(accumulateData);
						double sinkingAccumulation = monitorDataView.getSinkingAccumulation();
						monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(sinkingAccumulation + sumData));
					}

					if(null != sensor){
						sensorsWithId.put(sensor.getId(), sensor); // cache sensor 
						monitorData.setSensor(sensor);
						monitorData.setSensorType(sensor.getSensorType()); // peek from sensor
						monitorDataView.setSensor(sensor);
						monitorDataView.setSensorType(sensor.getSensorType());

						boolean modifySensor = false;
						if(sensor.getLastCollectingTime() <= monitorDataView.getCollectingTime()){ // used to query
							if(sensorType.equals("AxialForceMeter") || sensorType.equals("EarthPressureGauge") || sensorType.equals("SteelStressMeter") || sensorType.equals("StrainGauge")){
								sensor.setLastCollectingTime(monitorDataView.getCollectingTime());
								sensor.setLastDeviceData(CommUtil.roundDouble(monitorDataView.getDeviceData()));
								sensor.setLastSinkingData(CommUtil.roundDouble(monitorDataView.getSinkingData()));
								sensor.setLastSinkingAccumulation(CommUtil.roundDouble(monitorDataView.getSinkingAccumulation()));
								if (!sensor.isBase()) {
									calculateAlarmLevel(monitorLine, sensor);
								}
								modifySensor = true;
							}else{
								if(Math.abs(monitorDataView.getSinkingAccumulation()) < 50){
									sensor.setLastCollectingTime(monitorDataView.getCollectingTime());
									sensor.setLastDeviceData(CommUtil.roundDouble(monitorDataView.getDeviceData()));
									sensor.setLastSinkingData(CommUtil.roundDouble(monitorDataView.getSinkingData()));
									sensor.setLastSinkingAccumulation(CommUtil.roundDouble(monitorDataView.getSinkingAccumulation()));
									if (!sensor.isBase()) {
										calculateAlarmLevel(monitorLine, sensor);
									}
									modifySensor = true;
								}
							}
							
						}
						if (sensor.getFirstDeviceData() == Sensor.INIT_MONITORVALUE){ // used to calculate the sinking offset
							sensor.setFirstDeviceData(monitorData.getDeviceData());
							Logger.printlnWithTime("update FirstDeviceData for sensor "+sensor);
							modifySensor = true;
						}
						if (modifySensor) {
							this.sensorService.update(sensor);
						}

						// cache sinking offset value for base point
						if(sensor.isBase()){
							basePointSinkingOffsets.put(sensor.getId(), monitorData.getDeviceData() - sensor.getFirstDeviceData());
						}
						/*//查询当前时间数据库是否存在相同数据
						monitorDataold = this.monitorDataService.query("select obj from MonitorData obj where obj.sensor.id='"+sensor.getId()+"' and obj.monitorLine.id='"+monitorLine.getId()+"' and obj.collectingTime="+monitorData.getCollectingTime(), null, 0, 0);
						monitorDataviewold = this.monitorDataViewService.query("select obj from MonitorDataView obj where obj.sensor.id='"+sensor.getId()+"' and obj.monitorLine.id='"+monitorLine.getId()+"' and obj.collectingTime="+monitorData.getCollectingTime(), null, 0, 0);
						 */					} else {
							 if(null == noExistedSensors){
								 noExistedSensors = new HashSet<String>();
							 }
							 noExistedSensors.add(sensorName);
							 continue;
						 }
					monitorDatas[i] = monitorData;
					monitorDataViews[i] = monitorDataView;

					// Logger.printlnWithTime("monitorData= "+monitorData);
				}

				if(null != noExistedMonitorLines && !noExistedMonitorLines.isEmpty()){
					Logger.printlnWithTime("WARNING: monitor line " + noExistedMonitorLines + " is not existed for monitor data");
				}
				if(null != noExistedSensors && !noExistedSensors.isEmpty()){
					Logger.printlnWithTime("WARNING: sensor " + noExistedSensors + " is not existed for monitor data");
				}
				// calculate sinking data offset
				for(int i = 0; i<monitorDatas.length; i++){
					MonitorData monitorData = monitorDatas[i];
					MonitorDataView monitorDataView = monitorDataViews[i];
					if(null == monitorData){
						continue;
					}
					if(!Globals.SET_SENSORTYPE_TRANSDUCER.contains(monitorData.getSensorType())){ // calculate offset for leveling transducer only
						continue;
					}
					Sensor sensor = monitorData.getSensor();
					MonitorLine monitorLine = sensor.getMonitorLine();
					if(MonitorLine.INIT_BASEPOINT_SENSORID != monitorLine.getBasePointSensorId() // make sure monitor line have a base point
							&& monitorLine.getBasePointSensorId() != sensor.getId() // it is not base point(do not calculate for base-point)
							&& sensor.getFirstDeviceData() != Sensor.INIT_MONITORVALUE){ // have the firstDeviceData? generally, indeed!
						Double bpSinkingOffset = basePointSinkingOffsets.get(monitorLine.getBasePointSensorId());
						if(bpSinkingOffset == null){
							setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOBASEPOINTDATA, "No base point data");
							return;
						}
						double currentSinkingOffset = (monitorData.getDeviceData() - sensor.getFirstDeviceData());
						double offset = (currentSinkingOffset - bpSinkingOffset) * -1;
						monitorData.setSinkingOffset(CommUtil.roundDouble(offset));
						monitorDataView.setSinkingOffset(CommUtil.roundDouble(offset));
					}
				}
				this.monitorDataService.save(monitorDatas);
				for(int i = 0; i<monitorDataViews.length; i++){
					MonitorDataView monitorDataView = monitorDataViews[i];
					String sensorType = monitorDataView.getSensorType();
					if(sensorType.equals("AxialForceMeter") || sensorType.equals("EarthPressureGauge") || sensorType.equals("SteelStressMeter") || sensorType.equals("StrainGauge")){
						this.monitorDataViewService.save(monitorDataView);
					}else{
						if(Math.abs(monitorDataView.getSinkingAccumulation()) < 50){
							this.monitorDataViewService.save(monitorDataView);
						}
					}
				}

			}
			setSuccessResponse(response, gs);

		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}

	private String sendPost(String url, String jsonString) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(jsonString);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally{
			try{
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		return result;
	}


	private void calculateAlarmLevel(MonitorLine monitorLine, Sensor sensor) {
		double thresholdl1 = monitorLine.getSinkingthresholdl1();
		double thresholdl2 = monitorLine.getSinkingthresholdl2();
		double thresholdl3 = monitorLine.getSinkingthresholdl3();
		/*//特殊处理，TAIA项目将数据库内阈值放大10倍，本身字段
		if(monitorLine.getName().equals("G1-01")){
			thresholdl1 *= 10;
			thresholdl2 *= 10;
			thresholdl3 *= 10;
		}*/
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
		//Logger.printlnWithTime("Sensor " + sensor.getName() +" - alarm level is " + sensor.getAlarmLevel());
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


	private void setSuccessResponse(HttpServletResponse response, Gson gs)
			throws IOException {
		PrintWriter printWriter = response.getWriter();
		response.setContentType("application/json");
		Response jsonResponse = new Response(Response.RESULTCODE_SUCCESS, "SUCCESS");
		printWriter.write(gs.toJson(jsonResponse));
	}

	private void setResponse(HttpServletResponse response, String jsonString) throws IOException {
		PrintWriter printWriter = response.getWriter();
		response.setContentType("application/json;charset=UTF-8");
		printWriter.write(jsonString);
	}

	private void setResponse(HttpServletResponse response, Gson gs, int resultCode, String resultString)
			throws IOException {
		PrintWriter printWriter = response.getWriter();
		response.setContentType("application/json;charset=UTF-8");
		Response jsonResponse = new Response(resultCode, resultString);
		printWriter.write(gs.toJson(jsonResponse));
	}


	@RequestMapping({ "/app/monitorLineModify.htm" })
	public void monitorLineModify(HttpServletRequest request, HttpServletResponse response) {
		try {
			// get received JSON data from request
			String jsonString = getJsonString(request);
			Logger.printlnWithTime("Receive monitorLineModify String=" + jsonString);
			MonitorLineModifyNotification modifyNotify = gs.fromJson(jsonString, MonitorLineModifyNotification.class);
			if(null != modifyNotify.getOperation()){
				if(null==modifyNotify.getMonitorLineName() || modifyNotify.getMonitorLineName().isEmpty()){
					setResponse(response, gs, Response.RESULTCODE_DATAERROR, "MonitorLine cannot be null");
					Logger.printlnWithTime("MonitorLine cannot be null");
					return;
				}
				if(modifyNotify.getOperation().equals(Globals.DEVICE_MODIFY_ADD)){
					MonitorLine old = this.monitorLineService.getObjByProperty("name", modifyNotify.getMonitorLineName());
					if(null != old){
						setResponse(response, gs, Response.RESULTCODE_DATAERROR_EXISTEDDEVICE, "Monitor line " + modifyNotify.getMonitorLineName() + " is existing");
						Logger.printlnWithTime("Monitor line " + modifyNotify.getMonitorLineName() + " is existing");
						return;
					}
					MonitorLine monitorLine = modifyNotify.toMonitorLine();
					monitorLine.setAliveStatus(MonitorLine.ALIVESTATUS_ONLINE);
					Logger.printlnWithTime("add " + monitorLine);
					monitorLineService.save(monitorLine);
					String sensorTypeName = monitorLine.getSensorType();
					List<SensorType> sensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name ='"+sensorTypeName+"'", null);
					if(null!=sensorType){
						if(sensorType.get(0).getUsingStatus() == -1){
							int usingStatus = 0;
							this.monitorDao.UpdateUsingStatus(sensorType.get(0).getName(),usingStatus);
						}
					}
				} else if(modifyNotify.getOperation().equals(Globals.DEVICE_MODIFY_UPDATE)){
					// modify attribute host name and port
					MonitorLine old = this.monitorLineService.getObjByProperty("name", modifyNotify.getMonitorLineName());
					if(null == old){
						setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, 
								"Monitor line " + modifyNotify.getMonitorLineName() + " is not existing");
						Logger.printlnWithTime("Monitor line " + modifyNotify.getMonitorLineName() + " is not existing");
						return;
					}
					old.setHostName(modifyNotify.getHostName());
					old.setPort(modifyNotify.getPort());
					Logger.printlnWithTime("update " + old);
					monitorLineService.update(old);
				} else if(modifyNotify.getOperation().equals(Globals.DEVICE_MODIFY_DEL)){
					MonitorLine old = this.monitorLineService.getObjByProperty("name", modifyNotify.getMonitorLineName());
					if(null != old){
						Logger.printlnWithTime("delete " + modifyNotify.getMonitorLineName());
						// monitorLineService.delete(old.getId());
						monitorLineService.cascadingDelete(old.getId());
						String sensorTypeName = old.getSensorType();
						List<SensorType> sensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name ='"+sensorTypeName+"'", null);
						if(null!=sensorType){
							List<MonitorLine> monitorLines = this.monitorLineService.getObjsBySensorTypeName(sensorTypeName);
							if(monitorLines.isEmpty()){
								if(sensorType.get(0).getUsingStatus() == 0){
									int usingStatus = -1;
									this.monitorDao.UpdateUsingStatus(sensorType.get(0).getName(),usingStatus);
								}
							}
						}
					} else {
						setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, 
								"Monitor line " + modifyNotify.getMonitorLineName() + " is not existing");
						Logger.printlnWithTime("Monitor line " + modifyNotify.getMonitorLineName() + " is not existing");
						return;
					}
				} 
			}
			setSuccessResponse(response, gs);
		} catch (Exception ioe){
			ioe.printStackTrace();
			try {
				setResponse(response, gs, Response.RESULTCODE_OTHERERROR, ExceptionUtil.getInnestExceptionMessage(ioe));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@RequestMapping({ "/app/thresholdset.htm" })
	public void thresholdSet(HttpServletRequest request, HttpServletResponse response) {
		try {
			// get received JSON data from request
			String jsonString = getJsonString(request);
			Logger.printlnWithTime("Receive thresholdSet String=" + jsonString);
			Gson gs = new Gson();
			ThresholdSetNotification thresholdNotify = gs.fromJson(jsonString, ThresholdSetNotification.class);
			Logger.printlnWithTime("get threshold notificaton from app device: " + thresholdNotify);
			if(null != thresholdNotify){
				String monitorLineName = thresholdNotify.getMonitorLineName();
				MonitorLine monitorLine = this.monitorLineService.getObjByProperty("name", monitorLineName);
				if(null != monitorLine){
					monitorLine.setSinkingthresholdl1(thresholdNotify.getLevel1SinkingThreshold());
					monitorLine.setSinkingthresholdl2(thresholdNotify.getLevel2SinkingThreshold());
					monitorLine.setSinkingthresholdl3(thresholdNotify.getLevel3SinkingThreshold());
					monitorLine.setThresholdSetStatus(Globals.THRESHOLDSTATUS_SUCCESS);
					this.monitorLineService.update(monitorLine);
				} else {
					setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, 
							"Monitor line " + monitorLineName + " is not existing");
					Logger.printlnWithTime("Monitor line " + monitorLineName + " is not existing");
					return;
				}
			}
			setSuccessResponse(response, gs);
		} catch (Exception ioe){
			ioe.printStackTrace();
			try {
				setResponse(response, gs, Response.RESULTCODE_OTHERERROR, ExceptionUtil.getInnestExceptionMessage(ioe));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@RequestMapping({ "/app/sensorModify.htm" })
	public void sensorModify(HttpServletRequest request, HttpServletResponse response) {
		try {
			// get received JSON data from request
			String jsonString = getJsonString(request);
			Logger.printlnWithTime("Receive sensorModify String=" + jsonString);
			Gson gs = new Gson();
			SensorModifyNotification[] modifyNotifys = gs.fromJson(jsonString, SensorModifyNotification[].class);
			if(null != modifyNotifys && modifyNotifys.length > 0){
				for(SensorModifyNotification modifyNotify : modifyNotifys){
					if(null != modifyNotify.getOperation()){
						String sensorName = modifyNotify.getSensorName();
						String monitorLineName = modifyNotify.getMonitorLineName();
						if(null==sensorName || sensorName.isEmpty()){
							setResponse(response, gs, Response.RESULTCODE_DATAERROR, "Sensor name cannot be null");
							Logger.printlnWithTime("Sensor name cannot be null");
							return;
						}
						if(null==monitorLineName || monitorLineName.isEmpty()){
							setResponse(response, gs, Response.RESULTCODE_DATAERROR, "Monitor line name cannot be null");
							Logger.printlnWithTime("Monitor line name cannot be null");
							return;
						}
						if(modifyNotify.getOperation().equals(Globals.DEVICE_MODIFY_ADD)){
							Sensor old = this.sensorService.getObjByProperty("name", sensorName);
							if(null != old){
								setResponse(response, gs, Response.RESULTCODE_DATAERROR_EXISTEDDEVICE, "Sensor " +  modifyNotify.getSensorName() + " is existing");
								Logger.printlnWithTime("Sensor " +  modifyNotify.getSensorName() + " is existing");
								return;
							}
							Sensor sensor = modifyNotify.toSensor();
							sensor.setAlarmLevel(Globals.ALARMLEVEL_NORMAL);

							MonitorLine oldMonitorLine = this.monitorLineService.getObjByProperty("name", monitorLineName);
							if(null == oldMonitorLine){
								setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, "Monitor line " +  monitorLineName + " is not existing");
								Logger.printlnWithTime("Monitor line " +  monitorLineName + " is not existing");
								return;
							}
							sensor.setMonitorLine(oldMonitorLine);
							Logger.printlnWithTime("add " + sensor);
							sensorService.save(sensor);
							if(sensor.isBase()){
								oldMonitorLine.setBasePointSensorId(sensor.getId()); // 使用ID, 而不引用对象，避免循环依赖，因为sensor已经依赖monitor line
								this.monitorLineService.update(oldMonitorLine);
							}
						} else if(modifyNotify.getOperation().equals(Globals.DEVICE_MODIFY_UPDATE)){
							// modify attribute base
							Sensor old = this.sensorService.getObjByProperty("name", sensorName);
							if(null == old){
								setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, "Sensor " +  sensorName + " is not existing");
								Logger.printlnWithTime("Sensor " +  sensorName + " is not existing");
								return;
							}
							boolean changeBasePoint = false;
							if(old.isBase() != modifyNotify.isBase()) {
								old.setBase(modifyNotify.isBase());
								changeBasePoint = true;
								Logger.printlnWithTime("change monitor line " + monitorLineName + "'s base point to " + sensorName);
							}
							Logger.printlnWithTime("update " + old);
							sensorService.update(old);

							if(changeBasePoint && modifyNotify.isBase()){ // this sensor is base now
								MonitorLine monitorLine = this.monitorLineService.getObjByProperty("name", monitorLineName);
								if(null == monitorLine){
									return; //never go to here
								} else {
									monitorLine.setBasePointSensorId(old.getId());
									this.monitorLineService.update(monitorLine);
								}
							}
						} else if(modifyNotify.getOperation().equals(Globals.DEVICE_MODIFY_DEL)){
							Sensor old = this.sensorService.getObjByProperty("name", sensorName);
							if(null != old){
								Logger.printlnWithTime("delete " + sensorName);
								//this.sensorService.delete(old.getId());
								this.sensorService.cascadingDelete(old.getId());
							} else {
								setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, "Sensor " +  sensorName + " is not existing");
								Logger.printlnWithTime("Sensor " +  sensorName + " is not existing");
								return;
							}
						} 
					}
				}
			}
			setSuccessResponse(response, gs);
		} catch (Exception ioe){
			ioe.printStackTrace();
			try {
				setResponse(response, gs, Response.RESULTCODE_OTHERERROR, ExceptionUtil.getInnestExceptionMessage(ioe));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping({ "/app/heartbeat.htm" })
	public void heartBeat(HttpServletRequest request, HttpServletResponse response) {
		try {
			// get received JSON data from request
			String jsonString = getJsonString(request);
			Logger.printlnWithTime("Receive heartbeat String=" + jsonString);
			Gson gs = new Gson();
			AppStatusNotification heartbeat = gs.fromJson(jsonString, AppStatusNotification.class);
			if(null != heartbeat && null != heartbeat.getMonitorLineName()){
				String monitorLineName = heartbeat.getMonitorLineName();
				MonitorLine monitorLine = this.monitorLineService.getObjByProperty("name", monitorLineName);
				if(null != monitorLine){
					monitorLine.setLastHeartBeatTime(System.currentTimeMillis());
					monitorLine.setAliveStatus(MonitorLine.ALIVESTATUS_ONLINE);
					this.monitorLineService.update(monitorLine);
					Logger.printlnWithTime("get heart beat from " + monitorLineName);
					if(monitorLine.getThresholdSetStatus() == Globals.THRESHOLDSTATUS_ONGOING){
						List sendThreshold = new ArrayList<>();
						Map addop = new HashMap<>();
						addop.put("ADDOP", "THLD");
						sendThreshold.add(gs.toJson(addop));
						ThresholdSetNotification jsonResponse = new ThresholdSetNotification();
						jsonResponse.setMonitorLineName(monitorLineName);
						jsonResponse.setLevel1SinkingThreshold(monitorLine.getSinkingthresholdl1());
						jsonResponse.setLevel2SinkingThreshold(monitorLine.getSinkingthresholdl2());
						jsonResponse.setLevel3SinkingThreshold(monitorLine.getSinkingthresholdl3());
						sendThreshold.add(gs.toJson(jsonResponse));
						ResultNotification result = new ResultNotification();
						result.setResultCode(1);
						result.setResultString("SUCCESS");
						sendThreshold.add(gs.toJson(result));
						String jsonStringThresholdSet = sendThreshold.toString();
						// jsonStringThresholdSet = jsonStringThresholdSet.replaceAll("\r|\n", "");
						setResponse(response, jsonStringThresholdSet);
						Logger.printlnWithTime("set Threshold to device=" + jsonStringThresholdSet);
						return;
					}//检测是否需下发采集指令
					long collectCode = this.initialValueSettingDao.collectOrder(monitorLine.getId());
					if(collectCode != 0){
						List sendCollectMsg = new ArrayList<>();
						List<InitialCollect> collectMsg = this.initialValueSettingDao.getInitialCollect();
						if(!collectMsg.isEmpty()){
							double modifyValue = this.initialValueSettingDao.modifyCollectSetStatus();
							Map addop = new HashMap<>();
							addop.put("ADDOP", "FVGT");
							sendCollectMsg.add(gs.toJson(addop));
							CollectMsgNotification collect = new CollectMsgNotification();
							String lineName = "";
							int i = 1;
							for(InitialCollect ic:collectMsg){
								long monitorlineidLong = (long)ic.getMonitorlineid();
								MonitorLine monitorline = this.monitorLineService.getObjById(monitorlineidLong);
								lineName = lineName + monitorline.getName();
								if(i < collectMsg.size()){
									lineName = lineName + ",";
								}
								i++;
							}
							collect.setMonitorLineName(lineName);
							collect.setStartTime(collectMsg.get(0).getStartTime());
							collect.setStopTime(collectMsg.get(0).getStopTime());
							collect.setSampleInterval(collectMsg.get(0).getSampleInterva());
							sendCollectMsg.add(gs.toJson(collect));
							ResultNotification result = new ResultNotification();
							result.setResultCode(1);
							result.setResultString("SUCCESS");
							sendCollectMsg.add(gs.toJson(result));
						}
						String initialValueString = sendCollectMsg.toString();
						setResponse(response,initialValueString);
						Logger.printlnWithTime("set initCollect to device=" + initialValueString);
						return;
					}
					long sendCode = this.initialValueSettingDao.sendInitial(monitorLine.getId());
					if(sendCode != 0){
						List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where monitorline_id = " + monitorLine.getId(), null, -1, -1); 
						List sendInitialValue = new ArrayList<>();
						Map addop = new HashMap<>();
						addop.put("ADDOP", "FVST");
						sendInitialValue.add(gs.toJson(addop));
						for(Sensor sensor:sensors){
							String sensorName = sensor.getName();
							long collectingTime = this.initialValueSettingDao.initTime(sensorName);
							List<InitialValue> initialValue = this.initialValueSettingDao.getInitialValue(sensorName,collectingTime);
							if(!initialValue.isEmpty()){
								double modifyValue = this.initialValueSettingDao.modifyInitSetStatus(sensorName);
								List init = new ArrayList<>();
								for(InitialValue initValue:initialValue){
									FirstValueNotification firstValue = new FirstValueNotification();
									firstValue.setSensorName(initValue.getSensorName());
									firstValue.setDeviceData0(initValue.getDeviceData0());
									firstValue.setDeviceData1(initValue.getDeviceData1());
									firstValue.setDeviceData2(initValue.getDeviceData2());
									sendInitialValue.add(gs.toJson(firstValue));
								}
								//map.put(sensorName, init.get(0)+","+init.get(1)+","+init.get(2));
							}
						}
						ResultNotification result = new ResultNotification();
						result.setResultCode(1);
						result.setResultString("SUCCESS");
						sendInitialValue.add(gs.toJson(result));
						String initialValueString = sendInitialValue.toString();
						setResponse(response,initialValueString);
						Logger.printlnWithTime("set initValue to device=" + initialValueString);
						return;
					}
				} else {
					setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, "Monitor line " + monitorLineName + " is not existing");
					Logger.printlnWithTime("Monitor line " + monitorLineName + " is not existing");
					return;
				}
			} else {
				setResponse(response, gs, Response.RESULTCODE_FORMATERROR, "Data format error");
				Logger.printlnWithTime("Data format error");
				return;
			}
			setHeartBeatSuccessResponse(response, gs);
		} catch (Exception ioe){
			ioe.printStackTrace();
			try {
				setResponse(response, gs, Response.RESULTCODE_OTHERERROR, ExceptionUtil.getInnestExceptionMessage(ioe));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setHeartBeatSuccessResponse(HttpServletResponse response, Gson gs)
			throws IOException {
		PrintWriter printWriter = response.getWriter();
		response.setContentType("application/json");
		List resultMsg = new ArrayList<>();
		Map addop = new HashMap<>();
		addop.put("ADDOP", "NONE");
		resultMsg.add(gs.toJson(addop));
		ResultNotification result = new ResultNotification();
		result.setResultCode(1);
		result.setResultString("SUCCESS");
		resultMsg.add(gs.toJson(result));
		String heartBeatResponse = resultMsg.toString();
		printWriter.write(heartBeatResponse);
	}

	@RequestMapping({ "/app/thresholdsetstatusnotify.htm" })
	public void thresholdSetStatusNotify(HttpServletRequest request, HttpServletResponse response) {
		try {
			// get received JSON data from request
			String jsonString = getJsonString(request);
			Logger.printlnWithTime("Receive thresholdsetstatusnotify String=" + jsonString);
			Gson gs = new Gson();
			ThresholdSetStatusNotification notify = gs.fromJson(jsonString, ThresholdSetStatusNotification.class);
			String monitorLineName = notify.getMonitorLineName();
			if(null != monitorLineName && !monitorLineName.isEmpty()){
				MonitorLine monitorLine = this.monitorLineService.getObjByProperty("name", monitorLineName);
				if(null == monitorLine){
					setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, "Monitor line " + monitorLineName + " is not existing");
					Logger.printlnWithTime("Monitor line " + monitorLineName + " is not existing");
					return;
				}
				if(monitorLine.getThresholdSetStatus() == Globals.THRESHOLDSTATUS_ONGOING){
					int resultCode = notify.getResultCode();
					if(Globals.THRESHOLDSTATUS_INIT != resultCode){
						monitorLine.setThresholdSetStatus(resultCode);
						this.monitorLineService.update(monitorLine);
					} else { // THRESHOLDSTATUS_INIT(0) is the init status, just new instance uses the init value
						setResponse(response, gs, Response.RESULTCODE_DATAERROR, "Result code is wrong:" + resultCode);
						Logger.printlnWithTime("Result code of thresholdsetstatusnotify is wrong:" + resultCode + " for monitor line " + monitorLineName);
						return;
					}
				}
			}
			setSuccessResponse(response, gs);
		} catch (Exception ioe){
			ioe.printStackTrace();
			try {
				setResponse(response, gs, Response.RESULTCODE_OTHERERROR, ExceptionUtil.getInnestExceptionMessage(ioe));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping({ "/app/firstvaluereport.htm" })
	public void firstvaluereport(HttpServletRequest request, HttpServletResponse response) {
		try {
			// get received JSON data from request
			String jsonString = getJsonString(request);
			Logger.printlnWithTime("Receive firstValue String=" + jsonString);
			InitialValueItem[] initialValueItems = gs.fromJson(jsonString, InitialValueItem[].class);
			InitialValue[] initialValues = new InitialValue[initialValueItems.length];
			if(null != initialValueItems && initialValueItems.length > 0){
				for(int i = 0; i<initialValueItems.length; i++){
					String sensorName = initialValueItems[i].getSensorName();
					Sensor old = this.sensorService.getObjByProperty("name",sensorName);
					if(null == old){
						setResponse(response, gs, Response.RESULTCODE_DATAERROR_NOTEXISTEDDEVICE, "Sensor " +  sensorName + " is not existing");
						Logger.printlnWithTime("Sensor " +  sensorName + " is not existing");
						return;
					}
				}
				for(int i = 0; i<initialValues.length; i++){
					InitialValue initial = initialValueItems[i].toInitialValue();
					initial.setInitSetStatus(3);
					this.initialValueSettingDao.updateSetstatus(initial.getSensorName());
					Sensor sensor = this.sensorService.getObjByProperty("name", initial.getSensorName());
					this.initialValueSettingDao.insertInitialValue(initial,sensor.getMonitorLine().getId());
				}
				this.initialValueSettingDao.updateCollectSetstatus();
			}
			setSuccessResponse(response, gs);
		} catch (Exception ioe){
			ioe.printStackTrace();
			try {
				setResponse(response, gs, Response.RESULTCODE_OTHERERROR, ExceptionUtil.getInnestExceptionMessage(ioe));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getJsonString(HttpServletRequest request) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String jsonString = br.readLine();
		StringBuffer jsonSB = new StringBuffer();
		while(jsonString != null){
			jsonSB.append(jsonString);
			jsonString = br.readLine();
		}
		return jsonSB.toString();
	}

}
