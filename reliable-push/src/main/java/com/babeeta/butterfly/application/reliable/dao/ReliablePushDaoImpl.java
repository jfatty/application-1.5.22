package com.babeeta.butterfly.application.reliable.dao;

import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.reliable.MessageStatus;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

public class ReliablePushDaoImpl implements ReliablePushDao {

	private static final Logger log = LoggerFactory
			.getLogger(ReliablePushDaoImpl.class);
	private static final ReliablePushDaoImpl DEFAULT_INSTANCE = new ReliablePushDaoImpl();
	private static final String DEFAULT_DB_NAME = "reliable_push";
	private static final String DEFAULT_COLLECTION_NAME = "reliable_push";
	private static final String DOT_CHAR = ".";
	static final String FIELD_AID = "aid";
	static final String FIELD_CID = "cid";
	private static final String OPERATOR_SET = "$set";
	private static final String OPERATOR_IN = "$in";

	private static Mongo mongo;

	private DBCollection dbCollection;

	static {
		try {
			MongoOptions mongoOptions = new MongoOptions();
			mongoOptions.threadsAllowedToBlockForConnectionMultiplier = 5;
			mongoOptions.connectionsPerHost = 50;
			mongoOptions.autoConnectRetry = true;

			ServerAddress serverAddress = new ServerAddress("mongodb", 27017);
			mongo = new Mongo(serverAddress, mongoOptions);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private ReliablePushDaoImpl() {

	}

	public static ReliablePushDaoImpl getDefaultInstance() {
		DB db = mongo.getDB(DEFAULT_DB_NAME);
		DEFAULT_INSTANCE.dbCollection = db
				.getCollection(DEFAULT_COLLECTION_NAME);
		ensureIIndexs(DEFAULT_INSTANCE.dbCollection);

		return DEFAULT_INSTANCE;
	}

	private static void ensureIIndexs(DBCollection dbCollection) {
		log.info("ensure index started");

		BasicDBObject indexOptions = new BasicDBObject().append("background",
				true);
		dbCollection.ensureIndex(new BasicDBObject().append(
				ReliablePush.FIELD_STATUS, 1).append(
				ReliablePush.FIELD_EXPIRED_TIME, 1), indexOptions);
		dbCollection
				.ensureIndex(new BasicDBObject().append(
						ReliablePush.FIELD_STATUS, 1).append(
						ReliablePush.FIELD_KEY + DOT_CHAR + FIELD_AID, 1),
						indexOptions);
		dbCollection
				.ensureIndex(new BasicDBObject().append(
						ReliablePush.FIELD_STATUS, 1).append(
						ReliablePush.FIELD_KEY + DOT_CHAR + FIELD_CID, 1),
						indexOptions);
		dbCollection.ensureIndex(new BasicDBObject().append(
				ReliablePush.FIELD_STATUS, 1).append(
				ReliablePush.FIELD_KEY + DOT_CHAR + FIELD_AID, 1).append(
				ReliablePush.FIELD_EXPRIED_NOTIFY_BY, 1), indexOptions);
		dbCollection.ensureIndex(new BasicDBObject().append(
				ReliablePush.FIELD_STATUS, 1).append(
				ReliablePush.FIELD_KEY + DOT_CHAR + FIELD_AID, 1).append(
				ReliablePush.FIELD_EXPIRED_TIME, 1), indexOptions);
		dbCollection.ensureIndex(new BasicDBObject().append(
				ReliablePush.FIELD_STATUS, 1).append(
				ReliablePush.FIELD_EXPRIED_NOTIFY_BY, 1), indexOptions);
		dbCollection.ensureIndex(new BasicDBObject().append(
				ReliablePush.FIELD_KEY, 1).append(ReliablePush.FIELD_STATUS, 1)
				.append(ReliablePush.FIELD_BROADCAST, 1), indexOptions);

		log.info("ensure index finihsed");
	}

	protected static ReliablePushDaoImpl getInstance(String dbName,
			String collectionName) {
		ReliablePushDaoImpl reliablePushDaoImpl = new ReliablePushDaoImpl();
		DB db = mongo.getDB(dbName);
		reliablePushDaoImpl.dbCollection = db.getCollection(collectionName);
		ensureIIndexs(reliablePushDaoImpl.dbCollection);
		return reliablePushDaoImpl;
	}

	@Override
	public void deleteMessageById(String id) {
		dbCollection.remove(new BasicDBObjectBuilder().add(
				ReliablePush.FIELD_ID, id).get());
	}

	@Override
	public void updateMessageContentById(String id, byte[] content) {
		DBObject q = new BasicDBObjectBuilder().add(ReliablePush.FIELD_ID, id)
				.get();

		DBObject o = new BasicDBObjectBuilder().add(
				"$set",
				new BasicDBObjectBuilder().add(ReliablePush.FIELD_MESSAGE,
						content).get()).get();

		dbCollection.update(q, o);
	}

	protected DBCollection getDbCollection() {
		return dbCollection;
	}

	@Override
	public void updateDeliveringMsgStatusToExpired(Date maxExpiredTime,
			Date expiredAt) {
		if (maxExpiredTime == null) {
			throw new IllegalArgumentException(
					"miss required argument maxExpiredTime.");
		}
		if (expiredAt == null) {
			throw new IllegalArgumentException(
					"miss required argument expiredAt.");
		}

		DBObject q = new BasicDBObject();
		q.put(ReliablePush.FIELD_STATUS, MessageStatus.DELIVERING.toString());
		q.put(ReliablePush.FIELD_EXPIRED_TIME, new BasicDBObjectBuilder().add(
				"$lte", maxExpiredTime).get());

		DBObject setValue = new BasicDBObject();
		setValue.put(ReliablePush.FIELD_STATUS, MessageStatus.EXPIRED
				.toString());
		setValue.put(ReliablePush.FIELD_EXPIRED_AT, expiredAt);

		DBObject o = new BasicDBObject();
		o.put("$set", setValue);

		dbCollection.update(q, o, false, true);
	}

	@Override
	public List<String> getExpiredMessageId(String appId) {
		DBObject query = new BasicDBObject();
		query.put(ReliablePush.FIELD_STATUS, MessageStatus.EXPIRED.toString());
		query.put(ReliablePush.FIELD_KEY + DOT_CHAR + FIELD_AID, appId);

		DBObject keys = new BasicDBObject();
		keys.put(ReliablePush.FIELD_ID, 1);

		DBCursor dbCursor = dbCollection.find(query, keys);

		List<String> result = new ArrayList<String>();

		while (dbCursor.hasNext()) {
			DBObject dbObject = dbCursor.next();
			result.add((String) dbObject.get(ReliablePush.FIELD_ID));
		}
		return result;
	}

	@Override
	public void updateMessageStatus(List<String> messageIdList, String status) {
		if (messageIdList == null || messageIdList.size() == 0) {
			log.warn("message id list is emtpy ,not msg will be update.");
			return;
		}

		DBObject query = new BasicDBObject();
		query.put(ReliablePush.FIELD_ID, new BasicDBObject().append(
				OPERATOR_IN, messageIdList));

		DBObject update = new BasicDBObject();
		update.put(OPERATOR_SET, new BasicDBObject().append(
				ReliablePush.FIELD_STATUS, status));

		dbCollection.update(query, update, false, true);
	}

	@Override
	public void updateAckedWhenDelivering(String messageId, Date ackedTime) {
		updateAcked(messageId, ackedTime, false);
	}

	@Override
	public void updateAppAcked(String messageId, Date ackedTime) {
		updateAcked(messageId, ackedTime, true);
	}

	private void updateAcked(String messageId, Date ackedTime,
			boolean isAppAcked) {

		DBObject query = new BasicDBObject();
		query.put(ReliablePush.FIELD_ID, messageId);

		if (!isAppAcked) {
			query.put(ReliablePush.FIELD_STATUS, MessageStatus.DELIVERING
					.toString());
		}

		DBObject setValue = new BasicDBObject();

		if (isAppAcked) {
			setValue.put(ReliablePush.FIELD_STATUS, MessageStatus.APP_ACKED
					.toString());
		} else {
			setValue.put(ReliablePush.FIELD_STATUS, MessageStatus.ACKED
					.toString());
		}

		setValue.put(ReliablePush.FIELD_ACKED_AT,
				ackedTime == null ? new Date() : ackedTime);

		DBObject update = new BasicDBObjectBuilder().add("$set", setValue)
				.get();

		dbCollection.update(query, update, false, false);

	}

	@Override
	public Map findById(String id) {
		DBObject messageObj = new BasicDBObject();
		messageObj.put("_id", id);
		DBObject result = dbCollection.findOne(messageObj);
		return result == null ? null : result.toMap();
	}

	@Override
	public void increaseAckedCount(String id, int increaseCount) {
		DBObject query = new BasicDBObjectBuilder().add("_id", id).get();

		DBObject increaseValue = new BasicDBObject().append(
				ReliablePush.FIELD_ACKED_COUNT, increaseCount);
		DBObject update = new BasicDBObjectBuilder().add("$inc", increaseValue)
				.get();

		dbCollection.update(query, update, false, false);
	}

	@Override
	public void increaseAppAckedCount(String id, int increaseCount) {
		DBObject query = new BasicDBObjectBuilder().add("_id", id).get();

		DBObject increaseValue = new BasicDBObject().append(
				ReliablePush.FIELD_APP_ACKED_COUNT, increaseCount);
		DBObject update = new BasicDBObjectBuilder().add("$inc", increaseValue)
				.get();

		dbCollection.update(query, update, false, false);
	}

	@Override
	public void updateCidWhenDelayingOrDelivering(String oldCid, String newCid,
			String aid) {
		Map<String, String> statusDeliveringCondition = new LinkedHashMap<String, String>();
		statusDeliveringCondition.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());

		Map<String, String> statusDelayingCondition = new LinkedHashMap<String, String>();
		statusDelayingCondition.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELAYING.toString());

		DBObject q = new BasicDBObject();
		BasicDBList list = new BasicDBList();
		list.add(statusDeliveringCondition);
		list.add(statusDelayingCondition);

		q.put("$or", list);

		q.put(ReliablePush.FIELD_KEY, new BasicDBObject()
				.append(FIELD_AID, aid).append(FIELD_CID, oldCid));

		BasicDBObject o = new BasicDBObject();
		o.put("$set", new BasicDBObject().append(ReliablePush.FIELD_KEY
				+ DOT_CHAR + FIELD_CID, newCid));

		dbCollection.update(q, o, false, true);
	}

