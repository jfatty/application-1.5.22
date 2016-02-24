package com.babeeta.butterfly.application.monitoring;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-10
 * Time: 下午10:08
 * To change this template use File | Settings | File Templates.
 * <br>监控定义接口
 */
public interface MonitoringListener {
    public static final String EVENT_OK = "OK";
    public static final String EVENT_ERROR = "ERROR";
    public static final String EVENT_TIMEOUT = "WARNING";
    public static final String EVENT_NOT_FIND_SERVICE = "NOT_FIND_SERVICE";

    /**
     * 是否完成本次监控
     * @return true or false
     */
    boolean isDone();

    /**
     * 监控事件
     * @param event 本接口中定义
     */
    void onMonitoring(String event);

    /**
     * 获取监控结果
     * @return 本接口中定义事件类型值
     */
    String getResult();
}