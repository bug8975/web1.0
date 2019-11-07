package com.monitor.core.apptransport;

import com.monitor.foundation.domain.Sensor;

public class SensorModifyNotification {
	private String operation;
	private String sensorName;
	private String monitorLineName;
	private boolean base;
	private String sensorType;

	public SensorModifyNotification(){
	}
	public SensorModifyNotification(String monitorLineName, String sensorName, String operation, boolean base){
		this.monitorLineName = monitorLineName;
		this.sensorName = sensorName;
		this.operation = operation;
		this.base = base;
	}
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
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
	public boolean isBase() {
		return base;
	}
	public void setBase(boolean base) {
		this.base = base;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public  Sensor toSensor(){
		Sensor sensor = new Sensor();
		sensor.setBase(this.isBase());
		sensor.setName(this.getSensorName());
		sensor.setSensorType(this.sensorType);
		return sensor;
	}
}
