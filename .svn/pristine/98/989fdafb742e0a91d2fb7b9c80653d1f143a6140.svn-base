package com.monitor.core.apptransport;


import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.domain.MonitorData;

public class MonitorDataItem {
	private String sensorName;
	private String monitorLineName;
	private long collectingTime;
	private double deviceData;
	private double sinkingData;
	private double sinkingAccumulation;
	private String sensorType;

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
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public String getMonitorLineName() {
		return monitorLineName;
	}
	public void setMonitorLineName(String monitorLineName) {
		this.monitorLineName = monitorLineName;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	public double getSinkingAccumulation() {
		return sinkingAccumulation;
	}
	public void setSinkingAccumulation(double sinkingAccumulation) {
		this.sinkingAccumulation = sinkingAccumulation;
	}

	public MonitorData toMonitorData(){
		MonitorData monitorData = new MonitorData();
		monitorData.setCollectingTime(this.getCollectingTime());
		monitorData.setDeviceData(CommUtil.roundDouble(this.getDeviceData()));
		monitorData.setSinkingData(CommUtil.roundDouble(this.getSinkingData()));
		monitorData.setSinkingAccumulation(CommUtil.roundDouble(this.sinkingAccumulation));
		monitorData.setSensorType(this.sensorType);// this attribute will be peeked from sensor when persist it
		return monitorData;
	}
	
	
	
	@Override
	public String toString() {
		return "MonitorData [monitorLineName=" + monitorLineName
				+ ", sensorName=" + sensorName + ", sensorType=" + sensorType +", collectingTime="
				+ collectingTime + ", deviceData=" + deviceData
				+ ", sinkingData=" + sinkingData + "]";
	}
}
