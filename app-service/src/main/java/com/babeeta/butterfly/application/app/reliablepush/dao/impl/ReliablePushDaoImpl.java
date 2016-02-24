package com.babeeta.butterfly.application.app.reliablepush.dao.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.babeeta.butterfly.application.app.reliablepush.dao.ReliablePushDao;
import com.babeeta.butterfly.application.app.reliablepush.entity.ReliablePushBean;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;


/***
 * 可靠投递
 * @author zeyong.xia
 * @date 2011-9-19
 */
public class ReliablePushDaoImpl extends BasicDaoImpl implements ReliablePushDao{
 
	private static final String DB_NAME="reliable_push";
	private final static Logger logger = LoggerFactory
	.getLogger(ReliablePushDaoImpl.class);
	
	public ReliablePushDaoImpl()
	{
		datastore = morphia.createDatastore(mongo, DB_NAME);
		datastore.ensureIndexes();
	}
	
	/***
	 * 查询未ack的消息id
	 * @param aid
	 * @param cid
	 * @return
	 */
	public List<ReliablePushBean> queryNotAppAckMessageId(String aid,String cid)
	{
		BasicDBObject key = new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", cid);
		Query<ReliablePushBean> query=this.datastore.createQuery(ReliablePushBean.class)
		                              .filter("status", "DELIVERING")
		                              .filter("key", key);
		return query.asList();
//		List<ReliablePushBean> list=new ArrayList<ReliablePushBean>();
////		BasicDBObject key = new BasicDBObject();
////		key.put("aid", aid);
////		key.put("cid", cid);
//		DBCursor c=mongo.getDB(DB_NAME).getCollection(DB_NAME).find(new BasicDBObject("key",key),new BasicDBObject("status","DELIVERING"));
//		ReliablePushBean bean=null;
//		DBObject obj=null;
//		while(c.hasNext())
//		{
//			bean=new ReliablePushBean();
//			obj=c.next();
//			bean.setId(obj.get("_id").toString());
//			bean.setAge(0);//TODO
//			bean.setExpiredAt((Date)(obj.get("expiredAt")));
//			//bean.setKey(obj.get("key").toString());
//			bean.setMessage((byte[])obj.get("message"));
//			bean.setParentId(obj.get("parentId").toString());
//			bean.setStatus(obj.get("status").toString());
//			bean.setType(0);
//			list.add(bean);
//		}
//		return list;
	}	
	
	/***
	 * 锁定消息状态
	 */
	public void updateStatus(List<ReliablePushBean> list)
	{
		if(list!=null&&list.size()>0)
		{
			DBCollection  co=mongo.getDB(DB_NAME).getCollection(DB_NAME);
			for(ReliablePushBean bean :list)
			{
				co.update(new BasicDBObject("_id",bean.getId()), new BasicDBObject("status","EXPIRED"));
			}
		}
	}
	/***
	 * 重新设置消息状态
	 * 设定是应该多给10秒过期时间
	 */
	public void updateStatusToPush(List<ReliablePushBean> list)
	{
		//TODO
		if(list!=null&&list.size()>0)
		{
			DBCollection  co=mongo.getDB(DB_NAME).getCollection(DB_NAME);
			for(ReliablePushBean bean :list)
			{
				co.update(new BasicDBObject("_id",bean.getId()), new BasicDBObject("status","DELIVERING"));
			}
		}
	}
	

	/***
	 * 修改cid
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 */
	public void updateCid(String oldCid,String newCid,String aid)
	{
		BasicDBObject key=new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", oldCid);
		
		Query<ReliablePushBean> querys=this.datastore.createQuery(ReliablePushBean.class)
//        .field("status").notEqual("ACKED")
        .filter("status", "<> ACKED")
        .filter("status", "<> EXPIRED")
//        .field("status").notEqual("EXPIRED")
        .filter("key", key);
		List<ReliablePushBean> list=querys.asList();
		logger.debug("[ReliablePushDaoImpl] updateCid ,MessageListSize is {}",list.size());
		if(list!=null&&list.size()>0)
		{
			for(ReliablePushBean b :list)
			{
				logger.debug("[ReliablePushDaoImpl] updateCid ,iterator list, status is {}",b.getStatus());
				Query<ReliablePushBean> query=this.datastore.createQuery(ReliablePushBean.class).filter("_id", b.getId());
				BasicDBObject keys=new BasicDBObject();
				keys.put("aid", aid);
				keys.put("cid", newCid);
				UpdateOperations<ReliablePushBean> ops = datastore
				.createUpdateOperations(ReliablePushBean.class);
				ops.set("key", keys);
				this.datastore.update(query, ops);
			}
		}
	}
	
