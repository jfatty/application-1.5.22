package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

public interface ThreadPoolMBean {
	int getActiveCount();

	int getCorePoolSize();

	int getMaximumPoolSize();

	int getPoolSize();

	int getQueueLength();

	boolean isAllowCoreThreadTimeout();

	void setCorePoolSize(int corePoolSize);

	void setMaximumPoolSize(int getMaximumPoolSize);

}
