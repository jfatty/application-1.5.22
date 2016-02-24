package com.babeeta.butterfly.application.app.subscription.dao.impl;


import com.babeeta.butterfly.application.app.subscription.dao.SubscriptionDao;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SubscriptionDaoImpl extends BasicDaoImpl implements SubscriptionDao {
	
	private static final String DB_NAME = "subscription";

	public SubscriptionDaoImpl()
	{
		datastore = morphia.createDatastore(mongo, "subscription");
		datastore.ensureIndexes();
	}
	
	/***
	 * 通过aid,cid查询did
	 * @param aid
	 * @param cid
	 * @return
	 */
	public String querydid(String aid,String cid)
	{
		DBCollection c=this.mongo.getDB(DB_NAME).getCollection(DB_NAME);
		DBObject key=new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", cid);
		DBCursor obj=c.find(new BasicDBObject("_id",key));
		while(obj.hasNext())
		{
			return obj.next().get("did").toString();
		}
		return "";
	}
	
	/***
	 * 修改三者关系
	 * @param aid
	 * @param cid
	 * @param did
	 * @return
	 */
	public boolean updateRelationship(String aid,String cid,String did)
	{
		DBCollection c=this.mongo.getDB(DB_NAME).getCollection(DB_NAME);
		DBObject key=new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", cid);
		DBCursor obj=c.find(new BasicDBObject("_id",key));
		
		if(obj.hasNext())
		{
			DBObject deviceId = new BasicDBObject();
			deviceId.put("did", did);
			mongo.getDB(DB_NAME).getCollection(DB_NAME).update(new BasicDBObject("_id",key), new BasicDBObject("did",did));
			return true;
		}
		return false;
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
