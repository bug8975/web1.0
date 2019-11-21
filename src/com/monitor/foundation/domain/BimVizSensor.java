package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bimviz_sensor")
public class BimVizSensor {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
	private int id;
	private String global_id;		//
	private String sensor_Name;		//
	private int sensor_id;
	private int monitorline_id;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGlobal_id() {
		return global_id;
	}
	public void setGlobal_id(String global_id) {
		this.global_id = global_id;
	}
	public String getSensor_Name() {
		return sensor_Name;
	}
	public void setSensor_Name(String sensor_Name) {
		this.sensor_Name = sensor_Name;
	}
	public int getSensor_id() {
		return sensor_id;
	}
	public void setSensor_id(int sensor_id) {
		this.sensor_id = sensor_id;
	}
	public int getMonitorline_id() {
		return monitorline_id;
	}
	public void setMonitorline_id(int monitorline_id) {
		this.monitorline_id = monitorline_id;
	}
	@Override
	public String toString() {
		return "BimVizSensor [id=" + id + ", global_id=" + global_id + ", sensor_Name=" + sensor_Name + ", sensor_id="
		        + sensor_id + ", monitorline_id=" + monitorline_id + "]";
	}
	


	
}
