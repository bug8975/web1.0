package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trend_analysis")
public class TrendAnalysis {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
	private int id;
	private long monitorLine_id;
	private String sensorType;
	private long compareTime;			//对比时间
	private long trendInterval;			//数据时间间隔
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getMonitorLine_id() {
		return monitorLine_id;
	}
	public void setMonitorLine_id(long monitorLine_id) {
		this.monitorLine_id = monitorLine_id;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	public long getCompareTime() {
		return compareTime;
	}
	public void setCompareTime(long compareTime) {
		this.compareTime = compareTime;
	}
	public long getTrendInterval() {
		return trendInterval;
	}
	public void setTrendInterval(long trendInterval) {
		this.trendInterval = trendInterval;
	}
	
}
