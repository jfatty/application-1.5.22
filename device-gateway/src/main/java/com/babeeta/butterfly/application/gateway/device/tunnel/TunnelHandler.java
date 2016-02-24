package com.babeeta.butterfly.application.gateway.device.tunnel;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.gateway.device.push.BroadcastService;
import com.babeeta.butterfly.application.gateway.device.push.MessagePusher;
import com.google.protobuf.MessageLite;

public class TunnelHandler extends IdleStateAwareChannelUpstreamHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(TunnelHandler.class);

	private final ChannelGroup channelGroup;
	private final TunnelLetFactory tunnelLetFactory;
	private final TunnelContext tunnelContext;

	public TunnelHandler(ChannelGroup channelGroup,
							TunnelLetFactory tunnelLetFactory) {
		this.channelGroup = channelGroup;
		this.tunnelLetFactory = tunnelLetFactory;
		tunnelContext = new TunnelContext();
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		String cause=e.getFuture().getCause()==null?null:e.getFuture().getCause().getMessage();
		logger.debug("[{}] [{}] Channel Closed.", new Object[]{e.getChannel().getId(),e.getChannel().getRemoteAddress(),cause});
		super.channelClosed(ctx, e);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		channelGroup.add(ctx.getChannel());
		tunnelContext.setChannel(e.getChannel());
		logger.debug("[{}] [{}] Channel Connected.", e.getChannel().getId(),e.getChannel().getRemoteAddress());
	}

	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
			throws Exception {
		logger.info("[{}] [{}] reader Idle.", e.getChannel().getId(),e.getChannel().getRemoteAddress());
		e.getChannel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.info("[{}] [{}] exception Caught [{}]",new Object[]{ e.getChannel().getId(),e.getChannel().getRemoteAddress(),
				e.getCause()});
		e.getChannel().close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (!(e.getMessage() instanceof TunnelData)) {
			return;
		}
		TunnelData<MessageLite> data = (TunnelData<MessageLite>) e
				.getMessage();

		TunnelLet<MessageLite> tunnelLet = (TunnelLet<MessageLite>) tunnelLetFactory
				.getTunnelLet(data.cmd);
		if (tunnelLet == null) {
			logger.error("Unkown command: {},address {}", data.obj.getClass().getName() , e.getChannel().getRemoteAddress());
		} else {
			logger.debug("[Tunnel command: {}] [TunnelLet: {}] [address: {}]", new Object[]{data.cmd,tunnelLet.getClass().getName(),e.getChannel().getRemoteAddress()});
			tunnelLet
					.messageReceived(
							tunnelContext,
							data);
		}
	}
}