package com.babeeta.butterfly.application.third;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import com.babeeta.butterfly.application.TestEnviroment;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;


public class DaoInit {


	private Mongo mongo;

	public DaoInit() {
		try {
			mongo = new Mongo(TestEnviroment.MONGO_HOST, 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public String[] initAppAccount() {
		String appId = generateUUID();
		String appKey = generateUUID();

		BasicDBObject appAccount = new BasicDBObject();
		appAccount.put("_id", appId);
		appAccount.put("key", appKey);
		appAccount.put("status", "NORMAL");
		appAccount.put("createDate", new Date());

		mongo.getDB("account-app").getCollection("account").save(appAccount);

		return new String[] { appId, appKey };
	}
	
	public String initDevAccount(){
		String did=generateUUID();
		BasicDBObject account=new BasicDBObject();
		account.put("_id", did);
		account.put("key", generateUUID());
		account.put("createDate", new Date());
		
		mongo.getDB("account-dev").getCollection("account").save(account);
		
		
		return did;
	}
	
	
	public String initSubscription(String aid,String did) {

		String cid = generateUUID();

		BasicDBObject doc = new BasicDBObject();
		DBObject key=new BasicDBObjectBuilder().add("aid", aid).add("cid",cid).get();
		doc.put("_id", key);
		doc.put("did", did);
		doc.put("date", new Date());

		mongo.getDB("subscription").getCollection("subscription").save(doc);

		return cid;
	}

	private String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}


}
