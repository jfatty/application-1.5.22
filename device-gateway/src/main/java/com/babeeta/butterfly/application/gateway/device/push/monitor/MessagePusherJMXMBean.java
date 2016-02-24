package com.babeeta.butterfly.application.gateway.device.push.monitor;

/**
 * Created by IntelliJ IDEA. User: XYuser Date: 11-1-13 Time: 上午9:39 To change
 * this template use File | Settings | File Templates.
 */
public interface MessagePusherJMXMBean {
	long getFailCount();

	long getSucceedCount();

	boolean isDeviceOnline(String deviceId);
}
