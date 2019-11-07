package com.monitor.action.picreport;

import java.util.List;

import com.monitor.core.domain.virtual.SysMap;
import com.monitor.core.query.support.IPageList;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.query.MonitorDataQueryObject;
import com.monitor.foundation.service.IMonitorDataService;
import com.monitor.foundation.service.IMonitorDataViewService;

public class Utils {
	public static List<MonitorDataView> querySensorMonitorData(IMonitorDataViewService dataViewService, 
			long sensorid, long beginTimeLong, long endTimeTimeLong) {
		MonitorDataQueryObject qo = new MonitorDataQueryObject();
		qo.setPageSize(Integer.MAX_VALUE);
		qo.addQuery("obj.sensor.id", new SysMap("sensorid", sensorid), "=");
		if(0 != beginTimeLong && 0 != endTimeTimeLong){
			//			try {
			//				Date beginTimeDate = dateFormatWithHM.parse(beginTime);
			//		    	long beginTimeLong = beginTimeDate.getTime();
			//		    	Date endTimeTimeDate = dateFormatWithHM.parse(endTime); 
			//		    	long endTimeTimeLong = endTimeTimeDate.getTime();
			//		    	endTimeTimeLong = endTimeTimeLong + (23*3600+59*60+59)*1000;
			qo.addQuery("obj.collectingTime",
					new SysMap("beginCollectingTime", Long.valueOf(beginTimeLong)), ">=");
			qo.addQuery("obj.collectingTime",
					new SysMap("endCollectingTime", Long.valueOf(endTimeTimeLong)), "<=");
			//			} catch (ParseException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
		}
		qo.setOrderBy("collectingTime");
		qo.setOrderType("ASC");

		IPageList pList = dataViewService.list(qo);
		List<MonitorDataView> results = pList.getResult();
		return results;
	}

	public static List<MonitorDataView> queryMonitorData(IMonitorDataViewService dataViewService, 
			long monitorlineid, long collectingTime) {
		MonitorDataQueryObject qo = new MonitorDataQueryObject();
		qo.setPageSize(Integer.MAX_VALUE);
		qo.addQuery("obj.monitorLine.id", new SysMap("monitorlineid", monitorlineid), "=");
		if(0 != collectingTime){
			qo.addQuery("obj.collectingTime",
					new SysMap("collectingTime", Long.valueOf(collectingTime)), "=");
		}
		qo.setOrderBy("sensor.id");
		qo.setOrderType("ASC");

		IPageList pList = dataViewService.list(qo);
		List<MonitorDataView> results = pList.getResult();
		return results;
	}

	public static List<MonitorDataView> multiMonitorData(IMonitorDataViewService dataViewService, 
			long monitorlineid, long beginTimeLong, long endTimeTimeLong) {
		MonitorDataQueryObject qo = new MonitorDataQueryObject();
		qo.setPageSize(Integer.MAX_VALUE);
		qo.addQuery("obj.monitorLine.id", new SysMap("monitorlineid", monitorlineid), "=");
		if(0 != beginTimeLong && 0 != endTimeTimeLong){
			qo.addQuery("obj.collectingTime",
					new SysMap("beginCollectingTime", Long.valueOf(beginTimeLong)), ">=");
			qo.addQuery("obj.collectingTime",
					new SysMap("endCollectingTime", Long.valueOf(endTimeTimeLong)), "<=");
		}
		qo.setOrderBy("sensor.id");
		qo.setOrderType("ASC");

		IPageList pList = dataViewService.list(qo);
		List<MonitorDataView> results = pList.getResult();
		return results;
	}
	
	public static List<MonitorDataView> queryRobotData(IMonitorDataViewService dataViewService, 
			long monitorlineid, long collectingTime,String robotOrientation) {
		MonitorDataQueryObject qo = new MonitorDataQueryObject();
		qo.setPageSize(Integer.MAX_VALUE);
		if("SR".equals(robotOrientation)){
			qo.addQuery("obj.monitorLine.id", new SysMap("monitorlineid", monitorlineid), "<=");
		}else if("SL".equals(robotOrientation)){
			qo.addQuery("obj.monitorLine.id", new SysMap("monitorlineid", monitorlineid), ">");
		}
		if(0 != collectingTime){
			qo.addQuery("obj.collectingTime",
					new SysMap("collectingTime", Long.valueOf(collectingTime)), "=");
		}
		qo.setOrderBy("sensor.id");
		qo.setOrderType("ASC");

		IPageList pList = dataViewService.list(qo);
		List<MonitorDataView> results = pList.getResult();
		return results;
	}

}
