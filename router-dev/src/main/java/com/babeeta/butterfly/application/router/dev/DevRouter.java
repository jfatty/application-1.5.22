package com.babeeta.butterfly.application.router.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.AbstractMessageRouter;
import com.babeeta.butterfly.MessageFuture;
import com.babeeta.butterfly.MessageFutureListener;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.router.dev.messagetransformer.CommonMessageTransformer;
import com.babeeta.butterfly.application.router.dev.messagetransformer.DevGatewayReportMessageTransformer;
import com.babeeta.butterfly.application.router.dev.messagetransformer.GroupSyncMessageTransformer;
import com.babeeta.butterfly.application.router.dev.messagetransformer.MessageTransformer;
import com.babeeta.butterfly.application.router.dev.messagetransformer.MessageTransformerFactory;
import com.babeeta.butterfly.misc.Address;
import com.babeeta.butterfly.router.network.MessageSenderImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class DevRouter extends AbstractMessageRouter {

	static final String FIELD__ID = "_id";

    static final String DB_DEV_GATEWAY = "dev_gateway";
    
    private final String  MESSAGE_TYPE_BROADCAST="broadcast"; 
    private final String MESSAGE_TYPE_GROUP_SYNC="groupSync";
    private final String MESSAGE_TYPE_DEVGW_REPORT="devGWReport";
    private final String MESSAGE_TYPE_UNICAST="unicast";
    
    private final Mongo mongo;
	
    private final MessageTransformerFactory messageTransFormerFactory;
    
    private final MessageSender networkMessageSender;

    private static final Logger logger = LoggerFactory
            .getLogger(DevRouter.class);
    
    public static AtomicLong MESSAGE_COUNT = new AtomicLong(0);
    
    public DevRouter(MessageSender messageSender, Mongo mongo) {
    	super(messageSender);
    	networkMessageSender=new MessageSenderImpl();
    	this.mongo=mongo;
        messageTransFormerFactory=new MessageTransformerFactory(mongo);
    }

    @Override
    protected Message transform(final Message message) {
    	if(message.getBroadcast()){
    		broadcast(message);
    		logger.info("[devrouter] [{}] [{}] request route",MESSAGE_TYPE_BROADCAST,message.getUid());
    		return null;
    	}else{
    		MessageTransformer messageTransformer=messageTransFormerFactory.getMessageTransFormer(message);
    		logger.info("[devrouter] [{}] [{}] request route",getMessageType(messageTransformer),message.getUid());
    		return messageTransformer.transform(message);
    	}
    }
    
    private String getMessageType(MessageTransformer messageTransformer){
    	if(messageTransformer instanceof CommonMessageTransformer){
    		return MESSAGE_TYPE_UNICAST;
    	}else if(messageTransformer instanceof GroupSyncMessageTransformer){
    		return MESSAGE_TYPE_GROUP_SYNC;
    	}else if(messageTransformer instanceof DevGatewayReportMessageTransformer){
    		return MESSAGE_TYPE_DEVGW_REPORT;
    	}else{
    		return "unkown";
    	}
    }
    
    private void broadcast(final Message message){
		List<String> devGatewayList=findAllDevGateway();
		
		if(devGatewayList.size()==0){
			logger.error("not found dev gateway,drop broadcast message {}",message.getUid());
		}
		for(String devGateway:devGatewayList){
			String to="broadcast@"+devGateway;
			logger.debug("Sending broadcast [{}] to [{}]", message.getUid(),to);
			
			networkMessageSender.send(message.toBuilder().setTo(to).build()).addListener(
					new MessageFutureListener() {

						@Override
						public void operationComplete(MessageFuture future) {
							if (future.isSuccess()) {
								logger.debug("[{}] broadcast msg is delivered",
										message.getUid());
							} else {
								logger.error("[{}] broadcast msg failed,Cause:{}", message.getUid(),
										future.getCause().getMessage());
							}
						}
					});
		}
    }
    
    private DBCollection getDevGatewayCollection(){
   	 	DB db = mongo.getDB(DB_DEV_GATEWAY);
        DBCollection dbCollection = db.getCollection(DB_DEV_GATEWAY);
        return dbCollection;
   }
   
   private List<String> findAllDevGateway(){
	   	List<String> result=new ArrayList<String>();
	   	
	   	DBCursor dbCursor= getDevGatewayCollection().find();
	   	
	   	while(dbCursor.hasNext()){
	   		result.add((String)dbCursor.next().get(FIELD__ID));
	   	}
	   	return result;
   }
    

}
