package com.monitor.core.apptransport;

public class FirstValueNotification {

	private String sensorName;
	private double deviceData0;
	private double deviceData1;
	private double deviceData2;
	
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public double getDeviceData0() {
		return deviceData0;
	}
	public void setDeviceData0(double deviceData0) {
		this.deviceData0 = deviceData0;
	}
	public double getDeviceData1() {
		return deviceData1;
	}
	public void setDeviceData1(double deviceData1) {
		this.deviceData1 = deviceData1;
	}
	public double getDeviceData2() {
		return deviceData2;
	}
	public void setDeviceData2(double deviceData2) {
		this.deviceData2 = deviceData2;
	}
	@Override
	public String toString() {
		return "FirstValueNotification [sensorName=" + sensorName + ", deviceData0=" + deviceData0 + ", deviceData1="
				+ deviceData1 + ", deviceData2=" + deviceData2 + "]";
	}
	
}
