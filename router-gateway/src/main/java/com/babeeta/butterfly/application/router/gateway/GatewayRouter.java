package com.babeeta.butterfly.application.router.gateway;


import com.babeeta.butterfly.application.router.gateway.balance.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.AbstractMessageRouter;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.misc.Address;

import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GatewayRouter extends AbstractMessageRouter {

	private static final Logger logger = LoggerFactory
			.getLogger(GatewayRouter.class);
	private final ServiceLocator serviceLocator;
    public static AtomicLong MESSAGE_COUNT = new AtomicLong(0);
    private static final Pattern GROUP_SYCN_REGX = Pattern.compile("([0-9a-z]+)\\.([0-9a-z]+)\\.([0-9a-z]+)\\.(.+)");

	public GatewayRouter(MessageSender messageSender,
			ServiceLocator serviceLocator) {
		super(messageSender);
		this.serviceLocator = serviceLocator;
	}

	@Override
	protected Message transform(Message message) {
        MESSAGE_COUNT.getAndIncrement();
		if ("update@gateway.dev".equalsIgnoreCase(message.getTo().trim())) {
			String domain = serviceLocator.getDomain(message.getContent()
					.toStringUtf8());
			logger.debug("[{}] will go to {}", message.getUid(),
					domain);
			return message
					.toBuilder()
					.setTo(new StringBuilder("update@").append(domain)
							.toString())
					.build();
		}else if("addGroup@gateway.dev".equalsIgnoreCase(message.getTo().trim()) || "removeGroup@gateway.dev".equalsIgnoreCase(message.getTo().trim())){
			Matcher matcher=GROUP_SYCN_REGX.matcher(message.getContent().toStringUtf8());
			
			if(!matcher.matches()){
				logger.error("invalid group sync message {}", message.getContent().toStringUtf8());
				return null;
			}
			String did = matcher.group(1);
			String domain = serviceLocator.getDomain(did);
			String toPrefix=message.getTo().trim().substring(0,message.getTo().trim().indexOf("@"));
			String to=toPrefix+"@"+domain;
			logger.debug("[{}] will go to {}", message.getUid(),to);
			
			return message.toBuilder().setTo(to).build();
			
		}else {
			Address addr = new Address(message.getTo());
			String domain = serviceLocator.getDomain(addr.deviceId);
			logger.debug("[{}] will go to {}", message.getUid(),
					domain);
			return message.toBuilder()
					.setTo(addr.buildAddress(domain))
					.build();
		}
	}
}

