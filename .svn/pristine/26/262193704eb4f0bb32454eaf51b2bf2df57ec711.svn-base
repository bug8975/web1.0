package com.monitor.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.monitor.core.domain.IdEntity;

@Entity
@Table(name = "sensor")
public class Sensor extends IdEntity {
    public static final double INIT_MONITORVALUE = -9999999;

	private String name;
	private int alarmLevel;
	private boolean base;
	private long lastModifyTime;
	private long lastCollectingTime;
	private double lastDeviceData; // 监测点读数, 对应测斜仪/倾角仪X(°)
	private double lastSinkingData; // 即时形变, 对应测斜仪/倾角仪Y(°)
	private double lastSinkingAccumulation; // 累计形变, 对应测斜仪/倾角仪Z(°)
	private double firstDeviceData = INIT_MONITORVALUE;//used to calculate the sinking offset
	private String sensorType;
	
	@ManyToOne
	private MonitorLine monitorLine;
	
	public double getLastSinkingAccumulation() {
		return lastSinkingAccumulation;
	}
	public void setLastSinkingAccumulation(double lastSinkingAccumulation) {
		this.lastSinkingAccumulation = lastSinkingAccumulation;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public MonitorLine getMonitorLine() {
		return monitorLine;
	}
	public void setMonitorLine(MonitorLine monitorLine) {
		this.monitorLine = monitorLine;
	}
	public boolean isBase() {
		return base;
	}
	public void setBase(boolean isBase) {
		this.base = isBase;
	}
	public long getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public long getLastCollectingTime() {
		return lastCollectingTime;
	}
	public void setLastCollectingTime(long lastCollectingTime) {
		this.lastCollectingTime = lastCollectingTime;
	}
	public double getLastDeviceData() {
		return lastDeviceData;
	}
	public void setLastDeviceData(double lastDeviceData) {
		this.lastDeviceData = lastDeviceData;
	}
	public double getLastSinkingData() {
		return lastSinkingData;
	}
	public void setLastSinkingData(double lastSinkingData) {
		this.lastSinkingData = lastSinkingData;
	}
	public double getFirstDeviceData() {
		return firstDeviceData;
	}
	public void setFirstDeviceData(double firstDeviceData) {
		this.firstDeviceData = firstDeviceData;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	@Override
	public String toString() {
		return "Sensor [name=" + name + ", alarmLevel=" + alarmLevel
				+ ", sensorType=" + sensorType
				+ ", base=" + base + ", lastModifyTime=" + lastModifyTime
				+ ", lastCollectingTime=" + lastCollectingTime
				+ ", lastDeviceData=" + lastDeviceData + ", lastSinkingData="
				+ lastSinkingData + ", lastSinkingAccumulation=" + lastSinkingAccumulation
				+ ", firstDeviceData=" + firstDeviceData
				+ ", monitorLine=" + monitorLine + "]";
	}
}
