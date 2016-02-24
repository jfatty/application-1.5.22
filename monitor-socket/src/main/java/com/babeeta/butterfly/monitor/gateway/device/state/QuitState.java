package com.babeeta.butterfly.monitor.gateway.device.state;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.monitor.gateway.device.Session;

public class QuitState extends AbstractTunnelState {

	private static final Logger logger = LoggerFactory
			.getLogger(QuitState.class);

	@Override
	public TunnelState onBegin(Session session, MessageEvent e,
			ChannelHandlerContext ctx) {
		logger.info("[{}]Closing connection.", session.channel.getId());
		session.channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(
				ChannelFutureListener.CLOSE);
		return this;
	}

	@Override
	public TunnelState onMessageReceived(Session session, MessageEvent e,
			ChannelHandlerContext ctx) throws Exception {

		return null;
	}

}
