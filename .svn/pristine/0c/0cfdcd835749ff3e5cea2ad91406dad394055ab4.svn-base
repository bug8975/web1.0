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
public class AccumulativeDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private ISensorService sensorService;
	
	@Autowired
	public TrendAnalysisDao trendanalysisDao;
	
	@Autowired
	private IMonitorDataViewService monitorDataViewService;
	
	public List<Accumulative> accumulativeLine(long monitorlineid,long modifyTime){
		List<Accumulative> datas = (List<Accumulative>) jdbcTemplate.query("select * from accumulative obj where obj.monitorlineid = ? and modifyTime = ?",new Object[]{monitorlineid,modifyTime}, new BeanPropertyRowMapper(Accumulative.class));
		return datas;
	}

	public List<Accumulative> accumulative(Object sensorName,long beginTime,long endTime){
		List<Accumulative> datas = (List<Accumulative>) jdbcTemplate.query("select * from accumulative obj where obj.sensorName = ? and modifyTime > ? and modifyTime < ?",new Object[]{sensorName,beginTime,endTime}, new BeanPropertyRowMapper(Accumulative.class));
		return datas;
	}

	public List<Accumulative> disTime(String monitorlineid,long beginTime,long endTime){
		List<Accumulative> datas = (List<Accumulative>) jdbcTemplate.query("select distinct obj.modifyTime from  accumulative obj where obj.modifyTime >= ? and obj.modifyTime <=? and monitorlineid = ?",new Object[]{beginTime,endTime,monitorlineid}, new BeanPropertyRowMapper(Accumulative.class));
		return datas;
	}

	//累计形变矫正
	public String accumulateData(Object sensorName,long modifyTime){
		String sumData = (String) jdbcTemplate.queryForObject("select obj.sumData from accumulative obj where obj.sensorName = '" + sensorName + "' and modifyTime = " + modifyTime, String.class);
		return sumData;
	}

	public long accumulateTime(Object sensorName){
		String time = (String) jdbcTemplate.queryForObject("select max(obj.modifyTime) from accumulative obj where obj.sensorName = '" + sensorName + "'", String.class);
		return Long.parseLong(time);
	}

	public long accumulateFirstTime(Object sensorName){
		String time = (String) jdbcTemplate.queryForObject("select min(his_modifyTime) from accumulative obj where obj.sensorName = '" + sensorName + "'", String.class);
		return Long.parseLong(time);
	}

	public int accumulateAdd(long monitorlineid,String sensorName,long modifyTime){
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM accumulative WHERE sensorName=?", 
				new Object[]{sensorName}, Integer.class);
		if (cnt == 0) {
			int id =  jdbcTemplate.queryForInt("select max(obj.id) from accumulative obj");
			String strSQL = "insert into accumulative(id,monitorlineid,sensorName,sumData,modifyTime,his_sumData,his_modifyTime,first_modifyTime,mdata) values (?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					(id + 1),
					monitorlineid,
					sensorName,
					0,
					modifyTime,
					0,
					modifyTime,
					modifyTime,
					0
			});
			return 0;
		}else{
			return 1;
		}
	}

	public int accumulateModify(Long monitorlineid,String sensorName,double sumData,long modifyTime){
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM accumulative WHERE sensorName=? and sumData=? and modifyTime=?", 
				new Object[]{sensorName,sumData,modifyTime}, Integer.class);
		if( cnt == 0){
			/*	long his_modifyTime = (long) jdbcTemplate.queryForObject("select max(obj.modifyTime) from accumulative obj where obj.sensorName = '" + sensorName + "'", Long.class);
			String sumDataSel = (String) jdbcTemplate.queryForObject("select obj.sumData from accumulative obj where modifyTime = "+ his_modifyTime+" and obj.sensorName = '" + sensorName + "'", String.class);
			 */			
			double mdata = CommUtil.roundDouble(sumData);											//本次调整数值
			long his_modifyTime = accumulateTime(sensorName);
			String sumDataSel = accumulateData(sensorName,his_modifyTime);
			long first_modifyTime = accumulateTime(sensorName);				//首次修改时间
			double his_sumData = CommUtil.roundDouble(Double.parseDouble(sumDataSel));
			sumData = CommUtil.roundDouble(mdata + his_sumData);
			if(sumData == his_sumData){
				String strSQL = "UPDATE accumulative SET sumData=?, his_sumData=?, his_modifyTime=? where sensorName=? and modifyTime=?";
				jdbcTemplate.update(strSQL, new Object[]{	
						sumData,
						his_sumData,
						his_modifyTime,
						sensorName,
						modifyTime
				});
			}else{
				String strSQL = "insert into accumulative(monitorlineid,sensorName,sumData,modifyTime,his_sumData,his_modifyTime,first_modifyTime,mdata) values (?,?,?,?,?,?,?,?)";
				jdbcTemplate.update(strSQL, new Object[]{
						monitorlineid,
						sensorName,
						sumData,
						modifyTime,
						his_sumData,
						his_modifyTime,
						first_modifyTime,
						mdata
				});
			}
			return 1;
		}else{
			return 0;
		}
	}
	public int ipRecord(String ipAddress,long modifyTime){
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM ipRecord WHERE modifyTime=?", 
				new Object[]{modifyTime}, Integer.class);
		if (cnt == 0) {
			int id =  jdbcTemplate.queryForInt("select max(obj.id) from ipRecord obj");
			String strSQL = "insert into ipRecord(id,ipAddress,modifyTime) values (?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					(id + 1),
					ipAddress,
					modifyTime
			});
			return 0;
		}else{
			return 1;
		}
	}

}