package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.let.ServiceUnbindTunnelLet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-27
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public class ServiceUnbindJMX implements ServiceUnbindJMXMBean {
    public long getServiceUnbindCount() {
        return ServiceUnbindTunnelLet.SERVICE_UNBIND_COUNT.getAndSet(0);
    }
}
