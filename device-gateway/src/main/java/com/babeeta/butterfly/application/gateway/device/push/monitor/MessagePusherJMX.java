package com.babeeta.butterfly.application.gateway.device.push.monitor;

import com.babeeta.butterfly.application.gateway.device.push.MessagePusher;

/**
 * Created by IntelliJ IDEA. User: yinchong Date: 11-1-13 Time: 上午9:41 To change
 * this template use File | Settings | File Templates.
 */
public class MessagePusherJMX implements MessagePusherJMXMBean {
	public long getFailCount() {
		return MessagePusher.PUSH_FAIL_COUNT.getAndSet(0);
	}

	public long getSucceedCount() {
		return MessagePusher.PUSH_SUCCEED_COUNT.getAndSet(0);
	}

	@Override
	public boolean isDeviceOnline(String deviceId) {
		return MessagePusher.getDefaultInstance().resolve(deviceId) != null;
	}
}
