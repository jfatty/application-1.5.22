package com.babeeta.butterfly.application.gateway.device.tunnel;

import com.google.protobuf.MessageLite;

public interface TunnelLetFactory {

	/**
	 * 根据数据类型，查找处理此数据的TunnelLet
	 * 
	 * @param obj
	 * @return
	 */
	TunnelLet<? extends MessageLite> getTunnelLet(int cmd);

}
