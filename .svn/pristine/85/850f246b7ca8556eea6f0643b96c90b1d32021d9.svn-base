package com.monitor.foundation.domain;



public class SensorDataTrendAnalysis {
    private String sensorType;
	private String sensorName;
	private long baseStartTime; //开始时间(什么时候开始采集基准数据)
	private long sampleStartTime; //采样时间 (什么时候开始采集采样数据)
	private double baseAverageValue; //起始值
	private double sampleAverageValue; //采样值
	private long timeSegment = 4 * 3600 * 1000; // 时间段，默认4 hours
	private String unit; //单位
	private double sinkingData; //沉降量:sampleAverageValue-baseAverageValue
	private boolean base;

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
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public long getBaseStartTime() {
		return baseStartTime;
	}
	public void setBaseStartTime(long baseStartTime) {
		this.baseStartTime = baseStartTime;
	}
	public long getSampleStartTime() {
		return sampleStartTime;
	}
	public void setSampleStartTime(long sampleStartTime) {
		this.sampleStartTime = sampleStartTime;
	}
	public double getBaseAverageValue() {
		return baseAverageValue;
	}
	public void setBaseAverageValue(double baseAverageValue) {
		this.baseAverageValue = baseAverageValue;
	}
	public double getSampleAverageValue() {
		return sampleAverageValue;
	}
	public void setSampleAverageValue(double sampleAverageValue) {
		this.sampleAverageValue = sampleAverageValue;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getSinkingData() {
		return sinkingData;
	}
	public void setSinkingData(double sinkingData) {
		this.sinkingData = sinkingData;
	}

	@Override
	public String toString() {
		return "SensorDataTrendAnalysis [sensorType=" + sensorType + ", sensorName=" + sensorName + ", baseStartTime="
				+ baseStartTime + ", sampleStartTime=" + sampleStartTime + ", baseAverageValue=" + baseAverageValue
				+ ", sampleAverageValue=" + sampleAverageValue + ", timeSegment=" + timeSegment + ", unit=" + unit
				+ ", sinkingData=" + sinkingData + ", base=" + base + "]";
	}

}
