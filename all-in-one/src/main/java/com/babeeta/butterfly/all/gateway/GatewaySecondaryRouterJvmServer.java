package com.babeeta.butterfly.all.gateway;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.application.router.gateway.secondary.GatewaySecondaryRouter;
import com.babeeta.butterfly.router.jvm.*;
import com.babeeta.butterfly.router.network.MessageSenderImpl;
import com.babeeta.butterfly.router.network.*;
import com.babeeta.butterfly.router.network.pool.ChannelFactory;
import com.babeeta.butterfly.router.network.pool.PooledChannelFactory;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: yinchong
 * Date: 10-12-7
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
public class GatewaySecondaryRouterJvmServer {
    private final static Logger logger = LoggerFactory.getLogger(GatewaySecondaryRouterJvmServer.class);

    public static void bootstrap(String gatewaySecondaryRouter,Mongo mongo,String dbName) throws Exception {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        MessageHandler messageHandler = new GatewaySecondaryRouter(dbName, mongo, new MessageSenderImpl());
        MessageService service = new MessageServiceImpl(messageHandler, executorService);
        final Server server = new Server(DNSImpl.getDefaultInstance(), gatewaySecondaryRouter, service);
        Runtime.getRuntime().removeShutdownHook(new Thread("Shutdown") {
            @Override
            public void run() {
                try {
                    server.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        logger.info("Gateway secondary router is serving on {}", gatewaySecondaryRouter);
    }
}