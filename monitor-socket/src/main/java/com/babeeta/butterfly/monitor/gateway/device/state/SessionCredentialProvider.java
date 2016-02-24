package com.babeeta.butterfly.monitor.gateway.device.state;

import com.babeeta.butterfly.monitor.gateway.device.Session;

public class SessionCredentialProvider implements CredentialProvider {

	@Override
	public String getId(Session session) {
		return session.clientId;
	}

	@Override
	public String getKey(Session session) {
		return session.secureKey;
	}

}
