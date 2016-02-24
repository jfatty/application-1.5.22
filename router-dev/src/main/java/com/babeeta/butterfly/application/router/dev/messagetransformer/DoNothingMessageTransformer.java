package com.babeeta.butterfly.application.router.dev.messagetransformer;

import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.router.dev.DevRouter;
import com.mongodb.Mongo;

public class DoNothingMessageTransformer extends AbstractMessageTransformer
        implements MessageTransformer {
	public DoNothingMessageTransformer(Mongo mongo) {
		super(mongo);
	}

	@Override
	public Message transform(Message message) {
		DevRouter.MESSAGE_COUNT.getAndIncrement();
		return message;
	}
}
