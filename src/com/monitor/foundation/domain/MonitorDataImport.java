package com.monitor.foundation.domain;


public class MonitorDataImport {
	
	private Sensor sensor;
	
	private int id;
	private long addTime;
	private boolean deleteStatus;
	private long senser_id;
	private long monitorLine_id;
	private long collectingTime;
	private double deviceData; // 监测点读数, 对应测斜仪/倾角仪X(°) -- 设备上报
	private double sinkingData; // 即时形变, 对应测斜仪/倾角仪Y(°) -- 设备上报
	private double sinkingOffset; // (offset of monitor point - offset of base point)*(-1) -- 计算得出
	private String sensorType;
	private double sinkingAccumulation; // 累计形变, 对应测斜仪/倾角仪Z(°) -- 设备上报 

	public MonitorDataImport() {
		this.id = 0;
		this.addTime = 0;
		this.deleteStatus = false;
		this.senser_id = 0;
		this.monitorLine_id = 0;
		this.collectingTime = 0;
		this.deviceData = 0;
		this.sinkingData = 0;
		this.sinkingOffset = 0;
		this.sensorType = "";
		this.sinkingAccumulation = 0.00;
	}

	public MonitorDataImport(long addTime, boolean deleteStatus, long senser_id, long monitorLine_id,
			long collectingTime, double deviceData, double sinkingData, double sinkingOffset, String sensorType,
			double sinkingAccumulation) {
		super();
		this.id = id;
		this.addTime = addTime;
		this.deleteStatus = deleteStatus;
		this.senser_id = senser_id;
		this.monitorLine_id = monitorLine_id;
		this.collectingTime = collectingTime;
		this.deviceData = deviceData;
		this.sinkingData = sinkingData;
		this.sinkingOffset = sinkingOffset;
		this.sensorType = sensorType;
		this.sinkingAccumulation = sinkingAccumulation;
	}
	
	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getAddTime() {
		return addTime;
	}
	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}
	public boolean isDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	public long getSenser_id() {
		return senser_id;
	}
	public void setSenser_id(long senser_id) {
		this.senser_id = senser_id;
	}
	public long getMonitorLine_id() {
		return monitorLine_id;
	}
	public void setMonitorLine_id(long monitorLine_id) {
		this.monitorLine_id = monitorLine_id;
	}
	public double getSinkingAccumulation() {
		return sinkingAccumulation;
	}
	public void setSinkingAccumulation(double sinkingAccumulation) {
		this.sinkingAccumulation = sinkingAccumulation;
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
		return "MonitorDataImport [id=" + id + ", addTime=" + addTime + ", deleteStatus=" + deleteStatus
				+ ", senser_id=" + senser_id + ", monitorLine_id=" + monitorLine_id + ", collectingTime="
				+ collectingTime + ", deviceData=" + deviceData + ", sinkingData=" + sinkingData + ", sinkingOffset="
				+ sinkingOffset + ", sensorType=" + sensorType + ", sinkingAccumulation=" + sinkingAccumulation + "]";
	}

}
