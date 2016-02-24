package com.babeeta.butterfly.application.reliable.dao;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.babeeta.butterfly.application.reliable.MessageStatus;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

public class ReliablePushDaoImplTest {

	private ReliablePushDaoImpl dao;
	private static final String CONTROL_GROUP_FILED = "controlGroupFiled";

	@Before
	public void init() {
		dao = ReliablePushDaoImpl.getInstance("reliable_push_"
				+ getTestDBNameSufix(), "reliable_push");
		dao.getDbCollection().remove(new BasicDBObject());

		Assert.assertEquals(0, dao.getDbCollection().count());
	}

	@Test
	public void findByIdTest() {
		Map testEntity = getTestReliablePushEntity(generateUUID());
		insertTestEntity(testEntity);

		// 插入一组对照数据，保证 非目标数据不会被查询出来
		Map controlGroupTestEntity = getTestReliablePushEntity(generateUUID());
		insertTestEntity(controlGroupTestEntity);

		assertEquals(2, dao.getDbCollection().count());

		Map actual = dao.findById((String) testEntity
				.get(ReliablePush.FIELD_ID));

		assertEquals(testEntity.get(ReliablePush.FIELD_ID),
				actual.get(ReliablePush.FIELD_ID));

		assertEquals(testEntity.get("controlGroupFiled"),
				actual.get("controlGroupFiled"));

	}

	@Test
	public void updateMessageContentByIdTest() {
		Map testEntity = getTestReliablePushEntity("testId11");
		testEntity.put(ReliablePush.FIELD_MESSAGE, "testContent".getBytes());
		dao.getDbCollection().save(new BasicDBObject(testEntity));

		// 插入一组对照的数据，以避免修改了不该修改的数据
		Map controlGroupEntity = getTestReliablePushEntity("testId22");
		controlGroupEntity.put(ReliablePush.FIELD_MESSAGE,
				"testContent".getBytes());
		dao.getDbCollection().save(new BasicDBObject(controlGroupEntity));

		Assert.assertEquals(2, dao.getDbCollection().count());

		byte[] testContent = (new String(
				(byte[]) testEntity.get(ReliablePush.FIELD_MESSAGE)) + "modify")
				.getBytes();

		dao.updateMessageContentById(
				(String) testEntity.get(ReliablePush.FIELD_ID), testContent);

		Assert.assertEquals(2, dao.getDbCollection().count());

		Map actualTestEntity = findById((String) testEntity
				.get(ReliablePush.FIELD_ID));
		Map actualControlGroupEntity = findById((String) controlGroupEntity
				.get(ReliablePush.FIELD_ID));

		// 验证不改修改的记录是否被修改
		assertEquals(controlGroupEntity.get(ReliablePush.FIELD_ID),
				actualControlGroupEntity.get(ReliablePush.FIELD_ID));
		assertEquals(
				new String(
						(byte[]) controlGroupEntity
								.get(ReliablePush.FIELD_MESSAGE)),
				new String((byte[]) actualControlGroupEntity
						.get(ReliablePush.FIELD_MESSAGE)));

		// 验证该修改的字段是否修改
		assertEquals(new String(testContent), new String(
				(byte[]) actualTestEntity.get(ReliablePush.FIELD_MESSAGE)));
		// 验证不该修改的字段是否被修改
		assertEquals(testEntity.get("controlGroupFiled"),
				actualTestEntity.get("controlGroupFiled"));
	}

	@Test
	public void deleteMessageById() {
		Map testEntity = getTestReliablePushEntity("testId11");
		dao.getDbCollection().save(new BasicDBObject(testEntity));

		// 插入一组对照的数据，以避免修改了不该修改的数据
		Map controlGroupEntity = getTestReliablePushEntity("testId22");
		dao.getDbCollection().save(new BasicDBObject(controlGroupEntity));

		assertEquals(2, dao.getDbCollection().count());
		dao.deleteMessageById((String) testEntity.get(ReliablePush.FIELD_ID));

		assertEquals(1, dao.getDbCollection().count());

		assertEquals(controlGroupEntity.get(ReliablePush.FIELD_ID), dao
				.getDbCollection().findOne().get(ReliablePush.FIELD_ID));

	}

