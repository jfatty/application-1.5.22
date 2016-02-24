package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.let.HeartbeatInitTunnelLet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-27
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
public class HeartbeatInitJMX implements HeartbeatInitJMXMBean{
    public long getHeartbeatInitCount(){
        return HeartbeatInitTunnelLet.HEARTBEAT_COUNT_INIT.getAndSet(0);
    }
}