	@Override
	public Map findAndModifyExpriedMsg(String appIdQueryField,
			String modifyExpiredNotifyBy) {
		BasicDBObject query = new BasicDBObject().append(
				ReliablePush.FIELD_STATUS, MessageStatus.EXPIRED.toString());
		query = query.append(ReliablePush.FIELD_KEY + DOT_CHAR + FIELD_AID,
				appIdQueryField);
		query = query.append(ReliablePush.FIELD_EXPRIED_NOTIFY_BY,
				new BasicDBObject().append("$exists", false));

		BasicDBObject update = new BasicDBObject().append("$set",
				new BasicDBObject().append(
						ReliablePush.FIELD_EXPRIED_NOTIFY_BY,
						modifyExpiredNotifyBy));

		DBObject result = dbCollection.findAndModify(query, update);
		return result == null ? null : result.toMap();
	}

	@Override
	public Map findAndModifyDeliveringMsg(String appIdQueryField,
			Date maxExpiredQueryField, String modifyExpriedNotifyBy) {
		BasicDBObject query = new BasicDBObject().append(
				ReliablePush.FIELD_STATUS, MessageStatus.DELIVERING.toString());
		query = query.append(ReliablePush.FIELD_EXPRIED_NOTIFY_BY,
				new BasicDBObject().append("$exists", false));
		query = query.append(ReliablePush.FIELD_KEY + DOT_CHAR + FIELD_AID,
				appIdQueryField);

		BasicDBObject maxExpiredQueryCondition = new BasicDBObject().append(
				"$lt", maxExpiredQueryField);
		query = query.append(ReliablePush.FIELD_EXPIRED_TIME,
				maxExpiredQueryCondition);

		BasicDBObject setValue = new BasicDBObject();
		setValue = setValue.append(ReliablePush.FIELD_EXPRIED_NOTIFY_BY,
				modifyExpriedNotifyBy);
		setValue = setValue.append(ReliablePush.FIELD_STATUS,
				MessageStatus.EXPIRED.toString());
		BasicDBObject update = new BasicDBObject().append("$set", setValue);

		DBObject result = dbCollection.findAndModify(query, update);
		return result == null ? null : result.toMap();
	}

	@Override
	public void unsetExpiredNotifyBy(String expiredNotifyByQueryField) {
		BasicDBObject q = new BasicDBObject().append(ReliablePush.FIELD_STATUS,
				MessageStatus.EXPIRED.toString())
				.append(ReliablePush.FIELD_EXPRIED_NOTIFY_BY,
						expiredNotifyByQueryField);

		BasicDBObject o = new BasicDBObject();
		o.append("$unset", new BasicDBObject().append(
				ReliablePush.FIELD_EXPRIED_NOTIFY_BY, 1));

		dbCollection.update(q, o);
	}
}
