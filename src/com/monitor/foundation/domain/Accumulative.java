package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accumulative")
public class Accumulative {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
	private int id;
	private String sensorName;
	private int monitorlineid;
	private double sumData;				//调整值
	private long modifyTime;			//修改时间
	private double his_sumData;			//上次调整值
	private long his_modifyTime;		//上次修改时间
	private long first_modifyTime;		//第一次修改时间
	private double mdata;				//本次修改值
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public int getMonitorlineid() {
		return monitorlineid;
	}
	public void setMonitorlineid(int monitorlineid) {
		this.monitorlineid = monitorlineid;
	}
	public double getSumData() {
		return sumData;
	}
	public void setSumData(double sumData) {
		this.sumData = sumData;
	}
	public long getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}
	public double getHis_sumData() {
		return his_sumData;
	}
	public void setHis_sumData(double his_sumData) {
		this.his_sumData = his_sumData;
	}
	public long getHis_modifyTime() {
		return his_modifyTime;
	}
	public void setHis_modifyTime(long his_modifyTime) {
		this.his_modifyTime = his_modifyTime;
	}
	public long getFirst_modifyTime() {
		return first_modifyTime;
	}
	public void setFirst_modifyTime(long first_modifyTime) {
		this.first_modifyTime = first_modifyTime;
	}
	public double getMdata() {
		return mdata;
	}
	public void setMdata(double mdata) {
		this.mdata = mdata;
	}
	@Override
	public String toString() {
		return "Accumulative [id=" + id + ", sensorName=" + sensorName + ", monitorlineid=" + monitorlineid
				+ ", sumData=" + sumData + ", modifyTime=" + modifyTime + ", his_sumData=" + his_sumData
				+ ", his_modifyTime=" + his_modifyTime + ", first_modifyTime=" + first_modifyTime + ", mdata=" + mdata
				+ "]";
	}
	
}
