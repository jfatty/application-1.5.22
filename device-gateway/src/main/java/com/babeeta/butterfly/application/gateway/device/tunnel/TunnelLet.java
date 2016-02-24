package com.babeeta.butterfly.application.gateway.device.tunnel;

import com.google.protobuf.MessageLite;

public interface TunnelLet<T extends MessageLite> {

	/**
	 * 处理Tunnel中收到的消息
	 * 
	 * @param tunnelContext
	 * 
	 * @param data
	 */
	void messageReceived(
			TunnelContext tunnelContext,
			TunnelData<T> data);

}
