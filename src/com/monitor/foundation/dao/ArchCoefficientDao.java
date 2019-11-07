package com.monitor.foundation.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.core.constant.Globals;
import com.monitor.core.tools.CommUtil;
import com.monitor.core.tools.Logger;
import com.monitor.foundation.domain.MonitorData;
import com.monitor.foundation.domain.MonitorDataView;
import com.monitor.foundation.domain.Sensor;
import com.monitor.foundation.domain.TrendAnalysis;
import com.monitor.foundation.service.IMonitorDataViewService;
import com.monitor.foundation.service.ISensorService;

@Repository
public class ArchCoefficientDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	public TrendAnalysisDao trendanalysisDao;
	
	@Autowired
	public AccumulativeDao accumulativeDao;
	
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	public int insterOrUpdate(MonitorDataView data){
		int count =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM monitor_dataview WHERE sensor_id=? and sensorType=? and collectingTime=?", 
				new Object[]{data.getSensor().getId(),data.getSensorType(),data.getCollectingTime()}, Integer.class);
		return count;
	}
	
	public void archivingUpdate(final List<MonitorDataView> monitordata){
		//System.out.println("更新start："+Globals.DateFormatWithHM.format(System.currentTimeMillis()));
		String sql = "UPDATE monitor_dataview SET sinkingAccumulation=? where monitorLine_id=? and sensor_id=? and collectingTime=?";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {  
			@Override
            public int getBatchSize() {  
                return monitordata.size();  
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
            }  
			@Override
            public void setValues(PreparedStatement ps, int i)throws SQLException {  
				MonitorDataView linkset = (MonitorDataView) monitordata.get(i);  
                ps.setDouble(1, linkset.getSinkingAccumulation());
                ps.setLong(2, linkset.getMonitorLine().getId());  
                ps.setLong(3, linkset.getSensor().getId());  
                ps.setLong(4, linkset.getCollectingTime());  
            }
        }); 
		//System.out.println("更新stop:"+Globals.DateFormatWithHM.format(System.currentTimeMillis()));
	}
	
	public void archivingInsert(final List<MonitorDataView> monitordata){
		//System.out.println("插入start:"+Globals.DateFormatWithHM.format(System.currentTimeMillis()));
		String sql = "INSERT INTO monitor_dataview(addTime,deleteStatus,sensor_id,monitorLine_id,collectingTime,deviceData,sinkingData,sinkingOffset,sensorType,sinkingAccumulation) VALUES(?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {  
			@Override
            public int getBatchSize() {  
                return monitordata.size();  
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
            }  
			@Override
            public void setValues(PreparedStatement ps, int i)throws SQLException {
				MonitorDataView linkset = (MonitorDataView) monitordata.get(i);  
                ps.setLong(1, linkset.getAddTime());  
                ps.setBoolean(2, linkset.isDeleteStatus());  
                ps.setLong(3, linkset.getSensor().getId());  
                ps.setLong(4, linkset.getMonitorLine().getId());  
                ps.setLong(5, linkset.getCollectingTime());
                ps.setDouble(6, linkset.getDeviceData());
                ps.setDouble(7, linkset.getSinkingData());
                ps.setDouble(8, linkset.getSinkingOffset());
                ps.setString(9, linkset.getSensorType());
                ps.setDouble(10, linkset.getSinkingAccumulation());
            }
        }); 
		//System.out.println("插入stop:"+Globals.DateFormatWithHM.format(System.currentTimeMillis()));
	}
	
	public int archiving(MonitorData data){
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM monitor_dataview WHERE sensor_id=? and sensorType=? and collectingTime=?", 
				new Object[]{data.getSensor().getId(),data.getSensorType(),data.getCollectingTime()}, Integer.class);
		if (cnt > 0) {
			List<Map> monitordata = jdbcTemplate.queryForList("select id from monitor_dataview where sensor_id="+ data.getSensor().getId() + " and sensorType='"+ data.getSensorType() +"' and collectingTime='"+ data.getCollectingTime() +"'");
			for(Map monitorid:monitordata){
				String strSQL = "UPDATE monitor_dataview SET addTime=?,deleteStatus=?,monitorLine_id=?,deviceData=?,sinkingData=?,sinkingOffset=?,sinkingAccumulation=? where id=?";
				jdbcTemplate.update(strSQL, new Object[]{
						data.getAddTime(),
						data.isDeleteStatus(),
						data.getMonitorLine().getId(),
						data.getDeviceData(),
						CommUtil.roundDouble(data.getSinkingData()),
						data.getSinkingOffset(),
						CommUtil.roundDouble(data.getSinkingAccumulation()),
						monitorid.get("id")
				});
			}
			return 1;
		}
		else {
			String strSQL = "INSERT INTO monitor_dataview(addTime,deleteStatus,sensor_id,monitorLine_id,collectingTime,deviceData,sinkingData,sinkingOffset,sensorType,sinkingAccumulation) VALUES(?,?,?,?,?,?,?,?,?,?)";

			jdbcTemplate.update(strSQL, new Object[] {
					data.getAddTime(),
					data.isDeleteStatus(),
					data.getSensor().getId(),
					data.getMonitorLine().getId(),
					data.getCollectingTime(),
					data.getDeviceData(),
					CommUtil.roundDouble(data.getSinkingData()),
					data.getSinkingOffset(),
					data.getSensorType(),
					CommUtil.roundDouble(data.getSinkingAccumulation())
			});
			return 0;
		}
	}

	public String coefficient(Object sensorName){
		String coeff = (String) jdbcTemplate.queryForObject("select obj.coefficient from arch_coefficient obj where obj.sensorName = '" + sensorName + "'", String.class);
		return coeff;
	}

	public String coeff(Object sensorName){
		String coeff = (String) jdbcTemplate.queryForObject("select obj.coeff from arch_coefficient obj where obj.sensorName = '" + sensorName + "'", String.class);
		return coeff;
	}

	public String time(int collectSubint){
		String time =  (String) jdbcTemplate.queryForObject("select obj.time from timetask obj", String.class);
		return time;
	}
	
	public String time(){
		String time =  (String) jdbcTemplate.queryForObject("select obj.time from timetask obj", String.class);
		return time;
	}


	public int timeModify(String time){
		int id = 1;
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM timetask WHERE id=?", 
				new Object[]{id}, Integer.class);
		if( cnt > 0){
			String strSQL = "UPDATE timetask SET time=? where id=1";
			jdbcTemplate.update(strSQL, new Object[]{
					time
			});
			return 1;
		}else{
			return 0;
		}
	}
	public int timeAdd(){
		double id = 1;
		double time = 6;
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM timetask WHERE id=?", 
				new Object[]{id}, Integer.class);
		if (cnt == 0) {
			String strSQL = "insert into timetask(id,time) values (?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					id,
					time
			});
			return 0;
		}else{
			return 1;
		}
	}

	public int coeffAdd(String sensorName){
		double coefficient = 1;
		double coeff = 1;
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM arch_coefficient WHERE sensorName=?", 
				new Object[]{sensorName}, Integer.class);
		if (cnt == 0) {
			int id =  jdbcTemplate.queryForInt("select max(obj.id) from arch_coefficient obj");
			String strSQL = "insert into arch_coefficient(id,sensorName,coefficient,coeff) values (?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					(id + 1),
					sensorName,
					coefficient,
					coeff
			});
			return 0;
		}else{
			return 1;
		}
	}

	public int coeffMondify(String sensorName,double coefficient,double coeff){
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM arch_coefficient WHERE sensorName=?", 
				new Object[]{sensorName}, Integer.class);
		if( cnt > 0){
			String strSQL = "UPDATE arch_coefficient SET coefficient=?,coeff=? where sensorName=?";
			jdbcTemplate.update(strSQL, new Object[]{	
					coefficient,
					coeff,
					sensorName
			});
			return 1;
		}else{
			return 0;
		}
	}

}