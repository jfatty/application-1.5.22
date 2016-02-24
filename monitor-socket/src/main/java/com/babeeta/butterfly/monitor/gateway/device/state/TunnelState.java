package com.babeeta.butterfly.monitor.gateway.device.state;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import com.babeeta.butterfly.monitor.gateway.device.Session;

public interface TunnelState {
	TunnelState onBegin(Session session, MessageEvent e,
			ChannelHandlerContext ctx);

	TunnelState onMessageReceived(Session session, MessageEvent e,
			ChannelHandlerContext ctx) throws Exception;
}
