package com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor;

import com.babeeta.butterfly.application.gateway.device.tunnel.let.DeviceRegisterTunnelLet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-27
 * Time: 上午10:28
 * To change this template use File | Settings | File Templates.
 */
public class DeviceRegisterJMX implements DeviceRegisterJMXMBean{
    public long getDeviceRegisterCount(){
        return DeviceRegisterTunnelLet.DEVICE_REGISTER_COUNT.getAndSet(0);
    }
}
