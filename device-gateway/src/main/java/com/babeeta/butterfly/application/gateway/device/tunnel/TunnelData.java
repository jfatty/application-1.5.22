package com.babeeta.butterfly.application.gateway.device.tunnel;

import com.google.protobuf.MessageLite;

public class TunnelData<T extends MessageLite> {
	public final int tag;
	public final T obj;
	public final int cmd;

	public TunnelData(int tag, int cmd, T obj) {
		super();
		this.tag = tag;
		this.obj = obj;
		this.cmd = cmd;
	}

}
