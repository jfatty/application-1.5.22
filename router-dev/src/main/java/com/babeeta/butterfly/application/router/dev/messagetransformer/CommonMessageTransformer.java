package com.babeeta.butterfly.application.router.dev.messagetransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.router.dev.DevRouter;
import com.babeeta.butterfly.misc.Address;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class CommonMessageTransformer extends AbstractMessageTransformer implements MessageTransformer {


    private static final Logger logger = LoggerFactory
            .getLogger(CommonMessageTransformer.class);
    
    public CommonMessageTransformer(Mongo mongo){
    	super(mongo);
    }

	@Override
	public Message transform(Message message) {

        long startTime = System.currentTimeMillis();
        DevRouter.MESSAGE_COUNT.getAndIncrement();
        Address addr = new Address(message.getTo());
        String deviceId = findDeviceId(addr, message);

        if (deviceId != null) {
            String transformed = new StringBuilder(deviceId)
                    .append(".")
                    .append(addr.clientId)
                    .append(".")
                    .append(addr.applicationId)
                    .append("@gateway.dev")
                    .toString();
            logger.debug("[{}]Transform:[{}] --> [{}], Time:{}ms", new Object[]{
                    message.getUid(),
                    message.getTo(),
                    transformed,
                    (System.currentTimeMillis() - startTime)});
            return message.toBuilder().setTo(transformed).build();
        } else {
            logger.debug("[{}]No suitable device. Message has been dropped.",
                    message.getUid());
            return null;
        }
	}
	
	private String findDeviceId(Address addr, Message message) {
		return findDeviceId(addr.applicationId,addr.clientId);
	}

}
