package com.babeeta.butterfly.monitor.gateway.device;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Heartbeat;
import com.babeeta.butterfly.MessageRouting.HeartbeatResponse;

public class HeartbeatService {
	private static final Logger logger = LoggerFactory
			.getLogger(HeartbeatService.class);
	private static ScheduledExecutorService scheduledExecutorService = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	private static final HeartbeatService DEFAULT_HEARTBEAT_SERVICE = new HeartbeatService();

	@SuppressWarnings("rawtypes")
	private static final Map<Integer, ScheduledFuture> futureMap = new ConcurrentHashMap<Integer, ScheduledFuture>();

	public static void cancelPushSchedule(Integer channelId) {
		if (futureMap.containsKey(channelId)) {
			futureMap.get(channelId).cancel(false);
		}
	}

	public static HeartbeatService getDefaultInstance() {
		return DEFAULT_HEARTBEAT_SERVICE;
	}

	public static void shutdown() {
		scheduledExecutorService.shutdownNow();
	}

	private HeartbeatService() {
	}

	public void addHeartBeat(final Session session, final int delay) {
		futureMap.put(session.channel.getId(),
				scheduledExecutorService.schedule(new Runnable() {
					@Override
					public void run() {
						if (!session.channel.isConnected()) {
							return;
						}
						session.channel.write(
								new TunnelData<Heartbeat>(session.getTag(), 1,
										Heartbeat
												.newBuilder()
												.setLastDelay(delay)
												.build()))
								.addListener(new ChannelFutureListener() {

									@Override
									public void operationComplete(
											ChannelFuture future)
											throws Exception {
										logger.debug("[{}]",
												session.channel.getId());
									}
								});
					}

				}, delay, TimeUnit.SECONDS));
	}

	public void onHeartbeat(final MessageEvent e, Session session) {
		@SuppressWarnings("unchecked")
		TunnelData<HeartbeatResponse> data = (TunnelData<HeartbeatResponse>) e
				.getMessage();
		logger.debug("[{}]Heartbeat:[{}]", e.getChannel().getId(),
				data.obj.getDelay());
		addHeartBeat(session, data.obj.getDelay());
	}
}
