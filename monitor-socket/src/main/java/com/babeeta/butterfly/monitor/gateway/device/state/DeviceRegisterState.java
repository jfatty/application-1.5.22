package com.babeeta.butterfly.monitor.gateway.device.state;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Credential;
import com.babeeta.butterfly.MessageRouting.DeviceRegister;
import com.babeeta.butterfly.monitor.gateway.device.Session;
import com.babeeta.butterfly.monitor.gateway.device.TunnelData;

public class DeviceRegisterState extends AbstractTunnelState {

	private static final Logger logger = LoggerFactory
			.getLogger(DeviceRegisterState.class);

	@Override
	public TunnelState onBegin(Session session, MessageEvent e,
			ChannelHandlerContext ctx) {
		session.channel.write(new TunnelData<DeviceRegister>(session.getTag(),
				131, DeviceRegister.newBuilder()
						.setImei("device-gateway-monitor-imei")
						.setOsName("device-gateway-monitor").build()));
		return this;
	}

	@Override
	public TunnelState onMessageReceived(Session session, MessageEvent e,
			ChannelHandlerContext ctx) throws Exception {
		@SuppressWarnings("unchecked")
		TunnelData<Credential> result = (TunnelData<Credential>) e.getMessage();

		logger.debug("[{}]DID:{}, KEY:{}",
				new Object[] { session.channel.getId(), result.obj.getId(),
						result.obj.getSecureKey() });
		session.clientId = result.obj.getId();
		session.secureKey = result.obj.getSecureKey();
		return nextState;
	}

}
