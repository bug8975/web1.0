package com.monitor.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.monitor.core.domain.IdEntity;

@Entity
@Table(name = "sensor_type")
public class SensorType extends IdEntity {
	public static final int STATUS_USING = 0;
	public static final int STATUS_NOTUSING = -1;

	private String name; // sensor type name
	private String displayName; // used in UI to be displayed
	private int usingStatus; // 0:using,-1:NoUsing ...
	private String unit;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getUsingStatus() {
		return usingStatus;
	}
	public void setUsingStatus(int usingStatus) {
		this.usingStatus = usingStatus;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "SensorType [name=" + name + ", displayName=" + displayName
				+ ", usingStatus=" + usingStatus + ", unit=" + unit + "]";
	}
}
