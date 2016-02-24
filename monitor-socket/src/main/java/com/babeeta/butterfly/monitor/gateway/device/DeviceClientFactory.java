package com.babeeta.butterfly.monitor.gateway.device;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.babeeta.butterfly.monitor.gateway.device.pipeline.PipelineFactory;
import com.babeeta.butterfly.monitor.gateway.device.state.AuthState;
import com.babeeta.butterfly.monitor.gateway.device.state.DeviceRegisterState;
import com.babeeta.butterfly.monitor.gateway.device.state.HeartbeatInitState;
import com.babeeta.butterfly.monitor.gateway.device.state.ListenState;
import com.babeeta.butterfly.monitor.gateway.device.state.QuitState;
import com.babeeta.butterfly.monitor.gateway.device.state.ServiceBindState;

public class DeviceClientFactory {
	private final static ExecutorService bossExecutorService = Executors
			.newCachedThreadPool();

	private final static ExecutorService workerExecutorService = Executors
			.newCachedThreadPool();

	private final static NioClientSocketChannelFactory nioClientSocketChannelFactory = new NioClientSocketChannelFactory(
			bossExecutorService, workerExecutorService);

	public static ClientBootstrap newClient() {
		final HeartbeatInitState hbs = new HeartbeatInitState();
		DeviceRegisterState drs = new DeviceRegisterState();
		AuthState aus = new AuthState();
		ServiceBindState sbs = new ServiceBindState();
		QuitState qs = new QuitState();

		hbs.setNextState(drs).setNextState(aus).setNextState(sbs)
				.setNextState(qs);

		ClientBootstrap bootstrap = new ClientBootstrap(
				nioClientSocketChannelFactory);
		bootstrap.setPipelineFactory(new PipelineFactory() {
			@Override
			protected TunnelHandler createTunnelHandler() {
				return new TunnelHandler(hbs);
			}
		});

		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("connectTimeoutMillis", 30000);

		return bootstrap;
	}

	public static ClientBootstrap newReceiver(
			com.babeeta.butterfly.MessageSender sender) {
		final HeartbeatInitState hbs = new HeartbeatInitState();
		DeviceRegisterState drs = new DeviceRegisterState();
		AuthState aus = new AuthState();
		ServiceBindState sbs = new ServiceBindState();
		ListenState ls = new ListenState(sender);
		QuitState qs = new QuitState();

		hbs.setNextState(drs)
				.setNextState(aus)
				.setNextState(sbs)
				.setNextState(ls)
				.setNextState(qs);

		ClientBootstrap bootstrap = new ClientBootstrap(
				nioClientSocketChannelFactory);
		bootstrap.setPipelineFactory(new PipelineFactory() {
			@Override
			protected TunnelHandler createTunnelHandler() {
				return new TunnelHandler(hbs);
			}
		});

		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("connectTimeoutMillis", 30000);

		return bootstrap;
	}
}
