package com.babeeta.butterfly.application.gateway.device.tunnel.let;

public interface AuthTunnelLetMBean {
	public abstract int getHttpClientConnectionsInPool();

	int getFailedCount();

	int getRequestCount();

	int getServiceUnavailableCount();

	int getSuccessCount();

}
