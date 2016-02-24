package com.babeeta.butterfly.monitor;

import java.util.Date;

import org.apache.commons.cli.CommandLine;

public class MonitorContext {
	private String type;
	private String host;
	private int port;
	private long startTime;

	public MonitorContext(CommandLine cl) {
		try {
			this.host = cl.getOptionValue("h");
			this.port = Integer.parseInt(cl.getOptionValue("p"));
			startTime = new Date().getTime();
			this.type = cl.getOptionValue("t");
		} catch (Exception e) {
			System.out.println("[Formatter error] " + e.toString());
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getType() {
		return type;
	}
}
