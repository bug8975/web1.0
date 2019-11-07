package com.monitor.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.monitor.core.domain.IdEntity;

@Entity
@Table(name = "monitor_data")
public class MonitorData extends IdEntity {
	
	@ManyToOne
	private Sensor sensor;
	
	@ManyToOne
	private MonitorLine monitorLine;
	private long collectingTime;
	private double deviceData; // 监测点读数, 对应测斜仪/倾角仪X(°) -- 设备上报
	private double sinkingData; // 即时形变, 对应测斜仪/倾角仪Y(°) -- 设备上报
	private double sinkingOffset; // (offset of monitor point - offset of base point)*(-1) -- 计算得出
	private double sinkingAccumulation; // 累计形变, 对应测斜仪/倾角仪Z(°) -- 设备上报 
	private String sensorType;

	public double getSinkingAccumulation() {
		return sinkingAccumulation;
	}
	public void setSinkingAccumulation(double sinkingAccumulation) {
		this.sinkingAccumulation = sinkingAccumulation;
	}
	public Sensor getSensor() {
		return sensor;
	}
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	public long getCollectingTime() {
		return collectingTime;
	}
	public void setCollectingTime(long collectingTime) {
		this.collectingTime = collectingTime;
	}
	public double getDeviceData() {
		return deviceData;
	}
	public void setDeviceData(double deviceData) {
		this.deviceData = deviceData;
	}
	public double getSinkingData() {
		return sinkingData;
	}
	public void setSinkingData(double sinkingData) {
		this.sinkingData = sinkingData;
	}
	public MonitorLine getMonitorLine() {
		return monitorLine;
	}
	public void setMonitorLine(MonitorLine monitorLine) {
		this.monitorLine = monitorLine;
	}
	public double getSinkingOffset() {
		return sinkingOffset;
	}
	public void setSinkingOffset(double sinkingOffset) {
		this.sinkingOffset = sinkingOffset;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	
	@Override
	public String toString() {
		String monitorLineName = (monitorLine==null) ? "" : monitorLine.getName();
		String sensorName = (sensor==null) ? "" : sensor.getName();
		return "MonitorData [monitorLine=" + monitorLineName
				+ ", sensorName=" + sensorName + ", collectingTime="
				+ collectingTime + ", deviceData=" + deviceData
				+ ", sensorType=" + sensorType
				+ ", sinkingData=" + sinkingData
				+ ", sinkingAccumulation=" + sinkingAccumulation
				+ ", sinkingOffset=" + sinkingOffset+ "]";
	}
	
	
}
