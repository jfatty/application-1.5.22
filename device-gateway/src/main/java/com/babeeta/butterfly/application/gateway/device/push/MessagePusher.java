package com.babeeta.butterfly.application.gateway.device.push;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.misc.Address;
import com.google.protobuf.MessageLite;

public class MessagePusher implements MessageHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(MessagePusher.class);

	private final ConcurrentHashMap<String, Channel> channelTable = new ConcurrentHashMap<String, Channel>();

	private static final MessagePusher defaultInstance = new MessagePusher();

	public static AtomicLong PUSH_SUCCEED_COUNT = new AtomicLong(0);
	public static AtomicLong PUSH_FAIL_COUNT = new AtomicLong(0);

	public static synchronized MessagePusher getDefaultInstance() {
		return defaultInstance;
	}

	private MessagePusher() {
	}
	
	public void push(Message message){

		Address address = new Address(message.getTo());
		Channel channel = resolve(address.deviceId);
		if (channel != null) {
			Message msg = message
					.toBuilder()
					.setTo(address.clientId + "." + address.applicationId
							+ "@dev")
					.build();

			channel.write(new TunnelData<MessageLite>(0, 129, msg));
			logger.debug(
					"[{}][{}] Message has been sent to mobile client [{}]",
					new Object[] {
							channel.getId(),
							message.getUid(),
							address.applicationId + "." + address.clientId
									+ "." + address.deviceId
			});
			PUSH_SUCCEED_COUNT.getAndIncrement();
		} else {
			logger.debug("[{}][Not find channel [{}]", new Object[] {
					message.getUid(),
					address.applicationId + "." + address.clientId + "."
							+ address.deviceId
			});
			PUSH_FAIL_COUNT.getAndIncrement();
		}
	
	}
	
	@Override
	public void onMessage(Message message) {
		logger.info("[{}] unicast message request.",message.getUid());//统计
		push(message);
	}

	public void register(final String device, final Channel channel)
			throws IllegalArgumentException {
		if (!channel.isConnected()) {
			logger.warn("[{}]Channel closed already .", device);
			return;
		}
		Channel old = channelTable.put(device, channel);
		if (old != null && old.getId() != channel.getId()) {
			logger.debug("[{}]Closing old channel : [{}]", channel.getId(),
					old.getId());
			old.close();
		}
		channel.getCloseFuture().addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (channelTable.remove(device, channel)) {
					logger.debug("[{}] Channel removed. [{}]", channel.getId(),
							device);
				}
				else {
					logger.debug("[{}] Channel already removed. [{}]",
							channel.getId(), device);
				}
			}
		});
		logger.debug("[{}] Chanel register [{}],channel size [{}]", new Object[]{channel.getId(), device , channelTable.size()});
	}

	public Channel resolve(String device) {
		return channelTable.get(device);
	}
	
	public boolean isOnline(String deviceId){
		return channelTable.containsKey(deviceId);
	}

	public void unRegister(String device) throws IllegalArgumentException {
		channelTable.remove(device);
		logger.debug("Chanel success unregister by [{}],channel size [{}]", device, channelTable.size());
	}
}