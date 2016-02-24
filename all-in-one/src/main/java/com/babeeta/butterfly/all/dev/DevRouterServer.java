package com.babeeta.butterfly.all.dev;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.babeeta.butterfly.application.router.dev.DevRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.router.jvm.DNS;
import com.babeeta.butterfly.router.jvm.DNSImpl;
import com.babeeta.butterfly.router.jvm.MessageSenderImpl;
import com.babeeta.butterfly.router.network.Service;
import com.mongodb.Mongo;

/**
 * Created by IntelliJ IDEA. User: yinchong Date: 10-12-7 Time: 上午11:57 To
 * change this template use File | Settings | File Templates.
 */
public class DevRouterServer {
    private final static Logger logger = LoggerFactory
            .getLogger(DevRouterServer.class);

    public static void bootstrap(String devRouter,Mongo mongo) throws Exception {
        final ExecutorService executorService = Executors.newCachedThreadPool();

        DNS dns = DNSImpl.getDefaultInstance();
        MessageSender messageSender = new MessageSenderImpl(dns);// MessageSenderImpl触发MessageServiceImpl
        MessageHandler messageHandler = new DevRouter(messageSender, mongo);// DevRouterHandler触发MessageSenderImpl
        final Service service = new Service(messageHandler, executorService, executorService);// 调用messageHandler onMessage 触发DevRouterHandler
        service.start(devRouter);

        Runtime.getRuntime().removeShutdownHook(new Thread("Shutdown") {
            @Override
            public void run() {
                service.shutdownGraceFully();
                executorService.shutdownNow();
            }
        });
        logger.info("[Dev router netty server is started.]");
    }
}
