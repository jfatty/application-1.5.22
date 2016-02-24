package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit;
import com.babeeta.butterfly.MessageRouting.HeartbeatResponse;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat.DefaultConfigurationProvider;
import com.babeeta.butterfly.application.gateway.device.tunnel.heartbeat.HeartbeatPolicyFactory;
import com.google.protobuf.MessageLite;

public class HeartbeatInitTunnelLet implements TunnelLet<HeartbeatInit> {

	private final static Logger logger = LoggerFactory
			.getLogger(HeartbeatInitTunnelLet.class);

	private final HeartbeatPolicyFactory heartbeatPolicyFactory;

    public static AtomicLong HEARTBEAT_COUNT_INIT = new AtomicLong(0);

	public HeartbeatInitTunnelLet() {
		heartbeatPolicyFactory = new HeartbeatPolicyFactory();
		heartbeatPolicyFactory
				.setConfigurationProvider(new DefaultConfigurationProvider());
	}

	@Override
	public void messageReceived(
			TunnelContext tunnelContext,
			TunnelData<HeartbeatInit> data) {
        HEARTBEAT_COUNT_INIT.getAndIncrement();
		logger.debug("[{}]e:{}, delay:{}, cause:{}", new Object[] {
				tunnelContext.getChannel().getId(),
				data.obj.getLastException(),
				data.obj.getLastTimeout(),
				data.obj.getCause() });

		tunnelContext.setCurrentHeartbeatPolicy(heartbeatPolicyFactory
				.getInstance(
						data.obj.getLastException().toString(),
						data.obj.getLastTimeout()));

		int initial =
				tunnelContext.getCurrentHeartbeatPolicy().getNextInterval();

		tunnelContext.getChannel().write(
				new TunnelData<MessageLite>(data.tag, 2, HeartbeatResponse
						.newBuilder()
						.setDelay(initial)
						.build()));
		
		logger.debug("[{}]Initial heartbeat:{}", tunnelContext.getChannel().getId(), initial);
	}
}