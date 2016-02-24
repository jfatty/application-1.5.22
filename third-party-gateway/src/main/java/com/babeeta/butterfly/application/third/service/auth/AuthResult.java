package com.babeeta.butterfly.application.third.service.auth;

public class AuthResult {
	public AuthResult(boolean success, AuthFailedReason failedReason) {
		this.success = success;
		this.failedReason = failedReason;
	}

	private boolean success;
	private AuthFailedReason failedReason;

	public boolean isSuccess() {
		return success;
	}

	public AuthFailedReason getFailedReason() {
		return failedReason;
	}
}