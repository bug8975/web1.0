package com.monitor.foundation.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monitor.foundation.domain.Accumulative;
import com.monitor.foundation.domain.BimVizProject;
import com.monitor.foundation.domain.BimVizSensor;
import com.monitor.foundation.domain.Sensor;

@Repository
public class BimVizDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<BimVizProject> getBimProject(){
		List<BimVizProject> datas = (List<BimVizProject>) jdbcTemplate.query("select * from bimviz_project", new BeanPropertyRowMapper(BimVizProject.class));
		return datas;
	}

	public List<BimVizSensor> getBimSensor(int monitorlineId){
		List<BimVizSensor> datas = (List<BimVizSensor>) jdbcTemplate.query("select * from bimviz_sensor obj where obj.monitorlineId = ?",new Object[]{monitorlineId}, new BeanPropertyRowMapper(BimVizSensor.class));
		return datas;
	}

	public List<Sensor> getSensor(String sensorName){
		List<Sensor> datas = (List<Sensor>) jdbcTemplate.query("select * from sensor obj where obj.sensorName = ?",new Object[]{sensorName}, new BeanPropertyRowMapper(Sensor.class));
		return datas;
	}

}