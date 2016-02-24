package com.babeeta.butterfly.application.reliable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.reliable.dao.MessageReceiveDao;
import com.babeeta.butterfly.application.reliable.dao.MessageReceiveDaoImpl;
import com.babeeta.butterfly.application.reliable.dao.ReliablePushDao;
import com.babeeta.butterfly.application.reliable.dao.ReliablePushDaoImpl;
import com.google.protobuf.ByteString;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

/**
 * Created by IntelliJ IDEA. User: XYuser Date: 11-1-22 Time: 下午2:10 To change
 * this template use File | Settings | File Templates.
 */
public class ReliablePushImpl implements ReliablePush {
	private static final Logger logger = LoggerFactory
			.getLogger(ReliablePushImpl.class);
	private static final ReliablePushImpl defaultInstance = new ReliablePushImpl();
	private ReliablePushDao reliablePushDao;
	private MessageReceiveDao messageReceiveDao;
	private final String RELIABLE_DB_NAME = "reliable_push";
	private final String SUBSCRIPTION_DB_NAME = "subscription";
	private final String STATUS_DELIVERING = "DELIVERING";// 投递中，未ack
	private final String STATUS_ACKED = "ACKED";// 已act
	private final String STATUS_EXPIRED = "EXPIRED";// 已过期
	private final String STATUS_EXPIRED_ACKED = "EXPIRED_ACKED";// 过期且已经ack
	private static Mongo mongo;
	
