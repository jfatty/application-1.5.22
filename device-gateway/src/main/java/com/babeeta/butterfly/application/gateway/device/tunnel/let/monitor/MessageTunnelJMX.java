package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.let.MessageTunnelLet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-27
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public class MessageTunnelJMX implements MessageTunnelJMXMBean{
    public long getTunnelMessageCount(){
        return MessageTunnelLet.TUNNEL_MESSAGE_COUNT.getAndSet(0);
    }
}
