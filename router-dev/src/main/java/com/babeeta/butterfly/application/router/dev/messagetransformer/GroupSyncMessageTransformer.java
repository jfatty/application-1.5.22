package com.babeeta.butterfly.application.router.dev.messagetransformer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.router.dev.DevRouter;
import com.babeeta.butterfly.misc.Address;
import com.google.protobuf.ByteString;
import com.mongodb.Mongo;

public class GroupSyncMessageTransformer extends AbstractMessageTransformer
		implements MessageTransformer {

	private static final Pattern REGX_CONTENT = Pattern
			.compile("([0-9a-z]+)\\.([0-9a-z]+)\\.(.+)");
	private static final Logger logger = LoggerFactory
			.getLogger(GroupSyncMessageTransformer.class);

	public GroupSyncMessageTransformer(Mongo mongo) {
		super(mongo);
	}
	
	@Override
	public Message transform(Message message) {
		DevRouter.MESSAGE_COUNT.getAndIncrement();
		long startTime = System.currentTimeMillis();

		String content = message.getContent().toStringUtf8();
		Matcher matcher = REGX_CONTENT.matcher(content);
		if (!matcher.matches()) {
			logger.error("invalid group sync message {}", content);
			return null;
		} else {
			String cid = matcher.group(1);
			String aid = matcher.group(2);
			String deviceId = findDeviceId(aid, cid);

			if (deviceId != null) {
				
				return message.toBuilder().setContent(
						ByteString.copyFromUtf8(deviceId + "." + content))
						.setTo(message.getTo().substring(0,message.getTo().indexOf("@")) + "@gateway.dev").build();
			} else {
				logger
						.debug(
								"[{}] No suitable device.group sync message has been dropped.",
								message.getUid());
				return null;
			}
		}
	}
}