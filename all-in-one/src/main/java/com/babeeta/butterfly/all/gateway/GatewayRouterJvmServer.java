package com.babeeta.butterfly.all.gateway;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.application.router.gateway.GatewayRouter;
import com.babeeta.butterfly.application.router.gateway.balance.KetamaHashServiceLocator;
import com.babeeta.butterfly.router.jvm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: yinchong
 * Date: 10-12-7
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class GatewayRouterJvmServer {
    private final static Logger logger = LoggerFactory.getLogger(GatewayRouterJvmServer.class);

    public static void bootstrap(String gatewayRouter,List list) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();

        DNS dns = DNSImpl.getDefaultInstance();
        MessageSender sender = new MessageSenderImpl(dns);
        MessageHandler messageHandler = new GatewayRouter(sender, new KetamaHashServiceLocator(list));
        MessageService service = new MessageServiceImpl(messageHandler, executorService);

        final Server server = new Server(dns, gatewayRouter, service);

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
        logger.info("[Gateway router jvm server is started]");
    }
}
