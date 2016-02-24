package com.babeeta.butterfly.application.monitoring;

import org.apache.commons.cli.*;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-10
 * Time: 下午6:37
 * To change this template use File | Settings | File Templates.
 * <br>监控服务的主类
 */
public class Monitoring {
    private final static Logger logger = LoggerFactory.getLogger(Monitoring.class);
    static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Options options = new Options();

        Option option = new Option("l", true, "当前监控服务的域名");//暂时没用
        option.setRequired(false);
        option.setArgName("host");
        options.addOption(option);

        option = new Option("t", true, "指定监控服务的域名");
        option.setRequired(true);
        option.setArgName("target");
        options.addOption(option);

        option = new Option("s", true, "指定验证的超时时间,单位:秒");
        option.setRequired(false);
        option.setArgName("timeout");
        options.addOption(option);
        try {
            CommandLine cl = new GnuParser().parse(options, args);
            MonitoringContext context = new MonitoringContext(cl, listener, start);
            monitoring(context);
        } catch (Exception e) {
            System.out.println(e.toString());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Monitoring", options);
        }
    }

    /**
     * 监控入口
     *
     * @param context
     */
    private static void monitoring(final MonitoringContext context) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                connect(context);
            }
        });
        while (true) {
            if (listener.isDone()) {
                System.out.println(listener.getResult());
                break;
            }
        }
        logger.debug("[monitoring time: {}ms]", (System.currentTimeMillis() - context.getStartTime()));
        System.exit(0);
    }


    /**
     * 向监控程序发送验证
     *
     * @param context
     */
    private static void connect(MonitoringContext context) {
        ScheduledExecutorService timeoutCleaner = Executors.newScheduledThreadPool(1);
        timeoutCleaner.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        listener.onMonitoring(MonitoringListener.EVENT_TIMEOUT);
                    }
                }, context.getSeconds(), TimeUnit.SECONDS);

        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(executorService, executorService));
        bootstrap.setPipelineFactory(new MonitoringClientPipelineFactory(null, listener));

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(context.getTarget(), 5757));
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }

    /**
     * 监听监控结果,暂时没用
     */
    private static final MonitoringListener listener = new MonitoringListener() {
        private String result = null;
        private boolean isDone = false;

        @Override
        public boolean isDone() {
            return isDone;
        }

        @Override
        public void onMonitoring(String event) {
            this.result = event;
            this.isDone = true;
        }

        @Override
        public String getResult() {
            return this.result;
        }
    };
}