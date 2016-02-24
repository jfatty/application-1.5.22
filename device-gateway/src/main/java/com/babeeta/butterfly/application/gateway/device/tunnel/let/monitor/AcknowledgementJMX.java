package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.let.AcknowledgementTunnelLet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-27
 * Time: 上午10:22
 * To change this template use File | Settings | File Templates.
 */
public class AcknowledgementJMX implements AcknowledgementJMXMBean{
    public long getAckCount(){
        return AcknowledgementTunnelLet.ACK_COUNT.getAndSet(0);
    }
}
