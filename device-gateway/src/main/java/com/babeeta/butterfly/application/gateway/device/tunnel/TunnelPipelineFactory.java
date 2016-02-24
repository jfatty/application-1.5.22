package com.babeeta.butterfly.application.gateway.device.tunnel;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

public class TunnelPipelineFactory implements ChannelPipelineFactory {

	private final ChannelGroup channelGroup;
	private final TunnelLetFactory tunnelLetFactory;
	private final Timer timer;

	public TunnelPipelineFactory(ChannelGroup channelGroup,
			TunnelLetFactory tunnelLetFactory) {
		super();
		this.channelGroup = channelGroup;
		this.tunnelLetFactory = tunnelLetFactory;
		this.timer = new HashedWheelTimer();
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = new DefaultChannelPipeline();
		pipeline.addLast("frameDecoder",
				new LengthFieldBasedFrameDecoder(10240, 8, 4, 0, 0));
		pipeline.addLast("idlestate", new IdleStateHandler(timer, 330, 0, 0));
		pipeline.addLast("requestDecoder",
				new TunnelDataEncoder());
		pipeline.addLast("requestEncoder",
				new TunnelDataDecoder());
		pipeline.addLast("requstHandler", new TunnelHandler(channelGroup,
				tunnelLetFactory));
		return pipeline;
	}
}
