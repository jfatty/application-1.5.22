package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.let.ServiceBindTunnelLet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-27
 * Time: 上午10:37
 * To change this template use File | Settings | File Templates.
 */
public class ServiceBindJMX implements ServiceBindJMXMBean {
    public long getServiceBindCount() {
        return ServiceBindTunnelLet.SERVICE_BIND_COUNT.getAndSet(0);
    }
}
