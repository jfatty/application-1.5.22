package com.babeeta.butterfly.app.monitor;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-13
 * Time: 上午10:05
 * To change this template use File | Settings | File Templates.
 */
public interface AppGatewayJMXMBean {
    /**
     * 所有请求总量
     * @return
     */
    long getRequestCount();

    /**
     * 获取有效的请求总量
     * @return
     */
    long getEffectiveRequestCount();

    /**
     * 远程调用成功数量
     * @return
     */
    long getRpcCount();
}
