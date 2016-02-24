package com.babeeta.butterfly.application.monitoring;

import com.babeeta.butterfly.MessageRouting;
import org.jboss.netty.channel.*;

public class MonitoringClientHandler extends SimpleChannelHandler {
    private MonitoringListener listener;
    private MessageRouting.Message message;

    /**
     *
     * @param listener
     * @param message  这个参数暂时没用，是用于目标服务逻辑依赖的数据对象。
     */
    public MonitoringClientHandler(MonitoringListener listener, MessageRouting.Message message) {
        this.listener = listener;
        this.message = message;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        ctx.getChannel().close();
        listener.onMonitoring(MonitoringListener.EVENT_OK);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        listener.onMonitoring(MonitoringListener.EVENT_ERROR);
        e.getChannel().close();
    }

    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) throws Exception {
    }
}