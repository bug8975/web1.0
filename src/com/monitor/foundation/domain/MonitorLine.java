package com.monitor.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.monitor.core.domain.IdEntity;

@Entity
@Table(name = "monitor_line")
public class MonitorLine extends IdEntity {
	public static final int ALIVESTATUS_ONLINE = 0;
	public static final int ALIVESTATUS_OFFLINE = 1;
	public static final long INIT_BASEPOINT_SENSORID = -1;

	private String name;
	private int aliveStatus;
	private long lastHeartBeatTime;
	private String hostName;
	private int port;
	private boolean alarmStatus; // true=alarm, false=normal
	private long lastModifyTime;
	private double sinkingthresholdl1;
	private double sinkingthresholdl2;
	private double sinkingthresholdl3;
	private int thresholdSetStatus;
	private long basePointSensorId = INIT_BASEPOINT_SENSORID;
	private String sensorType;

	
	
	public MonitorLine() {
		this.name = "";
		this.aliveStatus = ALIVESTATUS_OFFLINE;
		this.lastHeartBeatTime = 0;
		this.hostName = "";
		this.port = 0;
		this.alarmStatus = false;
		this.lastModifyTime = 0;
		this.sinkingthresholdl1 = 6;
		this.sinkingthresholdl2 = 8;
		this.sinkingthresholdl3 = 10;
		this.thresholdSetStatus = 0;
		this.sensorType = "";
	}
	public MonitorLine(String name, int aliveStatus, long lastHeartBeatTime, String hostName, int port,
			boolean alarmStatus, long lastModifyTime, double sinkingthresholdl1, double sinkingthresholdl2,
			double sinkingthresholdl3, int thresholdSetStatus, String sensorType) {
		super();
		this.name = name;
		this.aliveStatus = aliveStatus;
		this.lastHeartBeatTime = lastHeartBeatTime;
		this.hostName = hostName;
		this.port = port;
		this.alarmStatus = alarmStatus;
		this.lastModifyTime = lastModifyTime;
		this.sinkingthresholdl1 = sinkingthresholdl1;
		this.sinkingthresholdl2 = sinkingthresholdl2;
		this.sinkingthresholdl3 = sinkingthresholdl3;
		this.thresholdSetStatus = thresholdSetStatus;
		this.sensorType = sensorType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAliveStatus() {
		return aliveStatus;
	}
	public void setAliveStatus(int aliveStatus) {
		this.aliveStatus = aliveStatus;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isAlarmStatus() {
		return alarmStatus;
	}
	public void setAlarmStatus(boolean alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public long getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public double getSinkingthresholdl1() {
		return sinkingthresholdl1;
	}
	public void setSinkingthresholdl1(double sinkingthresholdl1) {
		this.sinkingthresholdl1 = sinkingthresholdl1;
	}
	public double getSinkingthresholdl2() {
		return sinkingthresholdl2;
	}
	public void setSinkingthresholdl2(double sinkingthresholdl2) {
		this.sinkingthresholdl2 = sinkingthresholdl2;
	}
	public double getSinkingthresholdl3() {
		return sinkingthresholdl3;
	}
	public void setSinkingthresholdl3(double sinkingthresholdl3) {
		this.sinkingthresholdl3 = sinkingthresholdl3;
	}
	public long getBasePointSensorId() {
		return basePointSensorId;
	}
	public void setBasePointSensorId(long basePointSensorId) {
		this.basePointSensorId = basePointSensorId;
	}
	public long getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}
	public void setLastHeartBeatTime(long lastHeartBeatTime) {
		this.lastHeartBeatTime = lastHeartBeatTime;
	}
	public int getThresholdSetStatus() {
		return thresholdSetStatus;
	}
	public void setThresholdSetStatus(int thresholdSetStatus) {
		this.thresholdSetStatus = thresholdSetStatus;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	@Override
	public String toString() {
		return "MonitorLine [name=" + name + ", aliveStatus=" + aliveStatus
				+ ", sensorType=" + sensorType
				+ ", lastHeartBeatTime=" + lastHeartBeatTime + ", hostName=" + hostName
				+ ", port=" + port + ", alarmStatus=" + alarmStatus
				+ ", lastModifyTime=" + lastModifyTime
				+ ", sinkingthresholdl1=" + sinkingthresholdl1
				+ ", sinkingthresholdl2=" + sinkingthresholdl2
				+ ", sinkingthresholdl3=" + sinkingthresholdl3
				+ ", thresholdSetStatus=" + thresholdSetStatus
				+ ", basePointSensorId=" + basePointSensorId + "]";
	}
}
