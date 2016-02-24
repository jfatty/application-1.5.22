package com.babeeta.butterfly.application.monitoring;

import com.babeeta.butterfly.MessageRouting;

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class MonitoringClientPipelineFactory implements ChannelPipelineFactory {
    private MessageRouting.Message message;
    private MonitoringListener listener;

    public MonitoringClientPipelineFactory(MessageRouting.Message message, MonitoringListener listener) {
        this.message = message;
        this.listener = listener;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline p = pipeline();

        p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        p.addLast("protobufDecoder", new ProtobufDecoder(MessageRouting.Message.getDefaultInstance()));

        p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        p.addLast("protobufEncoder", new ProtobufEncoder());

        p.addLast("handler", new MonitoringClientHandler(listener, message));
        return p;
    }
}