package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPool implements ThreadPoolMBean {

	private final ThreadPoolExecutor threadPoolExecutor;

	public ThreadPool(ThreadPoolExecutor threadPoolExecutor) {
		super();
		this.threadPoolExecutor = threadPoolExecutor;
	}

	@Override
	public int getActiveCount() {
		return this.threadPoolExecutor.getActiveCount();
	}

	@Override
	public int getCorePoolSize() {
		return this.threadPoolExecutor.getCorePoolSize();
	}

	@Override
	public int getMaximumPoolSize() {
		return this.threadPoolExecutor.getMaximumPoolSize();
	}

	@Override
	public int getPoolSize() {
		return this.threadPoolExecutor.getPoolSize();
	}

	@Override
	public int getQueueLength() {
		return this.threadPoolExecutor.getQueue().size();
	}

	@Override
	public boolean isAllowCoreThreadTimeout() {
		return this.threadPoolExecutor.allowsCoreThreadTimeOut();
	}

	@Override
	public void setCorePoolSize(int corePoolSize) {
		this.threadPoolExecutor.setCorePoolSize(corePoolSize);
	}

	@Override
	public void setMaximumPoolSize(int maximumPoolSize) {
		this.threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
	}
}
