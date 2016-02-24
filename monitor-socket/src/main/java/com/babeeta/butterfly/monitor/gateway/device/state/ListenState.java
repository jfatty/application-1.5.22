package com.babeeta.butterfly.monitor.gateway.device.state;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageRouting.Acknowledgement;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.monitor.gateway.device.Session;
import com.babeeta.butterfly.monitor.gateway.device.TunnelData;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;

public class ListenState extends AbstractTunnelState {

	private static final Logger logger = LoggerFactory
			.getLogger(ListenState.class);

	public static final AtomicInteger MESSAGE_RECEIVED_COUNTER = new AtomicInteger();

	private final com.babeeta.butterfly.MessageSender sender;

	public ListenState() {
		this.sender = null;
	}

	public ListenState(com.babeeta.butterfly.MessageSender sender) {
		super();
		this.sender = sender;
	}

	@Override
	public TunnelState onBegin(Session session, MessageEvent e,
			ChannelHandlerContext ctx) {
		if (sender != null) {
			String strUUID = java.util.UUID.randomUUID().toString()
					.replaceAll("-", "");

			session.msgId = strUUID;

			MessageRouting.Message.Builder builder = MessageRouting.Message
					.newBuilder()
					.setDate(System.currentTimeMillis())
					.setFrom(session.appId + "@" + "test.gateway.app")
					.setUid(strUUID);

			MessageRouting.Message msg = null;

			builder.setContent(ByteString
					.copyFromUtf8("this is a test message"));

			builder.setTo(new StringBuilder(session.clientId).append(".")
					.append(session.appId)
					.append("@dev").toString());

			msg = builder.build();

			sender.send(msg);
		}
		return this;
	}

	@Override
	public TunnelState onMessageReceived(final Session session, MessageEvent e,
			ChannelHandlerContext ctx) throws Exception {
		@SuppressWarnings("unchecked")
		TunnelData<MessageLite> tunnelData = (TunnelData<MessageLite>) e
				.getMessage();

		if (135 == tunnelData.cmd) {
			String status = ((Response) tunnelData.obj).getStatus();
			if ("success".equalsIgnoreCase(status)) {
				logger.error("Ack response:{} - {}", tunnelData.tag, status);
			} else {
				logger.debug("Ack response:{} - {}", tunnelData.tag, status);
			}
		} else {

			@SuppressWarnings("unchecked")
			TunnelData<Message> msg = (TunnelData<Message>) e.getMessage();
			MESSAGE_RECEIVED_COUNTER.incrementAndGet();
			logger.debug("[{}]UID:{}, Date:{}",
					new Object[] { session.channel.getId(), msg.obj.getUid(),
							msg.obj.getDate() });
			session.result = session.msgId.equalsIgnoreCase(msg.obj.getUid());

			session.channel.write(
					new TunnelData<MessageRouting.Acknowledgement>(
							session.getTag(), 130, Acknowledgement.newBuilder()
									.setUid(msg.obj.getUid()).build()))
					.addListener(
							new ChannelFutureListener() {
								@Override
								public void operationComplete(
										ChannelFuture future)
										throws Exception {
									logger.debug("[{}]acked.",
											session.channel.getId());
								}
							});
		}

		return nextState;
	}
}
