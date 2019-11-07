package com.monitor.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.monitor.core.domain.IdEntity;

@Entity
@Table(name = "sysconfig")
public class SysConfig extends IdEntity {
	private String name;
	private String value;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "SysConfig [name=" + name + ", value=" + value + "]";
	}
}
