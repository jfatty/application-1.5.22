package com.babeeta.butterfly.application.monitoring;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-10
 * Time: 下午11:00
 * To change this template use File | Settings | File Templates.
 */
public class MonitoringContext {
    private final static Logger logger = LoggerFactory.getLogger(MonitoringContext.class);

    private String domain;
    private String target;
    private int seconds = 5;//默认
    private long startTime;

    public MonitoringContext(CommandLine cl, MonitoringListener listener, long startTime) {
        try {
            this.domain = cl.getOptionValue("l");
            this.target = cl.getOptionValue("t");
            if (cl.getOptionValue("s") != null && cl.getOptionValue("s").trim().length() > 0) {
                this.seconds = Integer.parseInt(cl.getOptionValue("s"));
            }
            this.startTime = startTime;
        } catch (Exception e) {
            logger.error("[Formatter error] {}", e.toString());
            listener.onMonitoring(MonitoringListener.EVENT_ERROR);
        }
    }

    public String getDomain() {
        return domain;
    }

    public int getSeconds() {
        return seconds;
    }

    public String getTarget() {
        return target;
    }

    public long getStartTime() {
        return startTime;
    }
}
