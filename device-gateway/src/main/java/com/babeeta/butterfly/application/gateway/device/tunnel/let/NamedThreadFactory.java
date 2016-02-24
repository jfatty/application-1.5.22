package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	private static final AtomicInteger counter = new AtomicInteger(
			0);

	private final String prefix;

	public NamedThreadFactory(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, prefix
				+ counter.incrementAndGet());
	}

}
