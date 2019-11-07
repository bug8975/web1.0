package com.monitor.core.apptransport;

/**
 * heart beat 
 *
 */
public class AppStatusNotification {
	private String monitorLineName;


	public String getMonitorLineName() {
		return monitorLineName;
	}

	public void setMonitorLineName(String monitorLineName) {
		this.monitorLineName = monitorLineName;
	}

	@Override
	public String toString() {
		return "AppStatusNotification [monitorLineName=" + monitorLineName
				+ "]";
	}
	
	
}
