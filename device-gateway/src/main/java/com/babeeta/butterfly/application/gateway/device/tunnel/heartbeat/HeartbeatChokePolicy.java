package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;

public class HeartbeatChokePolicy extends HeartbeatPolicy {
	private static final Logger log = LoggerFactory
			.getLogger(HeartbeatChokePolicy.class);

	public HeartbeatChokePolicy(HeartbeatException lastException,
			int lastInterval, ConfigurationProvider configurationProvider) {
		super(lastException, lastInterval, configurationProvider);
		setNextInterval(getLastInterval()
				- getConfigurationProvider().getStep());
		log.debug("[Choke policy] set next internal = [{}]",
				getNextInterval());
	}

	@Override
	public HeartbeatPolicy getNextIntervalPolicy() {
		log.debug("[Choke policy] switch to Preserve policy.");
		return new HeartbeatPreservePolicy(this);
	}
}