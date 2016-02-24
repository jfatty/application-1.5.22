package com.babeeta.butterfly.monitor;

import java.util.HashMap;
import java.util.Map;

import com.babeeta.butterfly.monitor.app.AppAccountMonitor;
import com.babeeta.butterfly.monitor.app.AppServiceMonitor;
import com.babeeta.butterfly.monitor.app.AppTagMonitor;
import com.babeeta.butterfly.monitor.dev.DevAccountMonitor;
import com.babeeta.butterfly.monitor.dev.DevSubscriptionMonitor;
import com.babeeta.butterfly.monitor.gateway.ThirdPartyGatewayMonitor;

public class MonitorFactory {
	// extend interface
	private static final int THIRD_PARTY_GATEWAY = 2;
	// module belongs to "app" domain
	private static final int APP_ACCOUNT = 21;
	private static final int APP_TAG = 22;
	private static final int APP_SERVICE = 23;
	// module belongs to "dev" domain
	private static final int DEV_ACCOUNT = 31;
	private static final int DEV_SUBSCRIPTION = 32;

	private static Map<String, Integer> MONITOR_MAPPING = new HashMap<String, Integer>();

	static {
		MONITOR_MAPPING.put("third-party-gateway", THIRD_PARTY_GATEWAY);

		MONITOR_MAPPING.put("app-account", APP_ACCOUNT);
		MONITOR_MAPPING.put("app-tag", APP_TAG);
		MONITOR_MAPPING.put("app-service", APP_SERVICE);

		MONITOR_MAPPING.put("dev-account", DEV_ACCOUNT);
		MONITOR_MAPPING.put("dev-subscription", DEV_SUBSCRIPTION);
	}

	public static Monitor getMonitorByType(String type, String host, int port) {
		switch (MONITOR_MAPPING.get(type)) {
		case THIRD_PARTY_GATEWAY:
			return new ThirdPartyGatewayMonitor(host, port);
		case APP_ACCOUNT:
			return new AppAccountMonitor(host, port);
		case APP_TAG:
			return new AppTagMonitor(host, port);
		case APP_SERVICE:
			return new AppServiceMonitor(host, port);
		case DEV_ACCOUNT:
			return new DevAccountMonitor(host, port);
		case DEV_SUBSCRIPTION:
			return new DevSubscriptionMonitor(host, port);
		default:
			break;
		}
		return null;
	}
}
