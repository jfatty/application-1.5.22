package com.babeeta.butterfly.monitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceMonitor {
	private final static Logger logger = LoggerFactory
			.getLogger(ServiceMonitor.class);
	static final ExecutorService executorService = Executors
			.newCachedThreadPool();

	public static void main(String[] args) {
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
			MonitorContext context = new MonitorContext(cl);
			startup(context);
		} catch (Exception e) {
			System.out.println(e.toString());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Monitor", options);
			System.exit(0);
		}
	}

	private static void startup(final MonitorContext context) {
		final Monitor monitor = MonitorFactory.getMonitorByType(context
				.getType(), context.getHost(), context.getPort());

		if (monitor != null) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					monitor.startup(listener);
				}
			});
			while (true) {
				if (listener.isDone()) {
					System.out.println(listener.getResult().toString());
					break;
				}
			}
			logger.debug("[monitoring time: {}ms]",
					(System.currentTimeMillis() - context.getStartTime()));
			System.exit(0);
		} else {
			logger.error("invalid type:" + context.getType());
			System.exit(0);
		}
	}

	/**
	 * 监听监控结果,暂时没用
	 */
	private static final MonitorListener listener = new MonitorListener() {
		private MonitorResult result = null;
		private boolean isDone = false;

		@Override
		public boolean isDone() {
			return isDone;
		}

		@Override
		public void setResult(boolean success, String details) {
			isDone = true;
			result = new MonitorResult(success, details);
		}

		@Override
		public MonitorResult getResult() {
			return this.result;
		}
	};
}
