package com.babeeta.butterfly.monitor;

public class MonitorResult {
	public MonitorResult(boolean success, String details) {
		this.success = success;
		this.details = details;
	}

	private boolean success;
	private String details;

	public boolean isSuccess() {
		return success;
	}

	public String getDetails() {
		return details;
	}

	public String toString() {
		if (success) {
			return "success";
		}
		return "failed, cause: " + details;
	}
}
