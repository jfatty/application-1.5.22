package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;

public class HeartbeatProbePolicy extends HeartbeatPolicy {
	private static final Logger log = LoggerFactory
			.getLogger(HeartbeatProbePolicy.class);

	private boolean ignore = false;

	public HeartbeatProbePolicy(HeartbeatException lastException,
			int lastInterval, ConfigurationProvider configurationProvider) {
		super(lastException, lastInterval, configurationProvider);
		// TODO Auto-generated constructor stub
	}

	HeartbeatProbePolicy(HeartbeatPolicy heartbeatPolicy) {
		super(heartbeatPolicy);
	}

	@Override
	public HeartbeatPolicy getNextIntervalPolicy() {
		if (ignore) {
			ignore = false;
			return this;
		}
		log.debug("[Probe policy]last internal = [{}]", getLastInterval());
		if (getLastInterval() < getConfigurationProvider().getMaxInterval()) {
			// not reach max heartbeat interval,
			int nextInterval = getLastInterval()
							+ getConfigurationProvider().getStep();
			if (nextInterval > getConfigurationProvider().getMaxInterval()) {
				nextInterval = getConfigurationProvider().getMaxInterval();
			}
			setNextInterval(nextInterval);
			log.debug(
					"[Probe policy] increase step! set next internal = [{}] [{}]",
					getNextInterval(), nextInterval);
			return this;
		} else {
			// keep current interval
			log.debug("[Probe policy] switch to Preserve policy.");
			return new HeartbeatPreservePolicy(this);
		}
	}

	@Override
	public void onMessage() {
		ignore = true;
	}
}
