package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "initialValue")
public class InitialValue {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
	private int id;					
	private String sensorName;			//传感器名称
	private int monitorlineid;			//传感线标号
	private long firstValueGetType;		//获取初值类型编号
	private long collectingTime;		//获取初值的时间
	private double deviceData0;			//初值0, 对应测斜仪/倾角仪X(°)
	private double deviceData1;			//初值1, 对应测斜仪/倾角仪X(°)
	private double deviceData2;			//初值2, 对应测斜仪/倾角仪X(°)
	private long reserved;				//预留字段
	private int initSetStatus;			//初始值设置状态
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMonitorlineid() {
		return monitorlineid;
	}
	public void setMonitorlineid(int monitorlineid) {
		this.monitorlineid = monitorlineid;
	}
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
	public int getInitSetStatus() {
		return initSetStatus;
	}
	public void setInitSetStatus(int initSetStatus) {
		this.initSetStatus = initSetStatus;
	}
	@Override
	public String toString() {
		return "InitialValue [id=" + id + ", sensorName=" + sensorName + ", monitorlineid=" + monitorlineid
				+ ", firstValueGetType=" + firstValueGetType + ", collectingTime=" + collectingTime + ", deviceData0="
				+ deviceData0 + ", deviceData1=" + deviceData1 + ", deviceData2=" + deviceData2 + ", reserved="
				+ reserved + ", initSetStatus=" + initSetStatus + "]";
	}
	
}
