package com.monitor.core.apptransport;

import com.monitor.foundation.domain.MonitorLine;

public class MonitorLineModifyNotification {
	private String operation;
	private String monitorLineName;
	private String hostName;
	private int port;
	private String sensorType;

	public MonitorLineModifyNotification(){	
	}
	public MonitorLineModifyNotification(String monitorLineName, String hostName, int port, String operation){
		this.monitorLineName = monitorLineName;
		this.hostName = hostName;
		this.port = port;
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getMonitorLineName() {
		return monitorLineName;
	}
	public void setMonitorLineName(String monitorLineName) {
		this.monitorLineName = monitorLineName;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public MonitorLine toMonitorLine(){
		MonitorLine monitorLine = new MonitorLine();
		monitorLine.setHostName(this.getHostName());
		monitorLine.setPort(this.getPort());
		monitorLine.setName(this.getMonitorLineName());
		monitorLine.setSensorType(this.sensorType);
		return monitorLine;
	}
}
