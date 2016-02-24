package com.babeeta.butterfly.application.reliable.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

public class MessageReceiveDaoImpl implements MessageReceiveDao{
	
	private static final Logger log = LoggerFactory.getLogger(MessageReceiveDaoImpl.class);
	private static Mongo mongo;
	private static DBCollection dbCollection;
	private static final MessageReceiveDaoImpl DEFAULT_INSTANCE = new MessageReceiveDaoImpl();
	private static final String DB_NAME="message_receive";
	private static final String COLLECTION_NAME="message_receive";
	
	private static final String FIELD_CREATE_TIME="create_time";
	public static final String FIELD_CLIENT_ID="clientId";
	public static final String FIELD_MESSAGE_ID="messageId";

	static {
		try {
			MongoOptions mongoOptions = new MongoOptions();
			mongoOptions.threadsAllowedToBlockForConnectionMultiplier = 5;
			mongoOptions.connectionsPerHost = 50;
			mongoOptions.autoConnectRetry = true;
			
			ServerAddress serverAddress=new ServerAddress("mongodb", 27017);
			mongo = new Mongo(serverAddress,mongoOptions);
			dbCollection=mongo.getDB(DB_NAME).getCollection(COLLECTION_NAME);
			ensureIIndexs(dbCollection);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private MessageReceiveDaoImpl(){
		
	}
	
	private static void ensureIIndexs(DBCollection dbCollection){
		log.info("ensureIndex for {} started",COLLECTION_NAME);
		BasicDBObject indexOptions=new BasicDBObject().append("background", true);
		dbCollection.ensureIndex(new BasicDBObject().append(FIELD_CLIENT_ID, 1).append(FIELD_MESSAGE_ID, 1),indexOptions);
		
		log.info("ensureIndex for {} finished",COLLECTION_NAME);
	}
	
	public static synchronized MessageReceiveDaoImpl getDefaultInstance(){
		return DEFAULT_INSTANCE;
	}
	
	@Override
	public boolean exists(String messageId, String clientId) {
		DBObject dbObject=dbCollection.findOne(new BasicDBObject().append(FIELD_CLIENT_ID, clientId).append(FIELD_MESSAGE_ID, messageId));
		return dbObject!=null;
	}

	@Override
	public void save(String messageId, String clientId) {
		dbCollection.save(new BasicDBObject().append(FIELD_CLIENT_ID, clientId).append(FIELD_MESSAGE_ID, messageId).append(FIELD_CREATE_TIME, new Date()));
	}

}
