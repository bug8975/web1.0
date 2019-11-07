package com.monitor.core.apptransport;

public class ThresholdSetStatusNotification {
	private String monitorLineName;
	private int resultCode;
	private String resultString;

	public ThresholdSetStatusNotification(){
	}

	public ThresholdSetStatusNotification(String monitorLineName, int resultCode, String resultString){
		this.monitorLineName = monitorLineName;
		this.resultCode = resultCode;
		this.resultString = resultString;
	}
	
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultString() {
		return resultString;
	}
	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
	public String getMonitorLineName() {
		return monitorLineName;
	}
	public void setMonitorLineName(String monitorLineName) {
		this.monitorLineName = monitorLineName;
	}

	@Override
	public String toString() {
		return "ThresholdSetStatusNotify [monitorLineName=" + monitorLineName
				+ ", resultCode=" + resultCode + ", resultString="
				+ resultString + "]";
	}
}
