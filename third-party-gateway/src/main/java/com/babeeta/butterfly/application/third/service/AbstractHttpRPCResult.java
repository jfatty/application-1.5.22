package com.babeeta.butterfly.application.third.service;

public abstract class AbstractHttpRPCResult {
	public AbstractHttpRPCResult(boolean success, int statusCode) {
		this.success = success;
		this.statusCode = statusCode;
	}

	private boolean success;
	private int statusCode;

	public boolean isSuccess() {
		return success;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
