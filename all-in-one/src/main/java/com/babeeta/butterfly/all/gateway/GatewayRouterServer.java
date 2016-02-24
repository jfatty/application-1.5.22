package com.babeeta.butterfly.all.gateway;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.application.router.gateway.GatewayRouter;
import com.babeeta.butterfly.application.router.gateway.balance.KetamaHashServiceLocator;
import com.babeeta.butterfly.router.jvm.DNS;
import com.babeeta.butterfly.router.jvm.DNSImpl;
import com.babeeta.butterfly.router.jvm.MessageSenderImpl;
import com.babeeta.butterfly.router.network.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA. User: yinchong Date: 10-12-7 Time: 上午11:58 To
 * change this template use File | Settings | File Templates.
 */
public class GatewayRouterServer {
    private final static Logger logger = LoggerFactory
            .getLogger(GatewayRouterServer.class);

    public static void bootstrap(String gatewayRouter,List list) throws Exception {
        final ExecutorService executorService = Executors.newCachedThreadPool();

        DNS dns = DNSImpl.getDefaultInstance();
        MessageSender messageSender = new MessageSenderImpl(dns);
        MessageHandler messageHandler = new GatewayRouter(messageSender, new KetamaHashServiceLocator(list));
        final Service service = new Service(messageHandler, executorService, executorService);
        service.start(gatewayRouter);

        Runtime.getRuntime().removeShutdownHook(new Thread("Shutdown") {

            @Override
            public void run() {
                service.shutdownGraceFully();
                executorService.shutdownNow();
            }

        });
        logger.info("[Gateway router netty server is started.]");
    }
}
