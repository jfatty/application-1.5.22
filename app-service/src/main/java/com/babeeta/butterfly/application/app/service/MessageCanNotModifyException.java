package com.babeeta.butterfly.application.app.service;

import org.apache.commons.lang.StringUtils;

import com.babeeta.butterfly.application.reliable.MessageStatus;

/**
 * @author lisong
 *
 */
public class MessageCanNotModifyException extends Exception {
	private String message;
	private boolean broadcast;
	private String messageStatus;

	public MessageCanNotModifyException(boolean isBroadcast,
			String messageStatus) {
		this.broadcast=isBroadcast;
		this.messageStatus=messageStatus;
	}

	public MessageCanNotModifyException(boolean isBroadcast,
			String messageStatus, String message) {
		this.broadcast=isBroadcast;
		this.messageStatus=messageStatus;
		this.message=message;
	}

	@Override
	public String getMessage() {
		if(StringUtils.isNotBlank(message)){
			return message;
		}else{
			return super.getMessage();
		}
	}

	public boolean isBroadcast() {
		return broadcast;
	}


	public String getMessageStatus() {
		return messageStatus;
	}
}
