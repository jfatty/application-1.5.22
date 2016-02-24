package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;

public abstract class HeartbeatPolicy {
	private int initialInterval;
	private HeartbeatException lastException;
	private int nextInterval;
	private int lastInterval;

	private ConfigurationProvider configurationProvider = null;

	public HeartbeatPolicy(HeartbeatException lastException, int lastInterval,
			ConfigurationProvider configurationProvider) {
		this.lastException = lastException;
		nextInterval = lastInterval;
		initialInterval = lastInterval;
		this.lastInterval = lastInterval;
		this.configurationProvider = configurationProvider;
	}

	HeartbeatPolicy(HeartbeatPolicy heartbeatPolicy) {
		initialInterval = heartbeatPolicy.initialInterval;
		lastException = heartbeatPolicy.lastException;
		nextInterval = heartbeatPolicy.lastInterval;
		lastInterval = heartbeatPolicy.lastInterval;
		configurationProvider = heartbeatPolicy.configurationProvider;
	}

	public ConfigurationProvider getConfigurationProvider() {
		return configurationProvider;
	}

	public final int getLastInterval() {
		return lastInterval;
	}

	public HeartbeatException getLastException() {
		return lastException;
	}

	public abstract HeartbeatPolicy getNextIntervalPolicy();

	public int getInitialInterval() {
		return initialInterval;
	}

	public final int getNextInterval() {
		return nextInterval;
	}

	public void onMessage() {

	}

	protected final void setNextInterval(int nextInterval) {
		this.nextInterval = nextInterval;
		lastInterval = nextInterval;
	}

}