	/***
	 * 更新消息内容
	 * @param content
	 * @param parentId
	 */
	public boolean updateMessage(byte[] content,String parentId)
	{
		Query<ReliablePushBean> querys=this.datastore.createQuery(ReliablePushBean.class)
        .filter("parentId", parentId);
		List<ReliablePushBean> list=querys.asList();
		if(list!=null&&list.size()>0)
		{
			for(ReliablePushBean push:list)
			{
				if ((!push.getStatus().equals("ACKED"))&&(!push.getStatus().equals("EXPIRED"))&&(!push.getStatus().equals("DELETED"))) 
				{
					Query<ReliablePushBean> query=this.datastore.createQuery(ReliablePushBean.class).filter("_id", push.getId());
					if(query.asList()!=null&&query.asKeyList().size()>0)
					{
						UpdateOperations<ReliablePushBean> ops = datastore
						.createUpdateOperations(ReliablePushBean.class);
						ops.set("message", content);
						this.datastore.update(query, ops);
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	/***
	 * 将未投递成功的消息置为删除状态
	 * @param parentId
	 */
	public boolean deleteMessage(String parentId)
	{
		Query<ReliablePushBean> querys=this.datastore.createQuery(ReliablePushBean.class)
        .filter("parentId", parentId);
		List<ReliablePushBean> list=querys.asList();
		if(list!=null&&list.size()>0)
		{
			for(ReliablePushBean push:list)
			{
				if ((!push.getStatus().equals("ACKED"))&&(!push.getStatus().equals("EXPIRED"))&&(!push.getStatus().equals("DELETED"))) 
				{
					Query<ReliablePushBean> query=this.datastore.createQuery(ReliablePushBean.class).filter("_id", push.getId());
					if(query.asList()!=null&&query.asKeyList().size()>0)
					{
						UpdateOperations<ReliablePushBean> ops = datastore
						.createUpdateOperations(ReliablePushBean.class);
						ops.set("status", "DELETED");
						this.datastore.update(query, ops);
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	/***
	 * 变更目的地
	 * @param aid
	 * @param oldCid
	 * @param newCid
	 */
	public void updateRecipient(String aid,String oldCid,String newCid)
	{
		BasicDBObject key=new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", oldCid);
		
		Query<ReliablePushBean> querys=this.datastore.createQuery(ReliablePushBean.class)
		.field("status").notEqual("ACKED")
        .filter("key", key);
		List<ReliablePushBean> list=querys.asList();
		if(list!=null&&list.size()>0)
		{
			for(ReliablePushBean b :list)
			{
				Query<ReliablePushBean> query=this.datastore.createQuery(ReliablePushBean.class).filter("_id", b.getId());
				BasicDBObject keys=new BasicDBObject();
				keys.put("aid", aid);
				keys.put("cid", newCid);
				UpdateOperations<ReliablePushBean> ops = datastore
				.createUpdateOperations(ReliablePushBean.class);
				ops.set("key", keys);
				this.datastore.update(query, ops);
			}
		}
	}
	
	/***
	 * 查询消息
	 * @param uid
	 * @return
	 */
	public String queryByParentId(String uid)
	{
		logger.debug("[ReliablePushDaoImpl] queryByParentId,parentId is{}",uid);
		DBObject messageObj = new BasicDBObject();
		messageObj.put("_id", uid);
		DBObject result = getReliableDBCollection().findOne(messageObj);
		if (result == null) {
			return "";
		} else {
			
				result = getReliableDBCollection().findOne(messageObj);
				StringBuffer sb=new StringBuffer();
				if(result.get("ackedAt")!=null)
				{
					sb.append(result.get("status")).append(":").append(result.get("ackedAt"));
				}
				else
				{
					sb.append(result.get("status"));
				}
				logger.debug("[ReliablePushDaoImpl] queryByParentId,result is{}",sb.toString());
			return String.valueOf(sb.toString());
		}
	}
	
	private DBCollection getReliableDBCollection() {
		DB db = mongo.getDB(DB_NAME);
		return db.getCollection(DB_NAME);
	}
}
