package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;

public class HeartbeatPolicyFactory {

	private ConfigurationProvider configurationProvider = null;

	private static final Logger log = LoggerFactory
			.getLogger(HeartbeatPolicyFactory.class);

	public ConfigurationProvider getConfigurationProvider() {
		return configurationProvider;
	}

	public HeartbeatPolicy getInstance(String lastException, int timeout) {
		HeartbeatException e = convertToEnum(lastException);
		if (e != null) {
			return createNewPolicy(e, timeout);
		} else {
			return null;
		}
	}

	public void setConfigurationProvider(
			ConfigurationProvider configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	private HeartbeatException convertToEnum(String paramE) {
		try {
			return HeartbeatException.valueOf(paramE);
		} catch (Exception ex) {
			log.info(ex.getMessage());
			return null;
		}
	}

	private HeartbeatPolicy createNewPolicy(HeartbeatException e, int timeout) {
		if (HeartbeatException.choke.equals(e)) {
			if (timeout <= configurationProvider.getProxyInterval()) {
				// Apply the proxy policy when the timeout given by client less
				// the sum of min initial interval and max step interval.

				int newTimeout = timeout - configurationProvider.getStep();

				if (newTimeout < configurationProvider.getMinInterval()) {
					newTimeout = configurationProvider.getMinInterval();
				}
				log.debug("[Apply Proxy policy] with initial = {}", newTimeout);
				return new HeartbeatProxyPolicy(e,
						newTimeout,
						configurationProvider);
			} else {
				// Apply decrease policy to handle normal choke.
				log.debug("[Apply Choke policy]");
				return new HeartbeatChokePolicy(e, timeout,
						configurationProvider);
			}
		} else if (HeartbeatException.none.equals(e)) {
			log.debug("[Apply Probe policy]");
			return new HeartbeatProbePolicy(e, timeout,
					configurationProvider);
		} else if (HeartbeatException.exception.equals(e)) {
			log.debug("[Apply Probe policy with min interval {}]",
					configurationProvider.getMinInterval());
			return new HeartbeatProbePolicy(e,
					configurationProvider.getMinInterval(),
					configurationProvider);
		} else {
			log.debug("[Apply Preserve policy]");
			return new HeartbeatPreservePolicy(e, timeout,
					configurationProvider);
		}
	}
}