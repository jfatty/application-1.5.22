package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelService;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-13
 * Time: 上午9:52
 * To change this template use File | Settings | File Templates.
 */
public class TunnelJMX implements TunnelJMXMBean {
    public int getTunnelCount() {
        return TunnelService.getTunnelCount();
    }
}
