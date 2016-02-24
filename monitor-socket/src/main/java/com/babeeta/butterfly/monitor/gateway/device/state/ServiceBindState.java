package com.babeeta.butterfly.monitor.gateway.device.state;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.ServiceBind;
import com.babeeta.butterfly.monitor.gateway.device.Session;
import com.babeeta.butterfly.monitor.gateway.device.TunnelData;

public class ServiceBindState extends AbstractTunnelState {
	private String aid = "43c5f2f8d3bc4fe49b6465752ba57682";
	private static final Logger logger = LoggerFactory
			.getLogger(ServiceBindState.class);

	@Override
	public TunnelState onBegin(final Session session, MessageEvent e,
			ChannelHandlerContext ctx) {
		session.appId = aid;
		session.channel.write(
				new TunnelData<ServiceBind>(session.getTag(), 133, ServiceBind
						.newBuilder()
						.setApplicationId(aid)
						.build()))
				.addListener(
						new ChannelFutureListener() {

							@Override
							public void operationComplete(ChannelFuture future)
									throws Exception {
								logger.debug("[{}]Binding...",
										session.channel.getId());
							}
						});
		return this;
	}

	@Override
	public TunnelState onMessageReceived(Session session, MessageEvent e,
			ChannelHandlerContext ctx) throws Exception {
		@SuppressWarnings("unchecked")
		TunnelData<ServiceBind> result = (TunnelData<ServiceBind>) e
				.getMessage();
		logger.debug("CID:[{}]", result.obj.getClientId());
		session.clientId = result.obj.getClientId();
		return nextState;
	}

}
