package com.babeeta.butterfly.application.app.service;

import org.apache.commons.lang.StringUtils;

public class MessageNotFoundException extends Exception {
	private String messageId;

	public MessageNotFoundException(String messageId){
		this.messageId=messageId;
	}
	
	public String getMessageId() {
		return messageId;
	}
}
