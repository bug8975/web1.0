package com.monitor.foundation.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.foundation.domain.InitialCollect;
import com.monitor.foundation.domain.InitialValue;
import com.monitor.foundation.domain.MonitorLine;

@Repository
public class InitialValueSettingDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	//初值上报以及预加载设置初值
	public void insertInitialValue(InitialValue initial,long monitorlineid) {
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialValue WHERE collectingTime=? and sensorName=?", 
				new Object[]{initial.getCollectingTime(),initial.getSensorName()}, Integer.class);
		if (cnt == 0) {
			//int id =  jdbcTemplate.queryForInt("select max(obj.id) from initialValue obj");
			String strSQL = "insert into initialValue(sensorName,monitorlineid,firstValueGetType,collectingTime,deviceData0,deviceData1,deviceData2,reserved,initSetStatus) values (?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					//(id + 1),
					initial.getSensorName(),
					monitorlineid,
					initial.getFirstValueGetType(),
					initial.getCollectingTime(),
					initial.getDeviceData0(),
					initial.getDeviceData1(),
					initial.getDeviceData2(),
					initial.getReserved(),
					initial.getInitSetStatus()
			});
		}else{
			String strSQL = "UPDATE initialValue set firstValueGetType=?,deviceData0=?,deviceData1=?,deviceData2=?,reserved=? where sensorName=? and collectingTime=? and monitorlineid=?";
			jdbcTemplate.update(strSQL, new Object[]{
					initial.getFirstValueGetType(),
					initial.getDeviceData0(),
					initial.getDeviceData1(),
					initial.getDeviceData2(),
					initial.getReserved(),
					initial.getSensorName(),
					initial.getCollectingTime(),
					monitorlineid
			});
		}
	}

	//修改初值设置状态,0已下发,1等待下发,2预设值(用于第一次前端操作修改初值)
	public double modifyInitSetStatus(String sensorName) {
		String time = (String) jdbcTemplate.queryForObject("select max(obj.collectingTime) from initialValue obj where obj.sensorName = '" + sensorName + "' and initSetStatus = 1", String.class);
		if(null != time){
			long collectingTime = Long.parseLong(time);
			String strSQL = "UPDATE initialValue set initSetStatus=? where sensorName=? and collectingTime=?";
			jdbcTemplate.update(strSQL, new Object[]{
					0,
					sensorName,
					collectingTime
			});
		}
		return 0;
	}
	//监测是否存在需下发的初值
	public int sendInitial(long monitorlineid){
		int sendCode =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialValue WHERE  monitorlineid=? and (initSetStatus=0 or initSetStatus=1)", 
				new Object[]{monitorlineid}, Integer.class);
		return sendCode;
	}

	//初值下发操作获取存在待发数据的时间
	public long initTime(String sensorName){
		String time = (String) jdbcTemplate.queryForObject("select max(obj.collectingTime) from initialValue obj where obj.sensorName = '" + sensorName + "' and (initSetStatus=0 or initSetStatus=1)", String.class);
		if(null == time ){
			return 0;
		}else{
			return Long.parseLong(time);
		}
	}

	//获取待下发初值具体数据
	public List<InitialValue> getInitialValue(String sensorName,long collectingTime) {
		List<InitialValue> initValue = (List<InitialValue>) jdbcTemplate.query("select * from initialValue obj where obj.sensorName = ? and obj.collectingTime = ?", new Object[]{sensorName,collectingTime}, new BeanPropertyRowMapper(InitialValue.class));
		return initValue;
	}

	//获取最近次修改时间用于前端显示
	public long initialTime(Object sensorName){
		String time = (String) jdbcTemplate.queryForObject("select max(obj.collectingTime) from initialValue obj where obj.sensorName = '" + sensorName + "'", String.class);
		if(null == time ){
			return 0;
		}else{
			return Long.parseLong(time);
		}
	}

	//获取需导出至报表的数据
	public List<InitialValue> getInitialValueObj(String sensorName,long beginTime,long endTime) {
		List<InitialValue> initValue = (List<InitialValue>) jdbcTemplate.query("select * from initialValue obj where obj.sensorName = ? and obj.collectingTime > ? and obj.collectingTime < ?", new Object[]{sensorName,beginTime,endTime}, new BeanPropertyRowMapper(InitialValue.class));
		return initValue;
	}

	//获取导出时间
	public List<InitialValue> initModifyTime(String monitorlineid,long beginTime,long endTime){
		List<InitialValue> datas = (List<InitialValue>) jdbcTemplate.query("select distinct obj.collectingTime from  initialValue obj where obj.collectingTime >= ? and obj.collectingTime <=? and monitorlineid = ?",new Object[]{beginTime,endTime,monitorlineid}, new BeanPropertyRowMapper(InitialValue.class));
		return datas;
	}

	//根据导出时间获取当次修改过的初值
	public List<InitialValue> initValueLine(long monitorlineid,long modifyTime){
		List<InitialValue> datas = (List<InitialValue>) jdbcTemplate.query("select * from initialValue obj where obj.monitorlineid = ? and collectingTime = ?",new Object[]{monitorlineid,modifyTime}, new BeanPropertyRowMapper(InitialValue.class));
		return datas;
	}

	//初值修改方法
	public int initialVlaueModify(String sensorName, long collectingTime, long monitorlineid, double deviceData0, double deviceData1, double deviceData2) {
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialValue WHERE sensorName=? and collectingTime=? and deviceData0=? and deviceData1=? and deviceData2=?", 
				new Object[]{sensorName,collectingTime,deviceData0,deviceData1,deviceData2}, Integer.class);
		if( cnt == 0){
			int unique =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialValue WHERE sensorName=? and (initSetStatus=0 or initSetStatus=1)", 
					new Object[]{sensorName}, Integer.class);
			if( unique > 0 ){
				updateSetstatus(sensorName);
			}
			long firstValueGetType = 0;
			long reserved = 0;
			int initSetStatus = 1;
			String strSQL = "insert into initialValue(sensorName,monitorlineid,firstValueGetType,collectingTime,deviceData0,deviceData1,deviceData2,reserved,initSetStatus) values (?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					sensorName,
					monitorlineid,
					firstValueGetType,
					collectingTime,
					deviceData0,
					deviceData1,
					deviceData2,
					reserved,
					initSetStatus
			});
			return 1;
		}else{
			return 0;
		}
	}

	//采集初值指令
	public int initialCollect(long monitorlineid, long startTime, long stopTime, long sampleInterva) {
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialCollect WHERE monitorlineid=? and startTime=? and stopTime=?", 
				new Object[]{monitorlineid,startTime,stopTime}, Integer.class);
		if (cnt == 0) {
			int setStatus = 1;
			int id =  jdbcTemplate.queryForInt("select max(obj.id) from initialCollect obj");
			String strSQL = "insert into initialCollect(id,monitorlineid,startTime,stopTime,sampleInterva,setStatus) values (?,?,?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					(id + 1),
					monitorlineid,
					startTime,
					stopTime,
					sampleInterva,
					setStatus
			});
			return 1;
		}else{
			return 0;
		}
	}

	//获取采集状态值
	public long collectOrder(long monitorlineid) {
		int collectCode =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialCollect WHERE setStatus=0 or (setStatus=1)",Integer.class);
		return collectCode;
	}

	//采集指令具体值
	public List<InitialCollect> getInitialCollect() {
		List<InitialCollect> collectMsg = (List<InitialCollect>) jdbcTemplate.query("select * from initialCollect obj where obj.setStatus = 0 or (obj.setStatus = 1)", new BeanPropertyRowMapper(InitialCollect.class));
		return collectMsg;
	}

	//设置采集状态
	public double modifyCollectSetStatus() {
		String time = (String) jdbcTemplate.queryForObject("select max(obj.stopTime) from initialCollect obj where setStatus = 1", String.class);
		if(null != time){
			int setStatus = 0;
			long stopTime = Long.parseLong(time);
			String strSQL = "UPDATE initialCollect set setStatus=? and stopTime=? where setStatus != 2";
			jdbcTemplate.update(strSQL, new Object[]{
					setStatus,
					stopTime
			});
		}
		return 0;
	}

	public void updateSetstatus(String sensorName) {
		String strSQL = "UPDATE initialValue SET initSetStatus=? where sensorName=?";
		jdbcTemplate.update(strSQL, new Object[]{
				2,
				sensorName
		});
	}
	//初值模式
	public int setModes(String monitorlineid) {
		int single =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialValue WHERE monitorlineid=? and (initSetStatus=0 or initSetStatus=1)", 
				new Object[]{monitorlineid}, Integer.class);
		int interval =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialCollect WHERE  monitorlineid=? and (setStatus=0 or setStatus=1)", 
				new Object[]{monitorlineid}, Integer.class);
		if(interval > 0){
			return 0;
		}else if( interval == 0 && single > 0 ){
			return 1;
		}else{
			return -1;
		}
	}

	public int collectExist() {
		int counts = (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialCollect WHERE setStatus=0 or (setStatus=1)",Integer.class);
		if(counts > 0){
			return -1;
		}else{
			return 0;
		}
	}
	
	public int collectExist(String monitorlineid) {
		int counts = (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM initialCollect WHERE monitorlineid = " + monitorlineid + " and (setStatus=0 or setStatus=1)",Integer.class);
		if(counts > 0){
			return -1;
		}else{
			return 0;
		}
	}

	public void updateCollectSetstatus() {
		String strSQL = "UPDATE initialCollect SET setStatus=? where setStatus=0 or (setStatus=1)";
		jdbcTemplate.update(strSQL, new Object[]{
				2
		});
	}
}