package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageRouting.Acknowledgement;
import com.babeeta.butterfly.application.gateway.device.ServerContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelLet;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.babeeta.butterfly.application.reliable.ReliablePushImpl;
import com.google.protobuf.MessageLite;

public class AcknowledgementTunnelLet implements TunnelLet<Acknowledgement> {

	private static final Logger logger = LoggerFactory
			.getLogger(AcknowledgementTunnelLet.class);

	private final ServerContext serverContext;

	public static AtomicLong ACK_COUNT = new AtomicLong(0);

	public AcknowledgementTunnelLet(ServerContext serverContext) {
		super();
		this.serverContext = serverContext;
	}

	@Override
	public void messageReceived(
			final TunnelContext tunnelContext,
			final TunnelData<Acknowledgement> data) {
		ACK_COUNT.getAndIncrement();
		logger.debug("[{}]{}", tunnelContext.getChannel().getId(),
				data.obj.getUid());

		serverContext.workerExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				ReliablePush reliablePush = ReliablePushImpl
						.getDefaultInstance();
				
				if(data.obj.getIsAppAck()){
					logger.debug("[{}] ReliablePush updateAppAck",data.obj.getUid());
					reliablePush.updateAppAck(data.obj.getUid());
				}else{
					logger.debug("[{}] ReliablePush updateAck",data.obj.getUid());
					reliablePush.updateAckWhenDelivering(data.obj.getUid(),data.obj.getCid());
				}
				
				tunnelContext.getChannel().write(
						new TunnelData<MessageLite>(data.tag, 135,
								MessageRouting.Response.newBuilder()
										.setStatus("SUCCESS").build()));
			}
		});
	}
}
