package com.babeeta.butterfly.all.monitor;

import com.babeeta.butterfly.application.router.dev.DevRouter;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 10-12-23
 * Time: 下午5:29
 * To change this template use File | Settings | File Templates.
 */
public class DevJMX implements DevJMXMBean {
    public long getMessageCount() {
        return DevRouter.MESSAGE_COUNT.getAndSet(0);
    }
}
