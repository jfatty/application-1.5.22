package com.babeeta.butterfly.application.router.dev.messagetransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public abstract class AbstractMessageTransformer {
	static final String FIELD__ID = "_id";

	static final String FIELD_DEVICE_ID = "did";

	static final String FIELD_CLIENT_ID = "cid";

	static final String FIELD_APPLICATION_ID = "aid";

	static final String DB_SUBSCRIPTION = "subscription";

	private final Mongo mongo;
	
	private static  final Logger logger=LoggerFactory.getLogger(AbstractMessageTransformer.class); 
	
	public AbstractMessageTransformer(Mongo mongo){
		this.mongo=mongo;
	}
	
	protected String findDeviceId(String aid,String cid){
        DB db = mongo.getDB(DB_SUBSCRIPTION);
        DBCollection dbCollection = db.getCollection(DB_SUBSCRIPTION);
        DBObject result = dbCollection
                .findOne(new BasicDBObject(
                        FIELD__ID,
                        new BasicDBObjectBuilder()
                                .add(FIELD_APPLICATION_ID, aid)
                                .add(FIELD_CLIENT_ID, cid)
                                .get()));
        if (result != null) {
            return (String) result.get(FIELD_DEVICE_ID);
        } else {
            logger.info("can not find device id with aid {} cid {}.",aid,cid);
            return null;
        }
    
	}
}
