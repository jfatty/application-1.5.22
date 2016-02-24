package com.babeeta.butterfly.application.app.record.dao.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.reliable.MessageStatus;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MessageRecordDaoImplTest {

	private MessageRecordDaoImpl dao;

	@Before
	public void init() {
		Mongo mongo = null;
		try {
			mongo = new Mongo("mongodb", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}

		dao = new MessageRecordDaoImpl();
		dao.datastore = dao.morphia.createDatastore(mongo, "MessageRecord_"
				+ getTestDBNameSufix());

		dao.datastore.delete(dao.datastore.createQuery(MessageRecord.class));
	}

	protected String getTestDBNameSufix() {
		String hostName = null;
		String hostAddress = null;

		try {
			hostName = InetAddress.getLocalHost().getHostName()
					.replaceAll("\\W", "_");
			hostAddress = InetAddress.getLocalHost().getHostAddress()
					.replaceAll("\\W", "_");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostName + "_" + hostAddress;
	}
	@Test
	public void updateRecipientTest(){
		String targetRecipient="targetRecipient";
		String targetAppId="targetAppId";
		
		MessageRecord targetRecordOne = getTestMessageRecord();
		targetRecordOne.setRecipient(targetRecipient);
		targetRecordOne.setAppId(targetAppId);
		targetRecordOne.setStatus(MessageStatus.DELAYING.toString());
		dao.datastore.save(targetRecordOne);
		
		MessageRecord targetRecordTwo = getTestMessageRecord();
		targetRecordTwo.setRecipient(targetRecipient);
		targetRecordTwo.setAppId(targetAppId);
		targetRecordTwo.setStatus(MessageStatus.DELIVERING.toString());
		dao.datastore.save(targetRecordTwo);
		
		
		MessageRecord controlGroupRecordOne= getTestMessageRecord();
		controlGroupRecordOne.setStatus(MessageStatus.DELAYING.toString());
		controlGroupRecordOne.setRecipient(targetRecipient+"diff");
		controlGroupRecordOne.setAppId(targetAppId);
		dao.datastore.save(controlGroupRecordOne);
		
		
		MessageRecord controlGroupRecordTwo= getTestMessageRecord();
		controlGroupRecordTwo.setStatus(MessageStatus.DELIVERING.toString());
		controlGroupRecordTwo.setRecipient(targetRecipient);
		controlGroupRecordTwo.setAppId(targetAppId+"diff");
		dao.datastore.save(controlGroupRecordTwo);
		
		
		dao.updateRecipient(targetRecipient, "newCid", targetAppId);
		
		//TODO 需要验证代码
	}
	
	
	@Test
	public void modifyMessageRecordContent() {
		int count = dao.datastore.createQuery(MessageRecord.class).asList()
				.size();
		assertEquals(0, count);

		MessageRecord messageRecord = getTestMessageRecord();
		dao.datastore.save(messageRecord);
		// 对照组测试数据
		MessageRecord controlGroupMessageRecord = getTestMessageRecord();
		controlGroupMessageRecord.setMessageId("diff"
				+ messageRecord.getMessageId());
		controlGroupMessageRecord.setContent(messageRecord.getContent());
		controlGroupMessageRecord.setDataType(messageRecord.getDataType());
		dao.datastore.save(controlGroupMessageRecord);

		Assert.assertEquals(2, dao.datastore.createQuery(MessageRecord.class)
				.asList().size());

		byte[] modifyContent = (new String(messageRecord.getContent()) + "modify")
				.getBytes();
		String modifyDataType = "modify" + messageRecord.getDataType();
		dao.modifyMessageRecordContent(messageRecord.getMessageId(),
				modifyDataType, modifyContent);

		assertEquals(2, dao.datastore.createQuery(MessageRecord.class)
				.asList().size());

		// 验证需要修改的数据确实被修改正确
		MessageRecord actualMessageRecord = dao.datastore.createQuery(
				MessageRecord.class)
				.filter("_id", messageRecord.getMessageId()).get();
		messageRecord.setDataType(modifyDataType);
		messageRecord.setContent(modifyContent);
		assertMessageRecordEquals(messageRecord, actualMessageRecord);

		// 验证其他数据没有被修改
		MessageRecord actualControlGroupMessageRecord = dao.datastore
				.createQuery(MessageRecord.class).filter("_id",
						controlGroupMessageRecord.getMessageId()).get();
		assertMessageRecordEquals(controlGroupMessageRecord,
				actualControlGroupMessageRecord);
	}
	@Test
	public void findAndModifyDelayingMsg(){
		MessageRecord targetMessageRecord=getTestMessageRecord();
		targetMessageRecord.setStatus(MessageStatus.DELAYING.toString());
		targetMessageRecord.setDelayUntil(DateUtils.addMinutes(new Date(), 1));
		targetMessageRecord.setDelayExecBy(null);
		
		dao.datastore.save(targetMessageRecord);
		
		String modifyToDelayingExecBy="modifyToDelayingExecBy";
		MessageRecord actualMessageRecord= dao.findAndModifyDelayingMsg(DateUtils.addMinutes(new Date(), 3), modifyToDelayingExecBy);
		

		targetMessageRecord.setDelayExecBy(modifyToDelayingExecBy);
		assertMessageRecordEquals(targetMessageRecord, actualMessageRecord);
		
	}
	
	private void assertMessageRecordEquals(MessageRecord expect,
			MessageRecord actual) {
		assertEquals(expect.getMessageId(), actual.getMessageId());
		assertEquals(expect.getAppId(), actual.getAppId());
		assertEquals(expect.getBroadcastFlag(), actual.getBroadcastFlag());
		assertEquals(new String(expect.getContent()), new String(actual
				.getContent()));
		assertEquals(expect.getDataType(), actual.getDataType());
		assertEquals(expect.getDelay(), actual.getDelay());
		assertEquals(expect.getExpire(), actual.getExpire());
		assertEquals(expect.getRecipient(), actual.getRecipient());
		assertEquals(expect.getStatus(), actual.getStatus());
		assertEquals(expect.getDelayExecBy(), actual.getDelayExecBy());
		assertEquals(expect.getDelayUntil().getTime()/1000, actual.getDelayUntil().getTime()/1000);
	}

	private static MessageRecord getTestMessageRecord() {
		MessageRecord messageRecord = new MessageRecord();
		messageRecord.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
		messageRecord.setAppId("testAPPID");
		messageRecord.setBroadcastFlag(true);
		messageRecord.setContent("testContent".getBytes());
		messageRecord.setCreateAt(new Date());
		messageRecord.setDataType("testDataType");
		messageRecord.setDelay(1234);
		messageRecord.setExpire(321);
		messageRecord.setLastModified(new Date());
		messageRecord.setRecipient("testrecipient");
		messageRecord.setStatus("teststatus");
		messageRecord.setDelayUntil(new Date());
		return messageRecord;
	}
	
	
	
	private void assertMessageRecordCount(int count){
		assertEquals(count, dao.datastore.createQuery(MessageRecord.class).countAll());
	}

}
