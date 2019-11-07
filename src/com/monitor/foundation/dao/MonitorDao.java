package com.monitor.foundation.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor.GOLD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.core.constant.Globals;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Logger;
import com.monitor.foundation.domain.Accumulative;
import com.monitor.foundation.domain.ArchCoefficient;
import com.monitor.foundation.domain.InitialCollect;
import com.monitor.foundation.domain.InitialValue;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataImport;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.MonitorLine;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.SensorType;
import com.monitor.foundation.domain.TrendAnalysis;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.ISensorService;
import com.sun.java.swing.plaf.motif.resources.motif;
import com.monitor.foundation.domain.ArchCoefficient;

@Repository
public class MonitorDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private ISensorService sensorService;
	
	@Autowired
	public TrendAnalysisDao trendanalysisDao;
	
	@Autowired
	private IMonitorDataViewService monitorDataViewService;


	public List<MonitorDataImport> getMonitorDatas() {
		String strSQL = "select * from monitor_dataview";
		return jdbcTemplate.query(strSQL, new Object[]{}, new BeanPropertyRowMapper(MonitorDataImport.class));
	}

	public void UpdateUsingStatus(String sensorTypeName,int usingStatus){
		String strSQL = "UPDATE sensor_type SET usingStatus=? where name = ?";
		jdbcTemplate.update(strSQL, new Object[]{
				usingStatus,
				sensorTypeName
		});
	}

	/*
	 * 更新数据：若传感器名称与时间同时存在，则更新，否则追加
	 * 返回值: 1:更新 2:追加
	 */
	public int UpdateData(MonitorDataImport data) {        
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM monitor_dataview WHERE sensor_id=? and sensorType=? and collectingTime=?", 
				new Object[]{data.getSenser_id(),data.getSensorType(),data.getCollectingTime()}, Integer.class);

		if (cnt > 0) {
			String strSQL = "UPDATE monitor_dataview SET addTime=?,deleteStatus=?,monitorLine_id=?,deviceData=?,sinkingData=?,sinkingOffset=?,sinkingAccumulation=? where sensor_id=? and sensorType=? and collectingTime=?";
			jdbcTemplate.update(strSQL, new Object[]{
					data.getAddTime(),
					data.isDeleteStatus(),
					data.getMonitorLine_id(),
					data.getDeviceData(),
					data.getSinkingData(),
					data.getSinkingOffset(),
					data.getSinkingAccumulation(),
					data.getSenser_id(),
					data.getSensorType(),
					data.getCollectingTime()

			});

			return 1;
		}
		else {
			String strSQL = "INSERT INTO monitor_dataview(addTime,deleteStatus,sensor_id,monitorLine_id,collectingTime,deviceData,sinkingData,sinkingOffset,sensorType,sinkingAccumulation) VALUES(?,?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[] {
					data.getAddTime(),
					data.isDeleteStatus(),
					data.getSenser_id(),
					data.getMonitorLine_id(),
					data.getCollectingTime(),
					data.getDeviceData(),
					data.getSinkingData(),
					data.getSinkingOffset(),
					data.getSensorType(),
					data.getSinkingAccumulation()
			});
			return 2;
		}       
	}

	public void DelSensor(Sensor data) {
		String strSQL = "Delete from sensor where id=?";
		jdbcTemplate.update(strSQL, new Object[]{
				data.getId()
		});
	}

	public void DelMonitorLine(MonitorLine data) {
		String strSQL = "Delete from monitor_line where id=?";
		jdbcTemplate.update(strSQL, new Object[]{
				data.getId()
		});
	}

	public void update(MonitorData[] monitorDatas){
		for(MonitorData data:monitorDatas){
			long currentTime = System.currentTimeMillis();
			data.setAddTime(currentTime);
			String strSQL = "UPDATE monitor_dataview SET addTime=?,deleteStatus=?,monitorLine_id=?,deviceData=?,sinkingData=?,sinkingOffset=?,sinkingAccumulation=? where sensor_id=? and sensorType=? and collectingTime=?";
			jdbcTemplate.update(strSQL, new Object[]{
					data.getAddTime(),
					data.isDeleteStatus(),
					data.getMonitorLine().getId(),
					data.getDeviceData(),
					data.getSinkingData(),
					data.getSinkingOffset(),
					data.getSinkingAccumulation(),
					data.getSensor().getId(),
					data.getSensorType(),
					data.getCollectingTime()

			});
		}
	}

	public void updateForView(MonitorDataView[] monitorDataViews){
		for(MonitorDataView data:monitorDataViews){
			long currentTime = System.currentTimeMillis();
			data.setAddTime(currentTime);
			String strSQL = "UPDATE monitor_dataview SET addTime=?,deleteStatus=?,monitorLine_id=?,deviceData=?,sinkingData=?,sinkingOffset=?,sinkingAccumulation=? where sensor_id=? and sensorType=? and collectingTime=?";
			jdbcTemplate.update(strSQL, new Object[]{
					data.getAddTime(),
					data.isDeleteStatus(),
					data.getMonitorLine().getId(),
					data.getDeviceData(),
					data.getSinkingData(),
					data.getSinkingOffset(),
					data.getSinkingAccumulation(),
					data.getSensor().getId(),
					data.getSensorType(),
					data.getCollectingTime()

			});
		}
	}
	
	public List<MonitorLine> getMonitorLine() {
		List<MonitorLine> lines = (List<MonitorLine>) jdbcTemplate.query("select * from monitor_line", new BeanPropertyRowMapper(MonitorLine.class));
		return lines;
	}

}