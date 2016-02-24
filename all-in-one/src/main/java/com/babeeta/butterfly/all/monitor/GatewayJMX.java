package com.babeeta.butterfly.all.monitor;

import com.babeeta.butterfly.application.router.gateway.GatewayRouter;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-23
 * Time: 下午6:11
 * To change this template use File | Settings | File Templates.
 */
public class GatewayJMX implements GatewayJMXMBean {

    public long getMessageCount() {
        return GatewayRouter.MESSAGE_COUNT.getAndSet(0);
    }
}
