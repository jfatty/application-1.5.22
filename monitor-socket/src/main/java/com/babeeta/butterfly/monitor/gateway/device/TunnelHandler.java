package com.babeeta.butterfly.monitor.gateway.device;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.monitor.gateway.device.state.TunnelState;
import com.google.protobuf.MessageLite;

public class TunnelHandler extends SimpleChannelHandler {
	Logger logger = LoggerFactory.getLogger(TunnelHandler.class.getName());
	private final Session session;
	private TunnelState currentState = null;

	public TunnelHandler(TunnelState initialState) {
		this.currentState = initialState;
		this.session = new Session();
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelClosed(ctx, e);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		session.channel = e.getChannel();
		TunnelState nextState = currentState.onBegin(session, null, ctx);

		onBegin(ctx, null, nextState);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		HeartbeatService.cancelPushSchedule(e.getChannel().getId());
		if (session.result) {
			System.out.println("Successful!");
		} else {
			System.out.println("Failure!");
		}
		System.exit(0);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getChannel().close();
		System.out.println("已发生错误: " + e.getCause().getMessage());
		System.exit(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (2 == ((TunnelData<MessageLite>) e.getMessage()).cmd) {
			HeartbeatService.getDefaultInstance().onHeartbeat(e, session);
		} else {
			TunnelState nextState = currentState.onMessageReceived(session, e,
					ctx);
			onBegin(ctx, e, nextState);
		}
	}

	private void onBegin(ChannelHandlerContext ctx, MessageEvent e,
			TunnelState nextState) {
		if (currentState != nextState) {
			TunnelState newState = null;
			while (nextState != (newState = nextState.onBegin(session, e,
					ctx))) {
				nextState = newState;
			}
			currentState = nextState;
		}
	}

}
