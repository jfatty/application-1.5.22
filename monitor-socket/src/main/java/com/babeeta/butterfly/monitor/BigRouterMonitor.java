package com.babeeta.butterfly.monitor;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;

import com.babeeta.butterfly.monitor.gateway.device.DeviceClientFactory;
import com.babeeta.butterfly.monitor.gateway.device.state.AssignableMessageSender;

public class BigRouterMonitor {
	private String deviceGatewayHost = "192.168.20.82";
	private int deviceGatewayPort = 5757;

	private ClientBootstrap client;
	private AssignableMessageSender sender;

	public BigRouterMonitor(String bigRouterHost,
			int bigRouterPort) {

		sender = new AssignableMessageSender(bigRouterHost, bigRouterPort);

		client = DeviceClientFactory.newReceiver(sender);

	}

	public void startup() {
		client.connect(new InetSocketAddress(deviceGatewayHost,
				deviceGatewayPort));
	}
}
