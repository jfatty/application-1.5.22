package com.babeeta.butterfly.monitor.gateway.device.state;

public abstract class AbstractTunnelState implements TunnelState {

	protected AbstractTunnelState nextState = null;

	public AbstractTunnelState() {
		super();
	}

	public final AbstractTunnelState setNextState(AbstractTunnelState nextState) {
		this.nextState = nextState;
		return this.nextState;
	}
}