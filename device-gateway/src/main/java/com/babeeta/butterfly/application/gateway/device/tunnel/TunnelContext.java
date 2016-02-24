package com.babeeta.butterfly.application.gateway.device.tunnel;

import org.jboss.netty.channel.Channel;

import com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat.HeartbeatPolicy;

public class TunnelContext {
	private HeartbeatPolicy currentHeartbeatPolicy = null;
	private Channel channel = null;
	private String deviceId;

	public Channel getChannel() {
		return channel;
	}

	public HeartbeatPolicy getCurrentHeartbeatPolicy() {
		return currentHeartbeatPolicy;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setCurrentHeartbeatPolicy(HeartbeatPolicy currentHeartbeatPolicy) {
		this.currentHeartbeatPolicy = currentHeartbeatPolicy;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	void setChannel(Channel channel) {
		this.channel = channel;
	}

}
