package com.babeeta.butterfly.application.app.service.impl;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.babeeta.butterfly.application.app.record.dao.MessageRecordDao;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.service.MessageCanNotModifyException;
import com.babeeta.butterfly.application.app.service.MessageNotFoundException;
import com.babeeta.butterfly.application.reliable.MessageStatus;
import com.babeeta.butterfly.application.reliable.ReliablePush;

public class MessageServiceImplTest {
	private MessageServiceImpl service;
	private IMocksControl control;
	private MessageRecordDao mockMessageRecordDao;
	private ReliablePush mockReliablePush;

	@Before
	public void init() {
		control = EasyMock.createControl();

		service = new MessageServiceImpl(mockReliablePush = control
				.createMock(ReliablePush.class));
		service.setMessageRecordDao(mockMessageRecordDao = control
				.createMock(MessageRecordDao.class));
	}

	@Test(expected = MessageNotFoundException.class)
	public void testModifyMessage因为找不到消息记录抛MessageNotFound()
			throws MessageNotFoundException {
		control.resetToStrict();

		// 录制messageRecordDao.getMessageRecordbyId
		Capture<String> messageIdCapture = new Capture<String>();
		mockMessageRecordDao.getMessageRecordbyId(capture(messageIdCapture));
		expectLastCall().andReturn(null);

		control.replay();

		String testMessageId = "testMessageId";

		try {
			invokeModifyMessage("appId", testMessageId);
		} catch (MessageNotFoundException e) {
			control.verify();
			assertEquals(testMessageId, messageIdCapture.getValue());
			throw e;
		} catch (MessageCanNotModifyException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = MessageNotFoundException.class)
	public void testModifyMessage因为不是消息的owner抛出MessageNotFound()
			throws MessageCanNotModifyException, MessageNotFoundException {
		control.resetToStrict();

		String appId = "appId";
		String messageId = "messageId";

		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				false, MessageStatus.ACKED.toString(), appId);

		control.replay();

		try {
			invokeModifyMessage(appId + "diff", messageId);
		} catch (MessageNotFoundException e) {
			control.verify();
			assertEquals(messageId, messageIdCapture.getValue());
			throw e;
		}

	}

	@Test(expected = MessageCanNotModifyException.class)
	public void testModifyMessage因为消息是广播抛出MessageCanNotModifyException()
			throws MessageCanNotModifyException {
		control.resetToStrict();
		String appId = "appId";
		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				true, null, appId);
		control.replay();

		String testMessageId = "testMessageId";

