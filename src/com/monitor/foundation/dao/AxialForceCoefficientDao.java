package com.monitor.foundation.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.foundation.domain.AxialForceCoefficient;

@Repository
public class AxialForceCoefficientDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	
	public String getCalibratedValue(Object sensorName){
		String calibratedValue = (String) jdbcTemplate.queryForObject("select obj.calibratedValue from axialforce_coefficient obj where obj.sensorName = '" + sensorName + "'", String.class);
		return calibratedValue;
	}
	
	public String getFrequency(Object sensorName){
		String frequency = (String) jdbcTemplate.queryForObject("select obj.frequency from axialforce_coefficient obj where obj.sensorName = '" + sensorName + "'", String.class);
		return frequency;
	}

	public String getFristFrequency(Object sensorName){
		String fristFrequency = (String) jdbcTemplate.queryForObject("select obj.fristFrequency from axialforce_coefficient obj where obj.sensorName = '" + sensorName + "'", String.class);
		return fristFrequency;
	}
	
	public List<AxialForceCoefficient> getAxialForceCoefficientData(long monitorlineid){
		List<AxialForceCoefficient> datas = (List<AxialForceCoefficient>) jdbcTemplate.query("select * from axialforce_coefficient obj where obj.monitorlineid = ? order by obj.sensorName asc",new Object[]{monitorlineid}, new BeanPropertyRowMapper(AxialForceCoefficient.class));
		return datas;
	}
	
	public List<AxialForceCoefficient> getAxialForceCoefficientData(String  sensorName){
		List<AxialForceCoefficient> datas = (List<AxialForceCoefficient>) jdbcTemplate.query("select * from axialforce_coefficient obj where obj.sensorName = ?",new Object[]{sensorName}, new BeanPropertyRowMapper(AxialForceCoefficient.class));
		return datas;
	}

	public int AF_coeffAdd(String sensorName,long monitorlineid){
		double calibratedValue = 1;
		double frequency = 0;
		double fristFrequency = 0;
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM axialforce_coefficient WHERE sensorName=?", 
				new Object[]{sensorName}, Integer.class);
		if (cnt == 0) {
			int id =  jdbcTemplate.queryForInt("select max(obj.id) from axialforce_coefficient obj");
			String strSQL = "insert into axialforce_coefficient(id,sensorName,monitorlineid,calibratedValue,frequency,fristFrequency) values (?,?,?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					(id + 1),
					sensorName,
					monitorlineid,
					calibratedValue,
					frequency,
					fristFrequency
			});
			return 0;
		}else{
			return 1;
		}
	}

	public int AF_coeffMondify(String sensorName,double calibratedValue,double frequency,double fristFrequency){
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM axialforce_coefficient WHERE sensorName=?", 
				new Object[]{sensorName}, Integer.class);
		if( cnt > 0){
			String strSQL = "UPDATE axialforce_coefficient SET calibratedValue=?,frequency=?,fristFrequency=? where sensorName=?";
			jdbcTemplate.update(strSQL, new Object[]{
					calibratedValue,
					frequency,
					fristFrequency,
					sensorName
			});
			return 1;
		}else{
			return 0;
		}
	}
}