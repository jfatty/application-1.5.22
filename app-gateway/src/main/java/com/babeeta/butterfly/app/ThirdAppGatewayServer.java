package com.babeeta.butterfly.app;

import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.router.network.MessageSenderImpl;
import com.babeeta.butterfly.rpc.RPCHandler;
import com.babeeta.butterfly.rpc.RPCService;

public class ThirdAppGatewayServer extends HttpServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(ThirdAppGatewayServer.class);

	public static int MSG_CONTENT_MAX_LENGTH;
	public static String RESTFUL;
	public static String DOMAIN;

	private static MessageSender MESSAGE_SENDER = new MessageSenderImpl();
	private static RPCService RPC_SERVICE;

	public static AtomicLong RPC_SUCCEED_COUNT = new AtomicLong(0);

	public static void invoke(MessageRouting.Message message, RPCHandler handler) {
		RPC_SERVICE.invoke(message, handler);
	}

	public static void send(MessageRouting.Message message) {
		MESSAGE_SENDER.send(message);
	}

	public ThirdAppGatewayServer() {
		super();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		DOMAIN = System.getProperty("domain", "0.gateway.app");
		MSG_CONTENT_MAX_LENGTH = Integer
				.parseInt(getInitParameter("msgContentMaxLength"));
		RESTFUL = getInitParameter("restfulUrl");
		logger.debug("[domain:{}]", DOMAIN);
	}

}
