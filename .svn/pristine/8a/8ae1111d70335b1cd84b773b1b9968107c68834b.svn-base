package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "manpower_data")
public class ManPowerData {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
	private int id;
	private long uploadTime;
	private	String obserDirection;				//观测方向
	private String nodeName;					//节点名
	private double levelDegree;					//水准度数
	private double stadiaLength;				//前后视距长
	private double stadiaDifference;			//前后视距差
	private double accumulatestadiaDifference;	//累积视距差
	private	double highDifference;				//高差之差
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(long uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getObserDirection() {
		return obserDirection;
	}
	public void setObserDirection(String obserDirection) {
		this.obserDirection = obserDirection;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public double getLevelDegree() {
		return levelDegree;
	}
	public void setLevelDegree(double levelDegree) {
		this.levelDegree = levelDegree;
	}
	public double getStadiaLength() {
		return stadiaLength;
	}
	public void setStadiaLength(double stadiaLength) {
		this.stadiaLength = stadiaLength;
	}
	public double getStadiaDifference() {
		return stadiaDifference;
	}
	public void setStadiaDifference(double stadiaDifference) {
		this.stadiaDifference = stadiaDifference;
	}
	public double getAccumulatestadiaDifference() {
		return accumulatestadiaDifference;
	}
	public void setAccumulatestadiaDifference(double accumulatestadiaDifference) {
		this.accumulatestadiaDifference = accumulatestadiaDifference;
	}
	public double getHighDifference() {
		return highDifference;
	}
	public void setHighDifference(double highDifference) {
		this.highDifference = highDifference;
	}
	@Override
	public String toString() {
		return "ManPowerData [id=" + id + ", uploadTime=" + uploadTime + ", obserDirection=" + obserDirection
				+ ", nodeName=" + nodeName + ", levelDegree=" + levelDegree + ", stadiaLength=" + stadiaLength
				+ ", stadiaDifference=" + stadiaDifference + ", accumulatestadiaDifference="
				+ accumulatestadiaDifference + ", highDifference=" + highDifference + "]";
	}
	
	
}
