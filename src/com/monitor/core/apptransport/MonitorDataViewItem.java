package com.monitor.core.apptransport;


import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.domain.MonitorDataView;



public class MonitorDataViewItem {
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

	public MonitorDataView toMonitorDataView(){
		MonitorDataView monitorDataView = new MonitorDataView();
		monitorDataView.setCollectingTime(this.getCollectingTime());
		monitorDataView.setDeviceData(CommUtil.roundDouble(this.getDeviceData()));
		monitorDataView.setSinkingData(CommUtil.roundDouble(this.getSinkingData()));
		monitorDataView.setSinkingAccumulation(CommUtil.roundDouble(this.sinkingAccumulation));
		monitorDataView.setSensorType(this.sensorType);// this attribute will be peeked from sensor when persist it
		return monitorDataView;
	}
	
	@Override
	public String toString() {
		return "MonitorDataView [monitorLineName=" + monitorLineName
				+ ", sensorName=" + sensorName + ", sensorType=" + sensorType +", collectingTime="
				+ collectingTime + ", deviceData=" + deviceData
				+ ", sinkingData=" + sinkingData + "]";
	}
}
