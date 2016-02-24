package com.babeeta.butterfly.monitor.gateway.device.state;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageFuture;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.router.network.pool.ChannelFactory;
import com.babeeta.butterfly.router.network.pool.PooledChannelFactory;

public class AssignableMessageSender implements
		com.babeeta.butterfly.MessageSender {
	private static final Logger logger = LoggerFactory
			.getLogger(AssignableMessageSender.class);

	public static ExecutorService executorService;
	private String host;
	private int port;
	private final ChannelFactory channelFactory;

	public AssignableMessageSender(String host, int port) {
		this.host = host;
		this.port = port;
		executorService = Executors.newCachedThreadPool();
		channelFactory = new PooledChannelFactory(executorService,
				executorService);
	}

	@Override
	public MessageFuture send(final Message message) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				logger.debug("send message [{}] to [{}].",
						message.getUid(), message.getTo());

				Channel channel = null;
				try {
					InetSocketAddress address = new InetSocketAddress(host,
							port);
					channel = channelFactory.getChannel(address);
					ChannelFuture channelFuture = channel.write(message);
					channelFuture.addListener(new ChannelFutureListener() {

						@Override
						public void operationComplete(ChannelFuture future)
								throws Exception {
							if (future.isSuccess()) {
								logger.debug("[{}] sent succeed.",
										message.getUid());
							} else {
								logger.debug("[{}] sent fail.",
										message.getUid());
							}
						}
					});
				} catch (Exception e) {
					logger.error("{}", e.getMessage());
				} finally {
					logger.debug("Channel check {}", channel);
					if (channel != null) {
						channelFactory.returnChannel(channel);
					}
				}
			}
		});

		return null;
	}
}
