package com.monitor.foundation.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.foundation.domain.TrendAnalysis;

@Repository
public class TrendAnalysisDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public List<TrendAnalysis> getMsg() {
		List<TrendAnalysis> timelist = jdbcTemplate.query("select * from trend_analysis",new BeanPropertyRowMapper(TrendAnalysis.class));
		return timelist;
	}
	
	public List<TrendAnalysis> getData(long monitorlineid) {
		List<TrendAnalysis> timelist = jdbcTemplate.query("select * from trend_analysis where monitorLine_id = ?",new Object[]{monitorlineid},new BeanPropertyRowMapper(TrendAnalysis.class));
		return timelist;
	}
	
	public int commit(long compareTime,long interval,long monitorLineid){
		int cnt = 0;
		cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) FROM trend_analysis where monitorLine_id = ?",new Object[]{monitorLineid},Integer.class);
		String sensorType = "P-levelingTransducer";
		if(cnt > 0){
			String strSQL = "UPDATE trend_analysis SET compareTime=?,trendInterval=? where sensorType = ? and monitorLine_id = ?";
			jdbcTemplate.update(strSQL, new Object[]{
					compareTime,
					interval,
					sensorType,
					monitorLineid
			});
			return 1;
		}else{
			String strSQL = "insert into trend_analysis(compareTime,trendInterval,sensorType,monitorLine_id) values(?,?,?,?)";
			jdbcTemplate.update(strSQL, new Object[]{
					compareTime,
					interval,
					sensorType,
					monitorLineid
			});
			return 0;
		}
	}
	
	public int trendDelete(long monitorlineid){
		int cnt =  (int) jdbcTemplate.queryForObject("SELECT COUNT(1) from trend_analysis where monitorLine_id = ?", 
				new Object[]{monitorlineid}, Integer.class);
		if( cnt > 0){
			String strSQL = "delete from trend_analysis where monitorLine_id=?";
			jdbcTemplate.update(strSQL, new Object[]{
					monitorlineid
			});
			return 1;
		}else{
			return 0;
		}
	}
}
