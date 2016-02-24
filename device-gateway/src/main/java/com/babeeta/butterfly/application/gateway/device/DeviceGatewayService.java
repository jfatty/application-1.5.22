package com.babeeta.butterfly.application.gateway.device;

import java.io.File;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageSelector;
import com.babeeta.butterfly.RecipientAlreadyRegisteredException;
import com.babeeta.butterfly.application.gateway.device.push.BroadcastService;
import com.babeeta.butterfly.application.gateway.device.push.MessagePusher;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelService;
import com.babeeta.butterfly.router.network.Service;
import com.google.protobuf.ByteString;

public class DeviceGatewayService implements Daemon {

	private static final Logger logger = LoggerFactory
	        .getLogger(DeviceGatewayService.class);

	private static void bootstrap(final ServerContext serverContext)
	        throws Exception {

		final NioServerSocketChannelFactory nioServerSocketChannelFactory = new NioServerSocketChannelFactory(
		        serverContext.bossExecutorService,
		        serverContext.workerExecutorService);

		final Service messageService = new Service(
		        createNewMessageSelector(serverContext),
		        serverContext.workerExecutorService,
		        serverContext.bossExecutorService);

		messageService.start(serverContext.messageServiceAddress);
		System.out.println("Message service started.");

		final TunnelService tunnelService = new TunnelService(
		        nioServerSocketChannelFactory, serverContext);
		tunnelService.start();

		reportToGateway(serverContext);

		System.out.println("Tunnel service started.");
	}

	private static void reportToGateway(ServerContext serverContext) {
		logger.info("[{}] Report to dev.", serverContext.messageServiceAddress);
		Message msg = Message
		        .newBuilder()
		        .setContent(
		                ByteString
		                        .copyFromUtf8(serverContext.messageServiceAddress))
		        .setDate(System.currentTimeMillis())
		        .setFrom(
		                "report@" + serverContext.messageServiceAddress)
		        .setTo("devGatewayReport@dev")
		        .setUid(UUID.randomUUID().toString()
		                .replaceAll("\\-", ""))
		        .build();
		serverContext.messageSender.send(msg);
	}

	private static MessageSelector createNewMessageSelector(
	        ServerContext serverContext)
	        throws RecipientAlreadyRegisteredException {
		MessageSelector messageSelector = new MessageSelector();

		messageSelector.register("rpc", serverContext.getRPCServiceHandler());

		messageSelector.register("broadcast",
		        BroadcastService.getDefaultInstance());

		messageSelector.register("addGroup",
		        BroadcastService.getDefaultInstance());

		messageSelector.register("removeGroup",
		        BroadcastService.getDefaultInstance());

		messageSelector.register("cleanGroup",
		        BroadcastService.getDefaultInstance());

		messageSelector.setDefaultMessageHandler(MessagePusher
		        .getDefaultInstance());
		return messageSelector;
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(DaemonContext ctx) throws DaemonInitException, Exception {
		System.out.println("initializing...");
		Options options = new Options();

		Option option = new Option("r", true, "网关编号");
		option.setRequired(true);
		option.setArgName("host");
		options.addOption(option);

		Option optionTwo = new Option("clt", true, "clt");
		optionTwo.setRequired(false);
		optionTwo.setArgName("clt");
		options.addOption(optionTwo);

		try {
			CommandLine cl = new GnuParser().parse(options, ctx.getArguments());

			File DIR_LOG = new File(
			        StringUtils.isBlank(
			                System.getenv(("LOG_DIR"))) ?
			                "/var/log/dev-gateway-service"
			                : System.getenv(("LOG_DIR")));
			if (!DIR_LOG.exists()) {
				DIR_LOG.mkdirs();
			}
			System.setProperty("LOG_DIR", DIR_LOG.getCanonicalPath());

			ServerContext serverContext = new ServerContext(cl);
			bootstrap(serverContext);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("", options);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void stop() throws Exception {

	}

}
