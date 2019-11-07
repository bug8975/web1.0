package com.monitor.core.apptransport;

import com.monitor.core.tools.CommUtil;
import com.monitor.foundation.domain.InitialValue;

public class InitialValueItem {
	
	private String sensorName;			//传感器名称
	private long firstValueGetType;		//获取初值类型编号
	private long collectingTime;		//获取初值的时间
	private double deviceData0;			//初值0, 对应测斜仪/倾角仪X(°)
	private double deviceData1;			//初值1, 对应测斜仪/倾角仪X(°)
	private double deviceData2;			//初值2, 对应测斜仪/倾角仪X(°)
	private long reserved;				//预留字段
	
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public long getFirstValueGetType() {
		return firstValueGetType;
	}
	public void setFirstValueGetType(long firstValueGetType) {
		this.firstValueGetType = firstValueGetType;
	}
	public long getCollectingTime() {
		return collectingTime;
	}
	public void setCollectingTime(long collectingTime) {
		this.collectingTime = collectingTime;
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
	public long getReserved() {
		return reserved;
	}
	public void setReserved(long reserved) {
		this.reserved = reserved;
	}
	
	public InitialValue toInitialValue(){
		InitialValue initial = new InitialValue();
		initial.setSensorName(this.sensorName);
		initial.setFirstValueGetType(this.firstValueGetType);
		initial.setCollectingTime(this.collectingTime);
		initial.setDeviceData0(CommUtil.roundDouble(this.deviceData0));
		initial.setDeviceData1(CommUtil.roundDouble(this.deviceData1));
		initial.setDeviceData2(CommUtil.roundDouble(this.deviceData2));
		initial.setReserved(this.reserved);
		return initial;
	}
	
	@Override
	public String toString() {
		return "InitialValueItem [sensorName=" + sensorName + ", firstValueGetType=" + firstValueGetType
				+ ", collectingTime=" + collectingTime + ", deviceData0=" + deviceData0 + ", deviceData1=" + deviceData1
				+ ", deviceData2=" + deviceData2 + ", reserved=" + reserved + "]";
	}
	
}
