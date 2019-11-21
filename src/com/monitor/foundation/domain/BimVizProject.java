package com.monitor.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bimviz_project")
public class BimVizProject {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
	private int id;
	private String bim_projectid;		//
	private int project_id;
	private String projectName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBim_projectid() {
		return bim_projectid;
	}
	public void setBim_projectid(String bim_projectid) {
		this.bim_projectid = bim_projectid;
	}
	public int getProject_id() {
		return project_id;
	}
	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	@Override
	public String toString() {
		return "BimVizProject [id=" + id + ", bim_projectid=" + bim_projectid + ", project_id=" + project_id
		        + ", projectName=" + projectName + "]";
	}
	
}
