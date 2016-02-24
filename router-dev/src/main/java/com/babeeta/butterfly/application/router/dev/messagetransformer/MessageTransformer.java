package com.babeeta.butterfly.application.router.dev.messagetransformer;

import com.babeeta.butterfly.MessageRouting.Message;

public interface MessageTransformer {
	/**
	 * @param message
	 * @return
	 */
	public Message transform(Message message);
}
