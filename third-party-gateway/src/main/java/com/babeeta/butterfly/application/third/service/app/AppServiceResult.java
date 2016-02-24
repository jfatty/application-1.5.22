package com.babeeta.butterfly.application.third.service.app;

import com.babeeta.butterfly.application.third.service.AbstractHttpRPCResult;

public class AppServiceResult extends AbstractHttpRPCResult {
	public AppServiceResult(boolean success, int statusCode) {
		super(success, statusCode);
	}

	private String messageId;

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageId() {
		return messageId;
	}

	private Object messageStatus;

	public void setMessageStatus(Object messageStatus) {
		this.messageStatus = messageStatus;
	}

	public Object getMessageStatus() {
		return messageStatus;
	}
}
