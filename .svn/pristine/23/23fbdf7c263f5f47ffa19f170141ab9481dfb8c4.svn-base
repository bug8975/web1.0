package com.monitor.action.deviceapp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.monitor.action.Utils;
import com.monitor.core.constant.Globals;
import com.monitor.foundation.dao.AxialForceCoefficientDao;
import com.monitor.foundation.dao.MonitorDao;
import com.monitor.foundation.domain.AxialForceCoefficient;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.User;
import com.monitor.foundation.service.IMonitorLineService;
import com.monitor.foundation.service.ISensorService;
import com.monitor.foundation.service.ISensorTypeService;
import com.monitor.mv.JModelAndView;

@Controller
public class AxialForceAction {

	@Autowired
	private ISensorTypeService sensorTypeService;

	@Autowired
	private ISensorService sensorService;

	@Autowired
	private IMonitorLineService monitorLineService;

	@Autowired
	public MonitorDao monitorDao;
	
	@Autowired
	public AxialForceCoefficientDao Af_CoefficientDao;


	@RequestMapping({ "/deviceapp/axialForceCoefficient.htm" })
	public ModelAndView appStatusList(HttpServletRequest request, HttpServletResponse response, String currentPage, 
			String sensorTypeName, String monitorlineid, String sensorid) {
		User user = Utils.checkLoginedUser(request);
		if(user == null){
			return Utils.redirectToLoginForNonLogined(request, response);
		}
		ModelAndView mv = new JModelAndView("deviceapp/axialForceCoefficient.html", 0, request, response);
		mv.addObject("sensorTypeName", sensorTypeName);
        mv.addObject("monitorlineid", monitorlineid);
		// for condition selecting
        List<SensorType> sensorTypesForAxialForce = new ArrayList<SensorType>(4);
		List<SensorType> aSensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name = 'AxialForceMeter'", null);
		if(null != aSensorType && aSensorType.get(0).getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAxialForce.add(aSensorType.get(0));
		}
		List<SensorType> eSensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name = 'EarthPressureGauge'", null);
		if(null != eSensorType && eSensorType.get(0).getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAxialForce.add(eSensorType.get(0));
		}
		List<SensorType> steelSensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name = 'SteelStressMeter'", null);
		if(null != steelSensorType && steelSensorType.get(0).getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAxialForce.add(steelSensorType.get(0));
		}
		List<SensorType> strainSensorType = this.sensorTypeService.query("select obj from SensorType obj where obj.name = 'StrainGauge'", null);
		if(null != strainSensorType && strainSensorType.get(0).getUsingStatus() == SensorType.STATUS_USING){
			sensorTypesForAxialForce.add(strainSensorType.get(0));
		}
		mv.addObject("sensorTypes", sensorTypesForAxialForce);
		
		if(null != sensorTypeName && !sensorTypeName.isEmpty()){
			List<MonitorLine> monitorLines = this.monitorLineService.query("select obj from MonitorLine obj where obj.sensorType='"+sensorTypeName+"'", null, -1, -1);
			mv.addObject("monitorLines", monitorLines);
		}
		if(null != monitorlineid && !monitorlineid.isEmpty()){
			List<Sensor> sensors = this.sensorService.query("select obj from Sensor obj where obj.monitorLine.id="+monitorlineid, null, -1, -1);
			Long monitorlineidLong = Long.parseLong(monitorlineid);
			for(Sensor sensor:sensors){
				Af_CoefficientDao.AF_coeffAdd(sensor.getName(),monitorlineidLong);
			}
			List<AxialForceCoefficient>  Af_data = Af_CoefficientDao.getAxialForceCoefficientData(monitorlineidLong);
			mv.addObject("datas", Af_data);
		}
		return mv;
	}

	@RequestMapping({ "/deviceapp/axialForceMondify.htm" })
	public void axialForceMondify(HttpServletRequest request, HttpServletResponse response,String formString) {
		if(null != formString && !formString.isEmpty()){
    		String[] dataArray = formString.split(";");
    		if(null != dataArray && dataArray.length > 0){
    			for(String dataString : dataArray){
    				String[] axialForceValue = dataString.split("=");
    				String axialForceDataString = axialForceValue[1];
    				String[] dataStrArray = axialForceDataString.split(",");
    				String sensorName = dataStrArray[0];
    				if(dataStrArray.length != 4){
    					Utils.setResponse(response, "传感器[" + sensorName + "]数据格式错误!");
    					return;
    				}
    				String calibratedValueString = dataStrArray[1].trim();
    				String frequencyString = dataStrArray[2].trim();
    				String fristFrequencyString = dataStrArray[3].trim();
    				if(null != sensorName && !sensorName.isEmpty() && null != calibratedValueString && !calibratedValueString.isEmpty()
    						&& null != frequencyString && !frequencyString.isEmpty() && null != fristFrequencyString && !fristFrequencyString.isEmpty()){
    					double calibratedValue = 1;
    					double frequency = 0;
    					double fristFrequency = 0;
    					try {
    						calibratedValue = Double.valueOf(calibratedValueString);
    						frequency = Double.valueOf(frequencyString);
    						fristFrequency = Double.valueOf(fristFrequencyString);
    					} catch (Throwable e){
    						Utils.setResponse(response, "传感器[" + sensorName + "]数据格式错误!");
    				        return;
    					}
    					Af_CoefficientDao.AF_coeffMondify(sensorName, calibratedValue, frequency, fristFrequency);
    				}
    			}
    		}
    	}
        Utils.setResponse(response, "保存成功!");
	}
	
}
