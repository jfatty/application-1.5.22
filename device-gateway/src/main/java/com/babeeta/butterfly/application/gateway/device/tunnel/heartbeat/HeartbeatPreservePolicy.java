package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;

public class HeartbeatPreservePolicy extends HeartbeatPolicy {
	private static final Logger log = LoggerFactory
			.getLogger(HeartbeatPreservePolicy.class);
	private int successCounter = 0;

	public HeartbeatPreservePolicy(HeartbeatException lastException,
			int lastInterval, ConfigurationProvider configurationProvider) {
		super(lastException, lastInterval, configurationProvider);
		// TODO Auto-generated constructor stub
	}

	HeartbeatPreservePolicy(HeartbeatPolicy heartbeatPolicy) {
		super(heartbeatPolicy);
	}

	@Override
	public HeartbeatPolicy getNextIntervalPolicy() {
		successCounter++;
		if (successCounter > getConfigurationProvider().getHoldTimes()
				&& getLastInterval() < (getConfigurationProvider()
						.getMaxInterval() - getConfigurationProvider()
						.getStep())) {
			// try to probe
			log.debug("[Preserve policy] switch to Probe policy.");
			return new HeartbeatProbePolicy(this);
		} else if (getLastInterval() > getConfigurationProvider()
				.getMaxInterval()) {
			// replace with max limit interval
			setNextInterval(getConfigurationProvider().getMaxInterval());
			log.debug("[Preserve policy] get max!!!");
			return this;
		} else {
			// keep current interval
			log.debug("[Preserve policy] keep!!!");
			return this;
		}
	}
}
