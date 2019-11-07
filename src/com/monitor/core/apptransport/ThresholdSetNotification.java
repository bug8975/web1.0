package com.monitor.core.apptransport;

public class ThresholdSetNotification {
	private String monitorLineName;
	private double level1SinkingThreshold;
	private double level2SinkingThreshold;
	private double level3SinkingThreshold;
	
	
	public double getLevel1SinkingThreshold() {
		return level1SinkingThreshold;
	}
	public void setLevel1SinkingThreshold(double level1SinkingThreshold) {
		this.level1SinkingThreshold = level1SinkingThreshold;
	}
	public double getLevel2SinkingThreshold() {
		return level2SinkingThreshold;
	}
	public void setLevel2SinkingThreshold(double level2SinkingThreshold) {
		this.level2SinkingThreshold = level2SinkingThreshold;
	}
	public double getLevel3SinkingThreshold() {
		return level3SinkingThreshold;
	}
	public void setLevel3SinkingThreshold(double level3SinkingThreshold) {
		this.level3SinkingThreshold = level3SinkingThreshold;
	}
	public String getMonitorLineName() {
		return monitorLineName;
	}
	public void setMonitorLineName(String monitorLineName) {
		this.monitorLineName = monitorLineName;
	}
	@Override
	public String toString() {
		return "ThresholdSetNotification [monitorLineName=" + monitorLineName + ", level1SinkingThreshold="
				+ level1SinkingThreshold + ", level2SinkingThreshold=" + level2SinkingThreshold
				+ ", level3SinkingThreshold=" + level3SinkingThreshold + "]";
	}
}
