package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

public interface ConfigurationProvider {

	int getStep();

	int getProxyInterval();

	int getDefaultInterval();

	int getMaxInterval();

	int getMinInterval();

	int getHoldTimes();

	int getHoldInterval();
}