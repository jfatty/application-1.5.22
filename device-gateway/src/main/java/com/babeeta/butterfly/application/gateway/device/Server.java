package com.babeeta.butterfly.application.gateway.device;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;

public class Server {

	/**
	 * @param args
	 * @throws Exception
	 * @throws DaemonInitException
	 */

	public static void main(final String[] args) throws DaemonInitException,
			Exception {
		DeviceGatewayService dgs = new DeviceGatewayService();
		dgs.init(new DaemonContext() {

			@Override
			public String[] getArguments() {
				return args;
			}

			@Override
			public DaemonController getController() {
				return null;
			}
		});

		dgs.start();

	}

}