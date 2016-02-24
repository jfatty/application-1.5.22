package com.babeeta.butterfly.monitor;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;

import com.babeeta.butterfly.monitor.gateway.device.DeviceClientFactory;

public class DeviceGatewayMonitor {
	private String host;
	private int port;

	private ClientBootstrap client;

	public DeviceGatewayMonitor(String host, int port) {
		this.host = host;
		this.port = port;

		client = DeviceClientFactory.newClient();
	}

	public void startup() {
		client.connect(new InetSocketAddress(host, port));
	}
}
