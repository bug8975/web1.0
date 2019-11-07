package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "initialCollect")
public class InitialCollect {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
	private int id;					
	private int monitorlineid;			//传感线标号
	private long startTime;				//开始时间
	private long stopTime;				//结束时间
	private long sampleInterva;			//采集间隔
	private int setStatus;			    //初始值设置状态
	
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
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getStopTime() {
		return stopTime;
	}
	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}
	public long getSampleInterva() {
		return sampleInterva;
	}
	public void setSampleInterva(long sampleInterva) {
		this.sampleInterva = sampleInterva;
	}
	public int getSetStatus() {
		return setStatus;
	}
	public void setSetStatus(int setStatus) {
		this.setStatus = setStatus;
	}
	@Override
	public String toString() {
		return "InitialCollect [id=" + id + ", monitorlineid=" + monitorlineid + ", startTime=" + startTime
				+ ", stopTime=" + stopTime + ", sampleInterva=" + sampleInterva + ", setStatus=" + setStatus + "]";
	}
	
}
