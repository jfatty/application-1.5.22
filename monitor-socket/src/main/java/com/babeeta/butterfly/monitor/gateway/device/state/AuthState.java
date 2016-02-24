package com.babeeta.butterfly.monitor.gateway.device.state;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Credential;
import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.monitor.gateway.device.Session;
import com.babeeta.butterfly.monitor.gateway.device.TunnelData;

public class AuthState extends AbstractTunnelState {
	public static final AtomicInteger ERROR_COUNTER = new AtomicInteger();
	private static final Logger logger = LoggerFactory
			.getLogger(AuthState.class);

	private final CredentialProvider credentialProvider;

	public AuthState() {
		this(new SessionCredentialProvider());
	}

	public AuthState(CredentialProvider credentialProvider) {
		this.credentialProvider = credentialProvider;
	}

	@Override
	public TunnelState onBegin(final Session session, MessageEvent e,
			ChannelHandlerContext ctx) {
		session.channel.write(
				new TunnelData<Credential>(session.getTag(), 132, Credential
						.newBuilder().setId(credentialProvider.getId(session))
						.setSecureKey(credentialProvider.getKey(session))
						.build())).addListener(
				new ChannelFutureListener() {
					public void operationComplete(ChannelFuture future)
									throws Exception {
						logger.debug("[{}]正在进行身份验证...", session.channel.getId());
					}
				});
		return this;
	}

	@Override
	public TunnelState onMessageReceived(Session session, MessageEvent e,
			ChannelHandlerContext ctx) throws Exception {
		@SuppressWarnings("unchecked")
		TunnelData<Response> result = (TunnelData<Response>) e.getMessage();
		logger.info("[{}] {}", session.channel.getId(), result.obj.getStatus());
		return nextState;
	}

}
