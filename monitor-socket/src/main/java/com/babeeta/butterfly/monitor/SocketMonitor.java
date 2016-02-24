package com.babeeta.butterfly.monitor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class SocketMonitor {
	public static void main(String[] args) throws Exception {
		Options options = new Options();

		Option option = new Option("t", true,
				"type of service which need monitoring");
		option.setRequired(false);
		option.setArgName("type");
		options.addOption(option);

		option = new Option("h", true, "service ip");
		option.setRequired(true);
		option.setArgName("host");
		options.addOption(option);

		option = new Option("p", true, "service port");
		option.setRequired(true);
		option.setArgName("port");
		options.addOption(option);

		try {
			CommandLine cl = new GnuParser().parse(options, args);
			SocketMonitorContext loadContext = new SocketMonitorContext(cl);
			bootstrap(loadContext);
		} catch (Exception e) {
			e.printStackTrace();
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java ....socket monitor", options);
		}
	}

	public static void bootstrap(SocketMonitorContext socketMonitorContext)
			throws Exception {
		if (socketMonitorContext.type.equalsIgnoreCase("big-router")) {
			new BigRouterMonitor(socketMonitorContext.host,
					socketMonitorContext.port).startup();
		} else if (socketMonitorContext.type.equalsIgnoreCase("device-gateway")) {
			new DeviceGatewayMonitor(socketMonitorContext.host,
					socketMonitorContext.port).startup();
		} else {
			System.out.println("Unknown type = " + socketMonitorContext.type);
			System.exit(0);
		}
	}
}

class SocketMonitorContext {
	final String type;
	final String host;
	final int port;

	public SocketMonitorContext(CommandLine commandLine) {
		type = commandLine.getOptionValue("t");
		host = commandLine.getOptionValue("h");
		port = Integer.valueOf(commandLine.getOptionValue("p"));
	}
}