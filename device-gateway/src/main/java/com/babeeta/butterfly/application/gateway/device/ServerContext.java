package com.babeeta.butterfly.application.gateway.device;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.router.network.MessageSenderImpl;
import com.babeeta.butterfly.rpc.MessageBasedRPCService;
import com.babeeta.butterfly.rpc.RPCService;

public class ServerContext {

	private static final Logger logger = LoggerFactory
			.getLogger(ServerContext.class);

	public final MessageSender messageSender;
	public final String messageServiceAddress;
	public final RPCService rpcService;
	public boolean isCLT=false;
	public static final int DEFAULT_TCP_PORT = 5757;

	private ThreadFactory nettyThreadFactory = new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.MAX_PRIORITY);
			return t;
		}
	};

	public final ExecutorService bossExecutorService = Executors
			.newCachedThreadPool(nettyThreadFactory);
	public final ExecutorService workerExecutorService = Executors
			.newCachedThreadPool(nettyThreadFactory);

	ServerContext(CommandLine cl) throws IOException {
		messageServiceAddress = cl.getOptionValue("r") + ".gateway.dev";

		this.messageSender = new MessageSenderImpl();
		rpcService = new MessageBasedRPCService(messageSender);
		isCLT="1".equals(cl.getOptionValue("clt"));
	}

	public void registerMBean(Object mbean) {
		if (mbean == null) {
			logger.error("MBean cannot be null.");
			return;
		}

		registerMBean(mbean.getClass().getPackage().getName() + ":name="
				+ mbean.getClass().getSimpleName());
	}

	public void registerMBean(String name, Object mbean) {
		try {
			ObjectName mbeanName = new ObjectName(name);
			logger.debug("Registering mbean: {}", mbeanName.getCanonicalName());
			MBeanServer mBeanServer = ManagementFactory
					.getPlatformMBeanServer();

			int index = 0;
			while (mBeanServer.isRegistered(mbeanName)) {
				index++;
				mbeanName = new ObjectName(name + "-" + index);
			}

			mBeanServer.registerMBean(mbean, mbeanName);
		} catch (Exception e) {
			logger.error("Error register mbean.Name: {}, E: {}", name,
					e.getMessage());
		}

	}

	MessageHandler getRPCServiceHandler() {
		return (MessageHandler) rpcService;
	}
}