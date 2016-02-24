package com.babeeta.butterfly.application.router.gateway.secondary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.router.network.MessageSenderImpl;
import com.babeeta.butterfly.router.network.Service;

public class GatewaySecondaryRouterServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options options = new Options();

		Option option = new Option("m", true, "Mongodb host and port");
		option.setRequired(true);
		option.setArgName("host:port");
		option.setArgs(2);
		option.setValueSeparator(':');
		options.addOption(option);

		option = new Option("n", true, "Domain");
		option.setRequired(true);
		option.setArgName("serial number. eg:0\\1\\2\\3");
		options.addOption(option);

		option = new Option("l", true, "IP address to bind.");
		option.setRequired(true);
		option.setArgName("ip");
		options.addOption(option);

		try {
			CommandLine cl = new GnuParser().parse(options, args);
			ServerContext serverContext = new ServerContext(cl);
			bootstrap(serverContext);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java ....DevRouterServer", options);
		}
	}

	private static void bootstrap(ServerContext serverContext) throws Exception {
		final ExecutorService executorService = Executors.newCachedThreadPool();
		MessageHandler messageHandler = new GatewaySecondaryRouter(
				serverContext.domain, serverContext.mongo,
				new MessageSenderImpl());
		final Service service = new Service(messageHandler, executorService,
				executorService);
		service.start(serverContext.localAddress);
		Runtime.getRuntime().removeShutdownHook(new Thread("Shutdown") {
			@Override
			public void run() {
				service.shutdownGraceFully();
				executorService.shutdownNow();
			}
		});
	}
}