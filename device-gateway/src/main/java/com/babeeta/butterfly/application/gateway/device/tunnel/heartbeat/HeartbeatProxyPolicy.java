package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;

public class HeartbeatProxyPolicy extends HeartbeatPolicy {
	private static final Logger log = LoggerFactory
			.getLogger(HeartbeatProxyPolicy.class);
	private int successCounter = 0;
	private long keepDuration = 0;

	public HeartbeatProxyPolicy(HeartbeatException lastException,
			int lastInterval, ConfigurationProvider configurationProvider) {
		super(lastException, lastInterval, configurationProvider);

		int newTimeout = lastInterval;

		if (newTimeout < configurationProvider.getMinInterval()) {
			newTimeout = configurationProvider.getMinInterval();
		}
		setNextInterval(newTimeout);
	}

	@Override
	public HeartbeatPolicy getNextIntervalPolicy() {
		if (successCounter > 0) {
			keepDuration += getLastInterval();
		}
		successCounter++;

		if (keepDuration >= getConfigurationProvider().getHoldInterval()) {
			// reached hold interval, check last interval whether reached normal
			// heartbeat interval.
			if (getLastInterval() < getConfigurationProvider()
					.getProxyInterval()) {
				// not reach normal heartbeat interval,
				int nextInterval = getLastInterval()
							+ getConfigurationProvider().getStep();
				if (nextInterval > getConfigurationProvider()
						.getProxyInterval()) {
					nextInterval = getConfigurationProvider()
							.getProxyInterval();
				}
				setNextInterval(nextInterval);
				successCounter = 0;
				log.debug(
						"[Proxy policy] increase step! set next internal = [{}]",
						getNextInterval());
			} else {
				// keep current interval
				log.debug("[Proxy policy] switch to Probe policy.");
				return new HeartbeatProbePolicy(this);
			}
		} else {
			// keep current interval
			setNextInterval(getLastInterval());
			log.debug(
					"[Proxy policy] keep duration = [{}]. set next internal = [{}]",
					keepDuration,
					getNextInterval());
		}
		return this;
	}
}
