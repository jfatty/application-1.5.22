package com.babeeta.butterfly.application.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.app.record.dao.MessageRecordDao;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.service.DelayMessageTaskService;
import com.babeeta.butterfly.application.app.service.MessageCanNotModifyException;
import com.babeeta.butterfly.application.app.service.MessageNotFoundException;
import com.babeeta.butterfly.application.app.service.MessageService;
import com.babeeta.butterfly.application.reliable.MessageStatus;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.babeeta.butterfly.application.reliable.ReliablePushImpl;

public class MessageServiceImpl implements MessageService {

	private DelayMessageTaskService delayMessageTaskService;
	private MessageRecordDao messageRecordDao;
	private ReliablePush reliablePush;
	private static final String SPACE_CHAR = " ";
	private static final String SLASH_CHAR = "/";
	private static final Logger log = LoggerFactory
			.getLogger(MessageServiceImpl.class);

	public MessageServiceImpl() {
		reliablePush = ReliablePushImpl.getDefaultInstance();
	}

	public MessageServiceImpl(ReliablePush reliablePush) {
		this.reliablePush = reliablePush;
	}

	@Override
	public void deleteMessage(String appId, String messageId)
			throws MessageNotFoundException, MessageCanNotModifyException {
		MessageRecord messageRecord = messageRecordDao
				.getMessageRecordbyId(messageId);

		checkMessageModifiable(appId, messageId, messageRecord);

		if (MessageStatus.DELAYING.toString().equals(messageRecord.getStatus())) {
			delayMessageTaskService.removeTask(messageId);
		}

		if (MessageStatus.DELIVERING.toString().equals(
				messageRecord.getStatus())) {
			reliablePush.deleteMessageById(messageRecord.getMessageId());
		}

		messageRecordDao.updateStatus(messageRecord.getMessageId(),
				MessageStatus.DELETED.toString());
	}

	@Override
	public void modifyMessage(byte[] content, String appId, String messageId,
			String type)
			throws MessageNotFoundException, MessageCanNotModifyException {

		MessageRecord messageRecord = messageRecordDao
				.getMessageRecordbyId(messageId);

		checkMessageModifiable(appId, messageId, messageRecord);

		if (MessageStatus.DELIVERING.toString().equals(
				messageRecord.getStatus())) {
			reliablePush.updateMessageContentById(messageId, content);
		}

		messageRecordDao.modifyMessageRecordContent(messageId, type, content);
	}

	private void checkMessageModifiable(String appId, String messageId,
			MessageRecord messageRecord) throws MessageNotFoundException,
			MessageCanNotModifyException {

		if (messageRecord == null) {
			throw new MessageNotFoundException(messageId);
		}
		if (!isMsgOwner(appId, messageRecord)) {
			log.error("shit,{} is not the owner of message {}",appId,messageId);
			throw new MessageNotFoundException(messageId);
		}

		if (messageRecord.getBroadcastFlag()) {
			throw new MessageCanNotModifyException(true,
					messageRecord.getStatus());
		}

		if (MessageStatus.DELETED.toString().equals(messageRecord.getStatus())
				|| MessageStatus.EXPIRED.toString().equals(
						messageRecord.getStatus())) {
			throw new MessageCanNotModifyException(false,
					messageRecord.getStatus());
		} else if (MessageStatus.DELAYING.toString().equals(
				messageRecord.getStatus())) {
			return;
		} else {
			// messageRecord中的消息状态在消息开始投递(变成DELIVERING)之后就不会再变化，所以消息最准确的状态需要从reliable_push中查
			String messageStatus = reliablePush.getMessageStatus(messageId);

			// 非DELIVERING的其他状态（ACKED,APP_ACKED,EXPIRED,EXPIRED_ACKED）都不可以修改
			if (!MessageStatus.DELIVERING.toString().equals(messageStatus)) {
				throw new MessageCanNotModifyException(false, messageStatus);
			}
		}
	}

	@Override
	public String getMessageStatus(String appId, String messageId)
			throws MessageNotFoundException {
		MessageRecord messageRecord = messageRecordDao
				.getMessageRecordbyId(messageId);

		if (messageRecord == null
				|| MessageStatus.DELETED.toString().equals(
						messageRecord.getStatus())) {
			throw new MessageNotFoundException(messageId);
		}

		if (!isMsgOwner(appId, messageRecord)) {
			log.error("shit,{} is not the owner of message {}", appId, messageId);
			throw new MessageNotFoundException(messageId);
		}

		if (MessageStatus.DELAYING.toString().equals(messageRecord.getStatus())) {
			return MessageStatus.DELAYING.toString();
		}

		Map result = reliablePush.queryAndModifyExpiredReliablePush(messageId);

		if (result == null) {
			throw new MessageNotFoundException(messageId);
		}

		String status = String.valueOf(result.get(ReliablePush.FIELD_STATUS));

		if (messageRecord.getBroadcastFlag()) {
			int appAckedCount = result
					.get(ReliablePush.FIELD_APP_ACKED_COUNT) == null ? 0
					: Integer.valueOf(result.get(
							ReliablePush.FIELD_APP_ACKED_COUNT).toString());

			int ackedCount = result.get(ReliablePush.FIELD_ACKED_COUNT) == null ? 0
					: Integer.valueOf(result
							.get(ReliablePush.FIELD_ACKED_COUNT).toString());

			ackedCount = ackedCount - appAckedCount;
			if (ackedCount < 0) {
				ackedCount = 0;
			}

			int totalSubMessage = result
					.get(ReliablePush.FIELD_TOTAL_SUB_MESSAGE) == null ? 0
					: Integer.valueOf(result.get(
							ReliablePush.FIELD_TOTAL_SUB_MESSAGE).toString());

			String ackedInfo = appAckedCount + SLASH_CHAR + ackedCount
					+ SLASH_CHAR
					+ totalSubMessage;

			if (MessageStatus.DELIVERING.toString().equals(status)) {

				return MessageStatus.DELIVERING.toString() + SPACE_CHAR
						+ ackedInfo;

			} else if (MessageStatus.EXPIRED.toString().equals(status)
					|| MessageStatus.EXPIRED_NOTIFIED.toString().equals(status)) {

				return MessageStatus.EXPIRED.toString() + SPACE_CHAR
						+ ackedInfo;
			} else {
				return status;
			}
		}

		if (result.get(ReliablePush.FIELD_ACKED_AT) != null) {
			return String.valueOf(result.get(ReliablePush.FIELD_STATUS))
					+ SPACE_CHAR
					+ result.get(ReliablePush.FIELD_ACKED_AT).toString();

		} else if (MessageStatus.EXPIRED_NOTIFIED.toString().equals(status)) {

			return MessageStatus.EXPIRED.toString();

		} else {
			return String.valueOf(result.get(ReliablePush.FIELD_STATUS));
		}
	}

	private boolean isMsgOwner(String appId, MessageRecord messageRecord) {
		if (StringUtils.isBlank(appId)) {
			log.error("appId is null");
			return false;
		}

		if (StringUtils.isBlank(messageRecord.getAppId())) {
			log.error("messageRecord miss required property appId");
			return false;
		}

		return messageRecord.getAppId().equals(appId);
	}

	public void setDelayMessageTaskService(
			DelayMessageTaskService delayMessageTaskService) {
		this.delayMessageTaskService = delayMessageTaskService;
	}

	public void setMessageRecordDao(MessageRecordDao messageRecordDao) {
		this.messageRecordDao = messageRecordDao;
	}
}
