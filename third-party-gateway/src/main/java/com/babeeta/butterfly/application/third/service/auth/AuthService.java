package com.babeeta.butterfly.application.third.service.auth;

public interface AuthService {
	public AuthResult authenticate(String appId, String appKey);
}