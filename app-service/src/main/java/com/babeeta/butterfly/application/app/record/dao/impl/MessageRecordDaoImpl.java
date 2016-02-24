package com.babeeta.butterfly.application.app.record.dao.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.app.MessageContext;
import com.babeeta.butterfly.application.app.record.dao.MessageRecordDao;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.service.DelayMessageTask;
import com.babeeta.butterfly.application.app.service.DelayMessageTaskService;
import com.babeeta.butterfly.application.app.service.impl.DelayMessageTaskServiceImpl;
import com.babeeta.butterfly.application.reliable.MessageStatus;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MessageRecordDaoImpl extends BasicDaoImpl implements
		MessageRecordDao {

	private final static Logger logger = LoggerFactory
			.getLogger(MessageRecordDaoImpl.class);

	public MessageRecordDaoImpl() {

		datastore = morphia.createDatastore(mongo, "MessageRecord");
		
		DBCollection dbCollection=datastore.getCollection(MessageRecord.class);
		
		logger.info("ensure index started");
		BasicDBObject indexOptions=new BasicDBObject().append("background", true);
		dbCollection.ensureIndex(new BasicDBObject().append("status", 1),indexOptions);
		dbCollection.ensureIndex(new BasicDBObject().append("status", 1).append("delayExecBy", 1),indexOptions);
		dbCollection.ensureIndex(new BasicDBObject().append("status", 1).append("delayExecBy", 1).append("delayUntil", 1),indexOptions);
		dbCollection.ensureIndex(new BasicDBObject().append("status", 1).append("appId", 1).append("recipient", 1),indexOptions);
		
		logger.info("ensure index finished");
	}

	@Override
	public boolean saveMessageRecord(MessageContext mtx) {

		logger.debug("[MessageRecordDaoImpl] start saveMessageRecord ");
		MessageRecord record = new MessageRecord();

		record.setMessageId(mtx.getMessageId());
		record.setAppId(mtx.getSender());
		record.setRecipient(mtx.getRecipient());
		record.setDataType(mtx.getDataType());
		record.setContent(mtx.getContent());
		record.setExpire(mtx.getExpire());
		record.setBroadcastFlag(mtx.getBroadcastFlag());

		record.setStatus(mtx.getStatus());
		// /////zeyong.xia add
		record.setCreateAt(new Date());
		record.setDelay(mtx.getDelay());
		record.setLastModified(new Date());
		datastore.save(record);
		logger
				.debug(
						"[MessageRecordDaoImpl] end saveMessageRecord ,messageId is {}",
						record.getMessageId());
		return true;
	}

	@Override
	public boolean saveMessageRecord(MessageRecord messageRecord) {
		datastore.save(messageRecord);
		return true;
	}

	@Override
	public MessageRecord getMessageRecordbyId(String messageId) {
		MessageRecord record = new MessageRecord();
		record.setMessageId(messageId);
		record = datastore.get(record);
		return record;
	}

	@Override
	public boolean updateDelivering(String messageId) {
		Query<MessageRecord> query = datastore.createQuery(MessageRecord.class)
				.filter("_id", messageId);
		MessageRecord result = query.get();
		if (result != null) {
			if (result.getStatus().equals(MessageStatus.DELAYING.toString())) {
				UpdateOperations<MessageRecord> ops = datastore
						.createUpdateOperations(MessageRecord.class)
						.set("status", MessageStatus.DELIVERING.toString())
						.set("lastModified", new Date());
				datastore.update(query, ops);
				return true;
			}
		}
		return false;
	}

	@Override
	public void modifyMessageRecordContent(String messageId, String dataType,
			byte[] content) {

		Query<MessageRecord> query = datastore.createQuery(MessageRecord.class)
				.filter("_id", messageId);

		UpdateOperations<MessageRecord> ops = datastore
				.createUpdateOperations(MessageRecord.class)
				.set("dataType", dataType).set("content", content)
				.set("lastModified", new Date());
		datastore.update(query, ops);
	}

	/***
	 * 修改收件人
	 * 
	 * @param oldCid
	 * @param newCid
	 */
	public void updateRecipient(String oldCid, String newCid, String aid) {
		Map<String, String> statusDeliveringCondition = new LinkedHashMap<String, String>();
		statusDeliveringCondition.put("status",
				MessageStatus.DELIVERING.toString());

		Map<String, String> statusDelayingCondition = new LinkedHashMap<String, String>();
		statusDelayingCondition
				.put("status", MessageStatus.DELAYING.toString());

		DBObject q = new BasicDBObject();
		BasicDBList list = new BasicDBList();
		list.add(statusDeliveringCondition);
		list.add(statusDelayingCondition);
		q.put("$or", list);
		q.put("appId", aid);
		q.put("recipient", oldCid);

		BasicDBObject o = new BasicDBObject();
		o.put("$set", new BasicDBObject().append("recipient", newCid));

		DBCollection dbCollection = datastore
				.getCollection(MessageRecord.class);
		dbCollection.update(q, o, false, true);
	}

	/***
	 * 查询未过期的消息
	 * 
	 * @return
	 */
	public List<MessageRecord> queryNOtExpired() {
		Query<MessageRecord> query = datastore.createQuery(MessageRecord.class)
				.filter("status", "DELAYING").limit(200);

		return query.asList();
	}

	/****
	 * 更新状态
	 * 
	 * @param uid
	 * @param status
	 */
	public void updateStatus(String uid, String status) {
		logger
				.debug(
						"[MessageRecordDaoImpl] updateStatus,messageId is{} ,status is {}",
						uid, status);
		Query<MessageRecord> query = datastore.createQuery(MessageRecord.class)
				.filter("_id", uid);
		if (query.get() != null) {
			UpdateOperations<MessageRecord> ops = datastore
					.createUpdateOperations(MessageRecord.class);
			ops.set("status", status);
			this.datastore.update(query, ops);
		}
	}

	/***
	 * 是否为广播
	 * 
	 * @param uid
	 * @return
	 */
	public boolean isBroadCast(String uid) {

		logger
				.debug(
						"[MessageRecordDaoImpl] isBroadCast,messageId is{} ,status is {}",
						uid);
		Query<MessageRecord> query = datastore.createQuery(MessageRecord.class)
				.filter("_id", uid);
		if (query.get() != null) {
			return query.get().getBroadcastFlag();
		}
		return false;
	}

	/***
	 * 判断是否存在
	 * 
	 * @param messageId
	 * @return
	 */
	public boolean existsMessageId(String messageId) {
		logger.debug("[MessageRecordDaoImpl] existsMessageId,messageId is{} ",
				messageId);
		Query<MessageRecord> query = datastore.createQuery(MessageRecord.class)
				.filter("_id", messageId);
		if (query.get() != null) {
			return true;
		}
		return false;
	}

	@Override
	public List<MessageRecord> findMessageRecordByDelayingExecBy(
			String delayingExecBy) {
		return datastore.createQuery(MessageRecord.class)
				.filter("status", MessageStatus.DELAYING.toString())
				.filter("delayExecBy", delayingExecBy).asList();
	}

	@Override
	public MessageRecord findAndModifyDelayingMsg(Date maxDelayUtil,
			String modifyToDelayingExecBy) {
		Query<MessageRecord> q = datastore.createQuery(MessageRecord.class)
				.filter("status", MessageStatus.DELAYING.toString()).field(
						"delayExecBy").doesNotExist()
				.field("delayUntil").lessThan(maxDelayUtil);

		UpdateOperations<MessageRecord> ops = datastore.createUpdateOperations(
				MessageRecord.class).set("delayExecBy", modifyToDelayingExecBy);

		return datastore.findAndModify(q, ops);
	}

	@Override
	public void unsetDelayingExecBy(String queryDelayExecby) {
		Query<MessageRecord> query = datastore.createQuery(MessageRecord.class)
				.filter("status", MessageStatus.DELAYING.toString()).filter(
						"delayExecBy", queryDelayExecby);

		UpdateOperations<MessageRecord> ops = datastore.createUpdateOperations(
				MessageRecord.class).unset("delayExecBy");

		datastore.update(query, ops);
	}
}
