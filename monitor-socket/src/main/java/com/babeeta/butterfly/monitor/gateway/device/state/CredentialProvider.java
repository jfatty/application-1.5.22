package com.babeeta.butterfly.monitor.gateway.device.state;

import com.babeeta.butterfly.monitor.gateway.device.Session;

public interface CredentialProvider {
	String getId(Session session);

	String getKey(Session session);
}
