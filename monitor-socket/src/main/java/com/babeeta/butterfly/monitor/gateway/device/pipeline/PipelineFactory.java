package com.babeeta.butterfly.monitor.gateway.device.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

import com.babeeta.butterfly.monitor.gateway.device.TunnelHandler;

public abstract class PipelineFactory implements ChannelPipelineFactory {
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = new DefaultChannelPipeline();
		pipeline.addLast("frameDecoder",
				new LengthFieldBasedFrameDecoder(10240, 8, 4, 0, 0));
		pipeline.addLast("requestDecoder",
				new TunnelDataEncoder());
		pipeline.addLast("requestEncoder",
				new TunnelDataDecoder());
		pipeline.addLast("handler", createTunnelHandler());
		return pipeline;
	}

	protected abstract TunnelHandler createTunnelHandler();

}
