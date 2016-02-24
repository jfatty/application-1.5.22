package com.babeeta.butterfly.application.router.dev.messagetransformer;

import com.babeeta.butterfly.MessageRouting.Message;
import com.mongodb.Mongo;

public class MessageTransformerFactory {

	private Mongo mongo;
	private MessageTransformer commonMessageTransFormer;
	private MessageTransformer groupSyncMessageTransformer;
	private MessageTransformer devGatewayReportMessageTransformer;
	private MessageTransformer doNothingMessageTransformer;

	public MessageTransformerFactory(Mongo mongo) {
		this.mongo = mongo;
		commonMessageTransFormer = new CommonMessageTransformer(mongo);
		groupSyncMessageTransformer = new GroupSyncMessageTransformer(mongo);
		devGatewayReportMessageTransformer = new DevGatewayReportMessageTransformer(
		        mongo);
		doNothingMessageTransformer = new DoNothingMessageTransformer(mongo);
	}

	public MessageTransformer getMessageTransFormer(Message message) {
		if ("cleanGroup@dev".equalsIgnoreCase(message.getTo().trim())) {
			return doNothingMessageTransformer;
		} else if ("addGroup@dev".equalsIgnoreCase(message.getTo().trim())
		        || "removeGroup@dev".equalsIgnoreCase(message.getTo().trim())) {
			return groupSyncMessageTransformer;
		} else if ("devGatewayReport@dev".equalsIgnoreCase(message.getTo()
		        .trim())) {
			return devGatewayReportMessageTransformer;
		} else {
			return commonMessageTransFormer;
		}
	}
}
