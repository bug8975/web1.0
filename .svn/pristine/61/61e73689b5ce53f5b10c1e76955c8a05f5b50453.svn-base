package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "axialforce_coefficient")
public class AxialForceCoefficient {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
	private int id;
	private long monitorlineid;
	private String sensorName;
	private double calibratedValue;			//标定系数
	private double frequency;				//出厂频率
	private double fristFrequency;			//第一次测量频率
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getMonitorlineid() {
		return monitorlineid;
	}
	public void setMonitorlineid(long monitorlineid) {
		this.monitorlineid = monitorlineid;
	}
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public double getCalibratedValue() {
		return calibratedValue;
	}
	public void setCalibratedValue(double calibratedValue) {
		this.calibratedValue = calibratedValue;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	public double getFristFrequency() {
		return fristFrequency;
	}
	public void setFristFrequency(double fristFrequency) {
		this.fristFrequency = fristFrequency;
	}
	
	
}