	static {
		try {
			MongoOptions mongoOptions = new MongoOptions();
			mongoOptions.threadsAllowedToBlockForConnectionMultiplier = 5;
			mongoOptions.connectionsPerHost = 50;
			mongoOptions.autoConnectRetry = true;
			
			ServerAddress serverAddress=new ServerAddress("mongodb", 27017);
			mongo = new Mongo(serverAddress,mongoOptions);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	private ReliablePushImpl() {
		reliablePushDao = ReliablePushDaoImpl.getDefaultInstance();
		messageReceiveDao=MessageReceiveDaoImpl.getDefaultInstance();
	}

	public static ReliablePushImpl getDefaultInstance() {
		return defaultInstance;
	}

	static ReliablePushImpl getInstance(ReliablePushDao reliablePushDao,MessageReceiveDao messageReceiveDao) {
		ReliablePushImpl reliablePushImpl = new ReliablePushImpl();
		reliablePushImpl.reliablePushDao = reliablePushDao;
		reliablePushImpl.messageReceiveDao=messageReceiveDao;
		return reliablePushImpl;
	}

	@Override
	public List<MessageRouting.Message> getMessagesList(String did) {
		DBObject deviceId = new BasicDBObject();
		deviceId.put("did", did);

		DBCursor keyCursor = getSubscriptionDBCollection().find(deviceId);
		
		
		List<MessageRouting.Message> list = new ArrayList<MessageRouting.Message>();
		Set<String> set = new HashSet<String>();
		while (keyCursor.hasNext()) {
			// 获取subscription数据库中的aid、cid集合
			DBObject dbObject = keyCursor.next();
			BasicDBObject idObj = (BasicDBObject) dbObject.get("_id");
			String aid = idObj.get("aid").toString();
			String cid = idObj.get("cid").toString();

			if (set.contains(aid + "." + cid)) {// 去掉重复的aid和cid组合
				continue;
			}

			// 查询Message DB中的消息
			DBObject query = new BasicDBObject();
			query.put("key",
					new BasicDBObjectBuilder().add("aid", aid).add("cid", cid)
							.get());
			query.put("status", STATUS_DELIVERING);
			query.put("broadcast",false);

			DBCursor messageCursor = getReliableDBCollection().find(query);
			while (messageCursor.hasNext()) {
				DBObject result = messageCursor.next();
				String uid = result.get("_id").toString();
				if (expired(result)) {
					updateExpire(uid);
				} else {
					byte[] messageBody = (byte[]) result.get("message");
					Date createAt=null;
					
					if(result.get("createdAt")!=null && result.get("createdAt") instanceof Date){
						createAt=((Date)result.get("createdAt"));
					}else{
						createAt=new Date();
					}

					MessageRouting.Message message = MessageRouting.Message
							.newBuilder()
							.setUid(uid)
							.setContent(ByteString.copyFrom(messageBody))
							.setFrom("reliable_push@dev")
							.setTo(new StringBuilder(cid).append(".")
									.append(aid).append("@dev").toString())
							.setDate(createAt.getTime()).build();
					list.add(message);
				}
			}
			set.add(aid + "." + cid);
		}
		return list;
	}
	
	private List<MessageRouting.Message> getMessageList(String aid,String cid,boolean isBroadcast){
		List<MessageRouting.Message> list = new ArrayList<MessageRouting.Message>();

		// 查询Message DB中的消息
		DBObject query = new BasicDBObject();
		query.put("key",
				new BasicDBObjectBuilder().add("aid", aid).add("cid", cid)
						.get());
		query.put("status", STATUS_DELIVERING);
		query.put("broadcast",isBroadcast);

		DBCursor messageCursor = getReliableDBCollection().find(query);
		while (messageCursor.hasNext()) {
			DBObject result = messageCursor.next();
			String uid = result.get("_id").toString();
			if (expired(result)) {
				updateExpire(uid);
			} else {
				byte[] messageBody = (byte[]) result.get("message");
				
				Date createAt=null;
				
				if(result.get("createdAt")!=null && result.get("createdAt") instanceof Date){
					createAt=((Date)result.get("createdAt"));
				}else{
					createAt=new Date();
				}


				MessageRouting.Message message = MessageRouting.Message
						.newBuilder()
						.setUid(uid)
						.setContent(ByteString.copyFrom(messageBody))
						.setFrom("reliable_push@dev")
						.setTo(new StringBuilder(cid).append(".")
								.append(aid).append("@dev").toString())
						.setDate(createAt.getTime()).build();
				list.add(message);
			}
		}
		return list;
	}

	@Override
	public String getMessageStatus(String uid) {
		Map result = queryAndModifyExpiredReliablePush(uid);
		return String.valueOf(result.get("status"));
	}

	@Override
	public Map queryAndModifyExpiredReliablePush(String uid) {

		DBObject messageObj = new BasicDBObject();
		messageObj.put("_id", uid);
		DBObject result = getReliableDBCollection().findOne(messageObj);

		if (result == null) {
			return null;
		} else {
			if (result.get("status").equals(STATUS_DELIVERING)
					&& expired(result)) {
				updateExpire(uid);
				result = getReliableDBCollection().findOne(messageObj);
			}
			return result.toMap();
		}
	}

	@Override
	public boolean saveMessage(Message message, String aid, String cid,
			int totalSubMessage) {

		DBObject key = new BasicDBObject();
		key.put("aid", aid);
		key.put("cid", cid);

		DBObject messageObj = new BasicDBObject();
		messageObj.put("_id", message.getUid());
		messageObj.put("key", key);
		messageObj.put("message", message.getContent().toByteArray());
		messageObj.put("status", STATUS_DELIVERING);
		messageObj.put("age", message.getExpire());
		messageObj.put(ReliablePush.FIELD_BROADCAST, message.getBroadcast());
		Date now = new Date();
		if (message.getExpire() > 0) {
			messageObj.put("expiredTime",
					DateUtils.addHours(now, message.getExpire()));
		}
		messageObj.put("createdAt", now);
		messageObj.put("parentId", message.getParentId());
		if (totalSubMessage >= 0) {
			messageObj.put(ReliablePush.FIELD_TOTAL_SUB_MESSAGE,
					totalSubMessage);
		}

		// this.getReliableDBCollection().ensureIndex(key);
		getReliableDBCollection().save(messageObj);

		return true;
	}

	@Override
	public boolean saveMessage(MessageRouting.Message message, String aid,
			String cid) {
		return saveMessage(message, aid, cid, -1);
	}

	@Override
	public boolean updateAckWhenDelivering(String uid,String cid) {

		Map record = reliablePushDao.findById(uid);

		if (record == null) {
			logger.error("[{}] reliable_push not exists", uid);
			return false;
		}

		if (!record.containsKey(ReliablePush.FIELD_ID)) {
			logger.error("[{}] miss field _id", uid);
			return false;
		}
		if(record.containsKey(ReliablePush.FIELD_BROADCAST) && (Boolean)record.get(ReliablePush.FIELD_BROADCAST)){
			if(StringUtils.isNotBlank(cid)){
				messageReceiveDao.save(uid, cid);
			}else{
				logger.error("cid is blank");
			}
			
			reliablePushDao.increaseAckedCount(uid, 1);
			
			logger.debug("[{}] increase acked count success.",uid);
		}else{
			reliablePushDao.updateAckedWhenDelivering(uid, new Date());
		}
		return true;
	}

	@Override
	public void updateExpire(String uid) {
		DBObject messageObj = new BasicDBObject();
		messageObj.put("_id", uid);

		DBObject result = getReliableDBCollection().findOne(messageObj);
		if (result == null) {
			return;
		}
		// 只更改未ack的消息
		if (STATUS_DELIVERING.equals(result.get("status"))) {
			result.put("status", STATUS_EXPIRED);
			result.put("expiredAt", new Date());
			getReliableDBCollection().save(result);
		}
	}

	private boolean expired(DBObject result) {
		if (result.containsField("msgExptime")) {
			// 旧数据
			int exptime = ((Number) result.get("msgExptime")).intValue();
			try {
				return DateUtils.addSeconds(
						DateUtils.parseDate(result.get("saveTime").toString(),
								new String[] { "yyyy-MM-dd HH:mm:ss" }),
						exptime).before(new Date());
			} catch (ParseException ignore) {
				return true;
			}
		} else if (result.containsField("age")) {
			// 新数据
			// return DateUtils.addSeconds((Date) result.get("createdAt"),
			// ((Number) result.get("age")).intValue()).before(new Date());
			int age = (Integer) result.get("age");
			if (age == -1) {
				return false;
			}
			// 新数据
			return DateUtils.addHours((Date) result.get("createdAt"),
					((Number) result.get("age")).intValue()).before(new Date());
		} else {
			return true;
		}
	}

	private DBCollection getReliableDBCollection() {
		DB db = mongo.getDB(RELIABLE_DB_NAME);
		return db.getCollection(RELIABLE_DB_NAME);
	}

	private DBCollection getSubscriptionDBCollection() {
		DB db = mongo.getDB(SUBSCRIPTION_DB_NAME);
		return db.getCollection(SUBSCRIPTION_DB_NAME);
	}

	@Override
	public void updateMessageContentById(String id, byte[] content) {
		reliablePushDao.updateMessageContentById(id, content);
	}

	@Override
	public void deleteMessageById(String id) {
		reliablePushDao.deleteMessageById(id);
	}

	@Override
	public void updateAppAck(String uid) {
		Map record = reliablePushDao.findById(uid);

		if (record == null) {
			logger.error("[{}] reliable_push not exists", uid);
			return;
		}

		if (!record.containsKey(ReliablePush.FIELD_ID)) {
			logger.error("[{}] miss field _id", uid);
			return;
		}

		if(record.containsKey(ReliablePush.FIELD_BROADCAST) && (Boolean)record.get(ReliablePush.FIELD_BROADCAST)){
			reliablePushDao.increaseAppAckedCount(uid, 1);
			
			logger.info("[{}] increase app acked count success.",uid);//统计用
			
		}else{
			reliablePushDao.updateAppAcked(uid, new Date());
			
			logger.info("[{}] update unicast app acked success.",uid);//统计用
		}
	}

	@Override
	public List<Message> getDeliveringBroadcastMessageList(String aid,
			String tag,String cid) {
		List<MessageRouting.Message> messageList= getMessageList(aid, tag, true);
		
		if(messageList==null || messageList.size()==0){
			logger.debug("not found delivering broadcast msg for {}.{}",aid,tag);
			return null;
		}
		Iterator<Message> it=messageList.iterator();
		
		while(it.hasNext()){
			Message message=it.next();
			if(messageReceiveDao.exists(message.getUid(), cid)){
				it.remove();
			}
		}
		return messageList;
	}

}
