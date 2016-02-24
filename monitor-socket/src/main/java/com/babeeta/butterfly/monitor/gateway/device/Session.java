package com.babeeta.butterfly.monitor.gateway.device;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;

public class Session {
	public boolean result = true;
	public String secureKey;
	public String appId;
	public String clientId;
	public String msgId;
	private final AtomicInteger tagCounter = new AtomicInteger();
	public Channel channel;

	public Session() {
	}

	public int getTag() {
		return tagCounter.incrementAndGet();
	}
}
