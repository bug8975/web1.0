package com.monitor.foundation.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.foundation.domain.ManPowerData;

@Repository
public class ManPowerDao {

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    public List<ManPowerData> getManPowerDatas() {
        String strSQL = "select * from manpower_data";
        return jdbcTemplate.query(strSQL, new Object[]{}, new BeanPropertyRowMapper(ManPowerData.class));
    }
    
	public int commit(ManPowerData data){
		
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM manpower_data WHERE id=? and uploadTime=?", 
                new Object[]{data.getId(),data.getUploadTime()}, Integer.class);
        if (cnt > 0) {
            String strSQL = "UPDATE manpower_data SET addTime=?,deleteStatus=?,monitorLine_id=?,deviceData=?,sinkingData=?,sinkingOffset=?,sinkingAccumulation=? where sensor_id=? and sensorType=? and collectingTime=?";
            jdbcTemplate.update(strSQL, new Object[]{
            		data.getId(),
            		data.getUploadTime(),
            		data.getObserDirection(),
            		data.getNodeName(),
            		data.getLevelDegree(),
            		data.getStadiaLength(),
            		data.getStadiaDifference(),
            		data.getAccumulatestadiaDifference(),
            		data.getHighDifference()
            });
            return 1;
        }
        else {
        	String strSQL = "INSERT INTO manpower_data(uploadTime,obserDirection,nodeName,levelDegree,stadiaLength,stadiaDifference,accumulatestadiaDifference,highDifference) VALUES(?,?,?,?,?,?,?,?)";
            
            jdbcTemplate.update(strSQL, new Object[] {
            		data.getUploadTime(),
            		data.getObserDirection(),
            		data.getNodeName(),
            		data.getLevelDegree(),
            		data.getStadiaLength(),
            		data.getStadiaDifference(),
            		data.getAccumulatestadiaDifference(),
            		data.getHighDifference()
            });
            return 2;
        }       
	}

}
