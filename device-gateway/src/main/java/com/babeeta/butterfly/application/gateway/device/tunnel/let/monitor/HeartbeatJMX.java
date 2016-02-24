package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.let.HeartbeatTunnelLet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-27
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
public class HeartbeatJMX implements HeartbeatJMXMBean {
    public long getHeartbeatCount() {
        return HeartbeatTunnelLet.HEARTBEAT_COUNT.getAndSet(0);
    }
}