	@Test
	public void updateAckedWhenDeliveringTest() {
		testUpdateReliablePushAcked(false);
	}

	@Test
	public void updateAppAckedTest() {
		testUpdateReliablePushAcked(true);
	}

	@Test
	public void updateAppAckedCountTest() {
		testUpdateReliablePushAcked(true);
	}

	@Test
	public void updateMessageStatusToExpiredTest() {
		Date maxExpireDate = DateUtils.addDays(new Date(), 1);
		Date expiredAt = new Date();

		Map targetEntityOne = getTestReliablePushEntity();
		targetEntityOne.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());
		targetEntityOne.put(ReliablePush.FIELD_EXPIRED_TIME, new Date());
		insertTestEntity(targetEntityOne);

		// 插入多组目标数据以验证会更新多条
		Map targetEntityTwo = getTestReliablePushEntity();
		targetEntityTwo.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());
		targetEntityTwo.put(ReliablePush.FIELD_EXPIRED_TIME, new Date());
		insertTestEntity(targetEntityTwo);

		// 插入时间比maxExprieDate大的非目标数据
		Map controlGroupEntityOne = getTestReliablePushEntity();
		controlGroupEntityOne.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());
		controlGroupEntityOne.put(ReliablePush.FIELD_EXPIRED_TIME, new Date(
				maxExpireDate.getTime() + 6000));
		insertTestEntity(controlGroupEntityOne);

		// 插入状态不是DELIVERING的非目标数据
		Map controlGroupEntityTwo = getTestReliablePushEntity();
		controlGroupEntityTwo.put(ReliablePush.FIELD_EXPIRED_TIME, new Date());
		controlGroupEntityTwo.put(ReliablePush.FIELD_STATUS, "diff"
				+ MessageStatus.DELIVERING.toString());
		insertTestEntity(controlGroupEntityTwo);

		// 插入没有过期时间的非目标数据
		Map controlGroupEntityThree = getTestReliablePushEntity();
		controlGroupEntityOne.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());
		insertTestEntity(controlGroupEntityThree);

		assertEquals(5, dao.getDbCollection().count());

		dao.updateDeliveringMsgStatusToExpired(maxExpireDate, expiredAt);

		assertEquals(5, dao.getDbCollection().count());

		// 验证两个目标数据都被修改
		targetEntityOne.put(ReliablePush.FIELD_STATUS,
				MessageStatus.EXPIRED.toString());
		targetEntityOne.put(ReliablePush.FIELD_EXPIRED_AT, expiredAt);
		targetEntityTwo.put(ReliablePush.FIELD_STATUS,
				MessageStatus.EXPIRED.toString());
		targetEntityTwo.put(ReliablePush.FIELD_EXPIRED_AT, expiredAt);

		Map actualTargetOneEntity = findById(targetEntityOne.get(
				ReliablePush.FIELD_ID).toString());
		Map actualTargetTwoEntity = findById(targetEntityTwo.get(
				ReliablePush.FIELD_ID).toString());

		assertReliablePushEquals(targetEntityOne, actualTargetOneEntity);
		assertReliablePushEquals(targetEntityTwo, actualTargetTwoEntity);

		// 验证三个目标数据都没有被修改
		Map actualControlGroupOne = findById(controlGroupEntityOne.get(
				ReliablePush.FIELD_ID).toString());
		Map actualControlGroupTwo = findById(controlGroupEntityTwo.get(
				ReliablePush.FIELD_ID).toString());
		Map actualControlGroupThree = findById(controlGroupEntityThree.get(
				ReliablePush.FIELD_ID).toString());
		assertReliablePushEquals(controlGroupEntityOne, actualControlGroupOne);
		assertReliablePushEquals(controlGroupEntityTwo, actualControlGroupTwo);
		assertReliablePushEquals(controlGroupEntityThree,
				actualControlGroupThree);
	}

	@Test
	public void updateCidWhenDelayingOrDeliveringTest() {

		String targetAid = "targetAid";
		String targetCid = "targetCid";

		// 插入一个delaying和一个delvering的数据以确保目标数据都被修改
		Map targetEntityOne = getTestReliablePushEntity();
		targetEntityOne.put("key", new BasicDBObject().append("aid", targetAid)
				.append("cid", targetCid));
		targetEntityOne.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELAYING.toString());
		insertTestEntity(targetEntityOne);

		Map targetEntityTwo = getTestReliablePushEntity();
		targetEntityTwo.put("key", new BasicDBObject().append("aid", targetAid)
				.append("cid", targetCid));
		targetEntityTwo.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());
		insertTestEntity(targetEntityTwo);

		// 插入几个非目标数据以确保其他数据未被修改
		Map controlGroupEntityOne = getTestReliablePushEntity();
		controlGroupEntityOne.put(
				"key",
				new BasicDBObject().append("aid", targetAid + "diff").append(
						"cid", targetCid));
		controlGroupEntityOne.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELAYING.toString());
		insertTestEntity(controlGroupEntityOne);

		Map controlGroupEntityTwo = getTestReliablePushEntity();
		controlGroupEntityTwo.put(
				"key",
				new BasicDBObject().append("aid", targetAid).append("cid",
						targetCid + "diff"));
		controlGroupEntityTwo.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());
		insertTestEntity(controlGroupEntityTwo);

		Map controlGroupEntityThree = getTestReliablePushEntity();
		controlGroupEntityThree.put(
				"key",
				new BasicDBObject().append("aid", targetAid).append("cid",
						targetCid));
		controlGroupEntityThree.put(ReliablePush.FIELD_STATUS,
				MessageStatus.ACKED.toString());
		insertTestEntity(controlGroupEntityThree);

		String newCid = "newCid";
		dao.updateCidWhenDelayingOrDelivering(targetCid, newCid, targetAid);

		// TODO 需要验证
	}

	@Test
	public void getExpiredMessageIdTest() {
		String targetAid = "targetAid";

		String targetOneId = generateUUID();
		Map targetOneEntity = getTestReliablePushEntity(targetOneId);
		targetOneEntity.put("status", MessageStatus.EXPIRED.toString());
		targetOneEntity.put("key", new BasicDBObject().append("aid", targetAid)
				.append("cid", "cid111"));
		insertTestEntity(targetOneEntity);

		String targetTwoId = generateUUID();
		Map targetTwoEntity = getTestReliablePushEntity(targetTwoId);
		targetTwoEntity.put("status", MessageStatus.EXPIRED.toString());
		targetTwoEntity.put("key", new BasicDBObject().append("aid", targetAid)
				.append("cid", "cid222"));
		insertTestEntity(targetTwoEntity);

		// 插入非目标数据以确认非目标数据不会被查询出来
		Map controlGroupOneEntity = getTestReliablePushEntity();
		controlGroupOneEntity.put("status", MessageStatus.EXPIRED.toString()
				+ "diff");
		controlGroupOneEntity.put(
				"key",
				new BasicDBObject().append("aid", targetAid).append("cid",
						"cid333"));
		insertTestEntity(controlGroupOneEntity);

		Map controlGroupTwoEntity = getTestReliablePushEntity();
		controlGroupTwoEntity.put("status", MessageStatus.EXPIRED.toString());
		controlGroupTwoEntity.put(
				"key",
				new BasicDBObject().append("aid", targetAid + "diff").append(
						"cid", "cid444"));
		insertTestEntity(controlGroupTwoEntity);

		List<String> actual = dao.getExpiredMessageId(targetAid);
		assertEquals(2, actual.size());
		Assert.assertTrue(actual.contains(targetOneId));
		Assert.assertTrue(actual.contains(targetTwoId));
	}

	@Test
	public void updateMessageStatusTest() {
		String targetStatus = "targetStatus";

		// 插入两个目标数据
		String targetOneId = generateUUID();
		Map targetOneEntity = getTestReliablePushEntity(targetOneId);
		targetOneEntity.put(ReliablePush.FIELD_STATUS, targetStatus + "diff");
		insertTestEntity(targetOneEntity);

		String targetTwoId = generateUUID();
		Map targetTwoEntity = getTestReliablePushEntity(targetTwoId);
		targetTwoEntity.put(ReliablePush.FIELD_STATUS, targetStatus + "diff");
		insertTestEntity(targetTwoEntity);

		// 插入非目标数据以确保非目标数据不会被修改
		String controlGroupId = generateUUID();
		Map controlGroupEntity = getTestReliablePushEntity(controlGroupId);
		controlGroupEntity
				.put(ReliablePush.FIELD_STATUS, targetStatus + "diff");
		insertTestEntity(controlGroupEntity);

		assertEquals(3, dao.getDbCollection().getCount());

		List<String> messageIdList = new ArrayList<String>();
		messageIdList.add(targetOneId);
		messageIdList.add(targetTwoId);

		dao.updateMessageStatus(messageIdList, targetStatus);

		assertEquals(3, dao.getDbCollection().getCount());
		// 验证目标数据已被修改
		targetOneEntity.put(ReliablePush.FIELD_STATUS, targetStatus);
		Map actualTargetOneEntity = findById(targetOneId);
		assertReliablePushEquals(targetOneEntity, actualTargetOneEntity);

		targetTwoEntity.put(ReliablePush.FIELD_STATUS, targetStatus);
		Map actualTargetTwoEntity = findById(targetTwoId);
		assertReliablePushEquals(targetTwoEntity, actualTargetTwoEntity);

		// 验证非目标数据未被修改
		Map actualControlGroupEntity = findById(controlGroupId);
		assertReliablePushEquals(controlGroupEntity, actualControlGroupEntity);
	}

	private void testUpdateReliablePushAcked(boolean isAppAcked) {

		Map testEntity = getTestReliablePushEntity(generateUUID());
		if (isAppAcked) {
			testEntity.put(ReliablePush.FIELD_STATUS, "diff"
					+ MessageStatus.APP_ACKED.toString());
		} else {
			testEntity.put(ReliablePush.FIELD_STATUS,
					MessageStatus.DELIVERING.toString());
		}
		insertTestEntity(testEntity);

		// 插入一组对照数据，保证 非目标数据不会被修改
		Map controlGroupTestEntity = getTestReliablePushEntity(generateUUID());
		insertTestEntity(controlGroupTestEntity);
		controlGroupTestEntity.put(ReliablePush.FIELD_STATUS, "diff"
				+ MessageStatus.DELIVERING.toString());

		assertEquals(2, dao.getDbCollection().count());

		Date now = new Date();
		if (isAppAcked) {
			dao.updateAppAcked(
					testEntity.get(ReliablePush.FIELD_ID).toString(), now);
		} else {
			dao.updateAckedWhenDelivering(testEntity.get(ReliablePush.FIELD_ID)
					.toString(), now);
		}

		assertEquals(2, dao.getDbCollection().count());

		Map actualTestEntity = findById(testEntity.get(ReliablePush.FIELD_ID)
				.toString());

		// 验证目标字段确实已经修改完成
		if (isAppAcked) {
			assertEquals(MessageStatus.APP_ACKED.toString(),
					actualTestEntity.get(ReliablePush.FIELD_STATUS));
		} else {
			assertEquals(MessageStatus.ACKED.toString(),
					actualTestEntity.get(ReliablePush.FIELD_STATUS));
		}

		Date actualDate = (Date) actualTestEntity
				.get(ReliablePush.FIELD_ACKED_AT.toString());

		// 时间比较精确到秒就行了
		assertEquals(now.getTime() / 1000, actualDate.getTime() / 1000);
		// 验证非目标字段没有被修改
		assertEquals(testEntity.get("controlGroupFiled"),
				actualTestEntity.get("controlGroupFiled"));

		// 验证非目标记录没有被修改
		Map actualControlGroupEntity = findById(controlGroupTestEntity.get(
				ReliablePush.FIELD_ID).toString());
		assertEquals(controlGroupTestEntity.get("controlGroupFiled"),
				actualControlGroupEntity.get("controlGroupFiled"));

	}

	@Test
	public void findAndModifyExpiredMsgTest() {
		String testId = "testId";
		String appId = "testAppId";
		
		Map targetEntity = getTestReliablePushEntity(testId);
		targetEntity.put("key", new BasicDBObject().append("aid", appId));
		targetEntity.put(ReliablePush.FIELD_STATUS,
				MessageStatus.EXPIRED.toString());
		targetEntity.remove(ReliablePush.FIELD_EXPRIED_NOTIFY_BY);

		insertTestEntity(targetEntity);

		String modifyExpiredNotifyBy = "testmodifyExpiredNotifyBy";

		Map actual = dao.findAndModifyExpriedMsg(appId, modifyExpiredNotifyBy);

		assertReliablePushEquals(targetEntity, actual);
		
		 assertEquals(modifyExpiredNotifyBy, findById(testId).get(ReliablePush.FIELD_EXPRIED_NOTIFY_BY));
	}
	@Test
	public void findAndModifyDeliveringMsgTest(){
		String testId = "testId";
		String appId = "testAppId";
		Date now=new Date();
		
		
		Map targetEntity = getTestReliablePushEntity(testId);
		targetEntity.put("key", new BasicDBObject().append("aid", appId));
		targetEntity.put(ReliablePush.FIELD_STATUS,
				MessageStatus.DELIVERING.toString());
		targetEntity.put(ReliablePush.FIELD_EXPIRED_TIME, DateUtils.addMinutes(now, -3));
		targetEntity.remove(ReliablePush.FIELD_EXPRIED_NOTIFY_BY);
		
		insertTestEntity(targetEntity);
		
		String modifyExpiredNotifyBy = "testmodifyExpiredNotifyBy";
		
		
		Map acutal=dao.findAndModifyDeliveringMsg(appId, now, modifyExpiredNotifyBy);
		
		assertReliablePushEquals(targetEntity,acutal);
		
		Map entityInDB=findById(testId);
		
		assertEquals(modifyExpiredNotifyBy, entityInDB.get(ReliablePush.FIELD_EXPRIED_NOTIFY_BY));
		assertEquals(MessageStatus.EXPIRED.toString(), entityInDB.get(ReliablePush.FIELD_STATUS));
		
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

	private Map findById(String id) {
		return dao
				.getDbCollection()
				.findOne(
						new BasicDBObjectBuilder().add(ReliablePush.FIELD_ID,
								id).get()).toMap();
	}

	private Map getTestReliablePushEntity() {
		return getTestReliablePushEntity(generateUUID());
	}

	private Map getTestReliablePushEntity(String id) {
		Map map = new HashMap();
		map.put(ReliablePush.FIELD_ID, id);
		map.put(CONTROL_GROUP_FILED, "testValue");
		map.put(ReliablePush.FIELD_EXPIRED_AT, new Date());
		map.put(ReliablePush.FIELD_EXPIRED_TIME, new Date());
		return map;
	}

	private void insertTestEntity(Map entity) {
		dao.getDbCollection().save(new BasicDBObject(entity));
	}

	private Map insertTestEntity() {
		return insertTestEntity(generateUUID());
	}

	private Map insertTestEntity(String id) {
		Map testEntity = getTestReliablePushEntity();
		testEntity.put(ReliablePush.FIELD_ID, id);
		insertTestEntity(testEntity);
		return testEntity;
	}

	private String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	private void assertReliablePushEquals(Map expected, Map actual) {
		Date expectedExpiredAt = (Date) expected
				.remove(ReliablePush.FIELD_EXPIRED_AT);
		Date actualExpiredAt = (Date) actual
				.remove(ReliablePush.FIELD_EXPIRED_AT);
		assertDateValueInSeconds(expectedExpiredAt, actualExpiredAt);

		Date expectedExpiredTime = (Date) expected
				.remove(ReliablePush.FIELD_EXPIRED_TIME);
		Date actualExpiredTime = (Date) actual
				.remove(ReliablePush.FIELD_EXPIRED_TIME);

		assertDateValueInSeconds(expectedExpiredTime, actualExpiredTime);

		assertEquals(expected, actual);
	}

	private void assertDateValueInSeconds(Date expectedDate, Date actualDate) {
		if (expectedDate == null && actualDate != null) {
			Assert.fail();
		}

		if (expectedDate != null && actualDate == null) {
			Assert.fail();
		}

		if (expectedDate != null && actualDate != null) {
			// 时间比较精确到秒就行了
			assertEquals(expectedDate.getTime() / 1000,
					actualDate.getTime() / 1000);
		}
	}

}
