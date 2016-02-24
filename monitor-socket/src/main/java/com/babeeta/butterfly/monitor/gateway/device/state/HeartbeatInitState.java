package com.babeeta.butterfly.monitor.gateway.device.state;

import java.rmi.UnexpectedException;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.HeartbeatInit;
import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;
import com.babeeta.butterfly.monitor.gateway.device.Session;
import com.babeeta.butterfly.monitor.gateway.device.TunnelData;
import com.google.protobuf.MessageLite;

public class HeartbeatInitState extends AbstractTunnelState implements
		TunnelState {
	static final Logger logger = LoggerFactory
			.getLogger(HeartbeatInit.class);

	public TunnelState getNextState() {
		return nextState;
	}

	@Override
	public TunnelState onBegin(final Session session, MessageEvent e,
			ChannelHandlerContext ctx) {
		logger.debug("[{}]Sending heartbeat init.", session.channel.getId());
		session.channel.write(new TunnelData<MessageLite>(session.getTag(), 0,
						HeartbeatInit.newBuilder().setCause("none.")
								.setLastTimeout(120)
								.setLastException(HeartbeatException.none)
								.build()))
				.addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						logger.debug("[{}]", session.channel.getId());
					}
				});
		return nextState;
	}

	@Override
	public TunnelState onMessageReceived(Session session, MessageEvent e,
			ChannelHandlerContext ctx) throws UnexpectedException {
		throw new UnsupportedOperationException();
	}
}