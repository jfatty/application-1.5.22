package com.babeeta.butterfly.application.gateway.device.tunnel;

import java.util.HashMap;
import java.util.Map;

import com.babeeta.butterfly.application.gateway.device.ServerContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.AcknowledgementTunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.AuthTunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.DeviceRegisterTunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.HeartbeatInitTunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.HeartbeatTunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.MessageTunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.ServiceBindTunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.ServiceUnbindTunnelLet;
import com.google.protobuf.MessageLite;

public class DefaultTunnelLetFactory implements TunnelLetFactory {

	private final Map<Integer, TunnelLet<? extends MessageLite>> tunnelLetMap = new HashMap<Integer, TunnelLet<? extends MessageLite>>();

	public DefaultTunnelLetFactory(ServerContext serverContext) {
		tunnelLetMap.put(0, new HeartbeatInitTunnelLet());
		tunnelLetMap.put(1, new HeartbeatTunnelLet());
		tunnelLetMap.put(131, new DeviceRegisterTunnelLet(
				serverContext));
		tunnelLetMap.put(129, new MessageTunnelLet());
		tunnelLetMap.put(133, new ServiceBindTunnelLet(
				serverContext));
		tunnelLetMap.put(134, new ServiceUnbindTunnelLet(serverContext));
		tunnelLetMap.put(132, new AuthTunnelLet(serverContext));
        tunnelLetMap.put(130, new AcknowledgementTunnelLet(serverContext));
	}

	@Override
	public TunnelLet<? extends MessageLite> getTunnelLet(int cmd) {
		return tunnelLetMap.get(cmd);
	}

}
