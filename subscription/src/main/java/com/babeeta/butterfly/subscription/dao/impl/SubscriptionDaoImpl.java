package com.babeeta.butterfly.subscription.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.subscription.dao.SubscriptionDao;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SubscriptionDaoImpl extends BasicDaoImpl implements
		SubscriptionDao {

	private static final String DB_NAME = "subscription";
	private static final Logger logger=LoggerFactory.getLogger(SubscriptionDaoImpl.class);
	
	public SubscriptionDaoImpl(){
		ensureIndexe();
	}
	
	private void ensureIndexe(){
		logger.info("ensure index for subscription started.");
		BasicDBObject indexOptions = new BasicDBObject().append("background",
				true);
		mongo.getDB(DB_NAME).getCollection(DB_NAME).ensureIndex(new BasicDBObject().append("did", 1), indexOptions);
		logger.info("ensure index for subscription finished.");
	}

	@Override
	public boolean exists(String aid, String cid) {
		DBObject obj = new BasicDBObjectBuilder()
				.add("_id", new BasicDBObjectBuilder()
						.add("aid", aid)
						.add("cid", cid)
						.get())
					.get();
		return mongo.getDB(DB_NAME).getCollection(DB_NAME).findOne(obj) != null;
	}

	@Override
	public void save(String aid, String cid, String did) {
		BasicDBObject key = new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", cid);

		DBObject deviceId = new BasicDBObject();
		deviceId.put("did", did);

		BasicDBObject doc = new BasicDBObject();
		doc.put("_id", key);
		doc.putAll(deviceId);
		doc.put("date", new Date());

		mongo.getDB(DB_NAME).getCollection(DB_NAME).save(doc);
	}

	@Override
	public void remove(String aid, String cid) {
		BasicDBObject key = new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", cid);
		BasicDBObject doc = new BasicDBObject();
		doc.put("_id", key);
		mongo.getDB(DB_NAME).getCollection(DB_NAME).remove(doc);
	}

	@Override
	public List<Map> list(String did) {
		BasicDBObject q=new BasicDBObject().append("did", did);
		
		DBCursor dbCursor=mongo.getDB(DB_NAME).getCollection(DB_NAME).find(q);
		
		List<Map> result=new ArrayList<Map>();
		
		while(dbCursor.hasNext()){
			DBObject dbObject=dbCursor.next();
			
			result.add(((DBObject)dbObject.get("_id")).toMap());
		}
		return result;
	}

}