		try {
			invokeModifyMessage(appId, testMessageId);

		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		} catch (MessageCanNotModifyException e) {
			control.verify();
			assertEquals(testMessageId, messageIdCapture.getValue());
			assertTrue(e.isBroadcast());
			throw e;
		}
	}

	@Test(expected = MessageCanNotModifyException.class)
	public void testModifyMessage因为消息状态为DELETED抛出MessageCanNotModifyException()
			throws MessageCanNotModifyException {
		control.resetToStrict();
		String appId = "appId";
		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				false, MessageStatus.DELETED.toString(), appId);
		control.replay();

		String testMessageId = "testMessageId";
		try {
			invokeModifyMessage(appId, testMessageId);
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		} catch (MessageCanNotModifyException e) {
			control.verify();
			assertEquals(testMessageId, messageIdCapture.getValue());
			Assert.assertFalse(e.isBroadcast());
			assertEquals(MessageStatus.DELETED.toString(), e.getMessageStatus());
			throw e;
		}
	}

	@Test(expected = MessageCanNotModifyException.class)
	public void testModifyMessage因为消息状态为EXPIRED抛出MessageCanNotModifyException()
			throws MessageCanNotModifyException {

		control.resetToStrict();
		String appId = "appId";
		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				false, MessageStatus.EXPIRED.toString(), appId);
		control.replay();

		String testMessageId = "testMessageId";
		try {
			invokeModifyMessage(appId, testMessageId);
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		} catch (MessageCanNotModifyException e) {
			control.verify();
			assertEquals(testMessageId, messageIdCapture.getValue());
			Assert.assertFalse(e.isBroadcast());
			assertEquals(MessageStatus.EXPIRED.toString(), e.getMessageStatus());
			throw e;
		}

	}

	@Test(expected = MessageCanNotModifyException.class)
	public void testModifyMessage因为消息状态为ACKED抛出MessageCanNotModifyException()
			throws MessageCanNotModifyException {

		control.resetToStrict();
		String appId = "appId";
		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				false, MessageStatus.DELIVERING.toString(), appId);

		// 录制reliablePush.getMessageStatus
		mockReliablePush.getMessageStatus(capture(messageIdCapture));
		expectLastCall().andReturn(MessageStatus.ACKED.toString());

		control.replay();

		String testMessageId = "testMessageId";
		try {
			invokeModifyMessage(appId, testMessageId);
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		} catch (MessageCanNotModifyException e) {
			control.verify();
			assertEquals(testMessageId, messageIdCapture.getValues().get(0));
			assertEquals(testMessageId, messageIdCapture.getValues().get(1));
			Assert.assertFalse(e.isBroadcast());
			assertEquals(MessageStatus.ACKED.toString(), e.getMessageStatus());
			throw e;
		}

	}

	@Test(expected = MessageCanNotModifyException.class)
	public void testModifyMessage因为消息状态为APP_ACKED抛出MessageCanNotModifyException()
			throws MessageCanNotModifyException {

		control.resetToStrict();
		String appId = "appId";
		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				false, MessageStatus.DELIVERING.toString(), appId);

		// 录制reliablePush.getMessageStatus
		mockReliablePush.getMessageStatus(capture(messageIdCapture));
		expectLastCall().andReturn(MessageStatus.APP_ACKED.toString());

		control.replay();

		String testMessageId = "testMessageId";
		try {
			invokeModifyMessage(appId, testMessageId);
		} catch (MessageNotFoundException e) {
			e.printStackTrace();
		} catch (MessageCanNotModifyException e) {
			control.verify();
			assertEquals(testMessageId, messageIdCapture.getValues().get(0));
			assertEquals(testMessageId, messageIdCapture.getValues().get(1));
			Assert.assertFalse(e.isBroadcast());
			assertEquals(MessageStatus.APP_ACKED.toString(), e
					.getMessageStatus());
			throw e;
		}
	}

	@Test
	public void testModifyMessage正常更新且消息状态为DELAYING()
			throws MessageNotFoundException, MessageCanNotModifyException {
		control.resetToStrict();
		String appId = "appId";
		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				false, MessageStatus.DELAYING.toString(), appId);

		// 录制messageRecordDao.modifyMessageRecordContent
		Capture<String> dataTypeCapture = new Capture<String>();
		Capture<byte[]> contentCapture = new Capture<byte[]>();

		mockMessageRecordDao.modifyMessageRecordContent(
				capture(messageIdCapture), capture(dataTypeCapture),
				capture(contentCapture));

		control.replay();

		String testMessageId = "testMessageId";
		byte[] updateMessageContent = "updatedMessageContent".getBytes();
		String updateMessageType = "updateMessageType";

		service.modifyMessage(updateMessageContent, appId, testMessageId,
				updateMessageType);

		control.verify();
		assertEquals(testMessageId, messageIdCapture.getValues().get(0));
		assertEquals(testMessageId, messageIdCapture.getValues().get(1));
		assertEquals(updateMessageType, dataTypeCapture.getValue());
		assertEquals(updateMessageContent, contentCapture.getValue());
	}

	@Test
	public void testModifyMessage正常更新且消息状态为DELIVERING()
			throws MessageNotFoundException, MessageCanNotModifyException {
		control.resetToStrict();
		String appId = "appId";
		Capture<String> messageIdCapture = mockMessageRecordDao的getMessageRecordbyId方法(
				false, MessageStatus.DELIVERING.toString(), appId);
		// 录制MessageRecordDao.modifyMessageRecordContent
		Capture<String> dataTypeCapture = new Capture<String>(CaptureType.ALL);
		Capture<byte[]> contentCapture = new Capture<byte[]>(CaptureType.ALL);

		// 录制ReliablePush.getMessageStatus
		mockReliablePush.getMessageStatus(capture(messageIdCapture));
		expectLastCall().andReturn(MessageStatus.DELIVERING.toString());

		// 录制ReliablePush.updateMessageContentById
		mockReliablePush.updateMessageContentById(capture(messageIdCapture),
				capture(contentCapture));

		mockMessageRecordDao.modifyMessageRecordContent(
				capture(messageIdCapture), capture(dataTypeCapture),
				capture(contentCapture));

		control.replay();

		String testMessageId = "testMessageId";
		byte[] updateMessageContent = "updatedMessageContent".getBytes();
		String updateMessageType = "updateMessageType";

		service.modifyMessage(updateMessageContent, appId, testMessageId,
				updateMessageType);

		control.verify();
		assertEquals(testMessageId, messageIdCapture.getValues().get(0));
		assertEquals(testMessageId, messageIdCapture.getValues().get(1));
		assertEquals(updateMessageType, dataTypeCapture.getValue());
		assertEquals(updateMessageContent, contentCapture.getValues().get(0));
		assertEquals(updateMessageContent, contentCapture.getValues().get(1));
	}

	/**
	 * 调用modifyMessage
	 * 
	 * @param appId
	 * @param messageId
	 * @throws MessageNotFoundException
	 * @throws MessageCanNotModifyException
	 */
	private void invokeModifyMessage(String appId, String messageId)
			throws MessageNotFoundException, MessageCanNotModifyException {
		byte[] updateMessageContent = "updatedMessageContent".getBytes();
		String updateMessageType = "updateMessageType";

		service.modifyMessage(updateMessageContent, appId, messageId,
				updateMessageType);

	}

	/**
	 * 录制MessageRecordDao.getMessageRecordbyId方法
	 * 
	 * @param isbroadCast
	 * @param messageStatus
	 * @return
	 */
	private Capture<String> mockMessageRecordDao的getMessageRecordbyId方法(
			boolean isbroadCast, String messageStatus, String appId) {
		Capture<String> messageIdCapture = new Capture<String>(CaptureType.ALL);
		mockMessageRecordDao.getMessageRecordbyId(capture(messageIdCapture));

		MessageRecord messageRecord = new MessageRecord();
		messageRecord.setBroadcastFlag(isbroadCast);
		messageRecord.setStatus(messageStatus);
		messageRecord.setAppId(appId);
		expectLastCall().andReturn(messageRecord);

		return messageIdCapture;
	}
}
