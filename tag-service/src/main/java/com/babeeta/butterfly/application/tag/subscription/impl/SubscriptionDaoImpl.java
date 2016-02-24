package com.babeeta.butterfly.application.tag.subscription.impl;


import com.babeeta.butterfly.application.tag.subscription.SubscriptionDao;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SubscriptionDaoImpl extends BasicDaoImpl implements SubscriptionDao {
	
	private static final String DB_NAME = "subscription";

	public SubscriptionDaoImpl()
	{
	}
	
	/***
	 * 判断是否存在
	 * @param aid
	 * @param cid
	 * @return
	 */
	public boolean exists(String aid, String cid)
	{
		DBObject obj = new BasicDBObjectBuilder()
		.add("_id", new BasicDBObjectBuilder()
				.add("aid", aid)
				.add("cid", cid)
				.get())
			.get();
		return mongo.getDB(DB_NAME).getCollection(DB_NAME).findOne(obj) != null;
	}

}
