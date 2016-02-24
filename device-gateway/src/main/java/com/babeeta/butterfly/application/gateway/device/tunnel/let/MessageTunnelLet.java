package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import com.babeeta.butterfly.MessageRouting;
import com.google.protobuf.MessageLite;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelLet;

import java.util.concurrent.atomic.AtomicLong;

public class MessageTunnelLet implements TunnelLet<Message> {

    private static final Logger logger = LoggerFactory
            .getLogger(MessageTunnelLet.class);

    public static AtomicLong TUNNEL_MESSAGE_COUNT = new AtomicLong(0);

    @Override
    public void messageReceived(
            TunnelContext tunnelContext, TunnelData<Message> data) {
        TUNNEL_MESSAGE_COUNT.getAndIncrement();
        logger.debug("[{}]", tunnelContext.getChannel().getId());

        try {//try catch 中代码为测试代码，用于测试客户端上行消息是否正确
            MessageRouting.Message msg = MessageRouting.Message.parseFrom(data.obj.toByteArray());
            logger.debug("[{}]", msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        tunnelContext.getChannel().write(new TunnelData<MessageLite>(data.tag, 135, Response.newBuilder().setStatus("SUCCESS").build()))
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        logger.debug("MessageTunnelLet response has been sent.");
    }
                });
    }

}
