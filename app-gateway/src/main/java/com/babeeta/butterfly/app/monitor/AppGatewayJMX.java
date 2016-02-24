package com.babeeta.butterfly.app.monitor;

import com.babeeta.butterfly.app.ThirdAppGatewayServer;
import com.babeeta.butterfly.app.servlet.AbstractAppServlet;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-13
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */
public class AppGatewayJMX implements AppGatewayJMXMBean {

    public long getRequestCount() {
        return AbstractAppServlet.REQUEST_COUNT.getAndSet(0);
    }

    public long getEffectiveRequestCount() {
        return AbstractAppServlet.EFFECTIVE_REQUEST_COUNT.getAndSet(0);
    }

    public long getRpcCount() {
        return ThirdAppGatewayServer.RPC_SUCCEED_COUNT.getAndSet(0);
    }
}
