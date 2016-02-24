package com.babeeta.butterfly.monitor;

public interface MonitorListener {
	boolean isDone();

	MonitorResult getResult();

	void setResult(boolean success, String details);
}