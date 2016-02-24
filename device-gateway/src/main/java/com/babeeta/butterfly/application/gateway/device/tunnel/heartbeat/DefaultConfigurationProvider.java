package com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfigurationProvider implements ConfigurationProvider {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultConfigurationProvider.class);

	private final static String defaultProfilePath = "HeartbeatPolicy.properties";
	private final static int defaultMaxInterval = 300;
	private final static int defaultMinInterval = 20;
	private final static int defaultProxyInterval = 60;
	private final static int defaultStep = 10;
	private final static int defaultHoldTimes = 100;
	private final static int defaultHoldInterval = 3600;

	private int maxInterval = 0;
	private int minInterval = 0;
	private int proxyInterval = 0;
	private int step = 0;
	private int holdTimes = 0;
	private int holdInterval = 0;

	public DefaultConfigurationProvider() {
		PropertiesConfiguration profile = null;
		try {
			profile = openConfiguration(defaultProfilePath);
		} catch (Exception e) {
			logger.error("Caught exception when loading profile,use default profile: {}", e);
			profile = null;
		}
		if (profile == null) {
			maxInterval = defaultMaxInterval;
			minInterval = defaultMinInterval;
			proxyInterval = defaultProxyInterval;
			step = defaultStep;
			holdTimes = defaultHoldTimes;
			holdInterval = defaultHoldInterval;
		}else{
			maxInterval=profile.getInt("max_interval");
			minInterval=profile.getInt("min_interval");
			proxyInterval=profile.getInt("proxy_interval");
			step=profile.getInt("step");
			holdTimes=profile.getInt("hold_times");
			holdInterval=profile.getInt("hold_interval");
			logger.debug("load config profile success.");
		}
		logger.debug("heartbeat profile is maxInterval:{},minInterval:{},proxyInterval:{},step:{},holdTimes:{},holdInterval:{}",new Object[]{maxInterval,minInterval,proxyInterval,step,holdTimes,holdInterval});
	}

	private PropertiesConfiguration openConfiguration(String f)
			throws ConfigurationException {
		return new PropertiesConfiguration(Thread.currentThread()
				.getContextClassLoader().getResource(f));
	}

	@Override
	public int getStep() {
		return step;
	}

	@Override
	public int getProxyInterval() {
		return proxyInterval;
	}

	@Override
	public int getDefaultInterval() {
		return minInterval;
	}

	@Override
	public int getMaxInterval() {
		return maxInterval;
	}

	@Override
	public int getMinInterval() {
		return minInterval;
	}

	@Override
	public int getHoldTimes() {
		return holdTimes;
	}

	@Override
	public int getHoldInterval() {
		return holdInterval;
	}
}