package com.babeeta.butterfly.application.app.pusher;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.app.record.MessageRecordService;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.tag.TagService;
import com.babeeta.butterfly.application.app.tag.TagServiceImpl;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.babeeta.butterfly.application.reliable.ReliablePushImpl;
import com.babeeta.butterfly.router.network.MessageSenderImpl;
import com.google.protobuf.ByteString;

public class MessagePusher {
	private final static Logger logger = LoggerFactory
			.getLogger(MessagePusher.class);
	private static final MessageSender MESSAGE_SENDER = new MessageSenderImpl();
	private static final MessagePusher defaultInstance = new MessagePusher();

	private static final TagService tagService = new TagServiceImpl();

	public final ThreadPoolExecutor executor = new ThreadPoolExecutor(32, 64,
			5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000));

	public static MessagePusher getDefaultInstance() {
		return defaultInstance;
	}

	private MessageRouting.Message getMessageFromMessageContext(
			MessageRecord messageRecord, String clientId) {
		MessageRouting.Message.Builder builder = MessageRouting.Message
				.newBuilder().setDate(System.currentTimeMillis())
				.setExpire(messageRecord.getExpire())
				.setFrom(messageRecord.getAppId()+"."+messageRecord.getRecipient() + "@" + "app")
				.setUid(messageRecord.getMessageId()).setParentId(messageRecord.getParentId()).setBroadcast(true);

		MessageRouting.Message msg = null;

		builder.setContent(ByteString.copyFrom(messageRecord.getContent()));

		builder.setTo(new StringBuilder(clientId).append(".")
				.append(messageRecord.getAppId()).append("@dev").toString());

		msg = builder.build();

		return msg;
	}

	public void unicast(String clientId, MessageRecord mesageRecord) {
		if("DELAYING".equals(mesageRecord.getStatus())){
			MessageRecordService.getDefaultInstance().getDao()
			.updateDelivering(mesageRecord.getMessageId());
		}

		MessageRouting.Message.Builder builder = MessageRouting.Message
				.newBuilder().setDate(System.currentTimeMillis())
				.setExpire(mesageRecord.getExpire())
				.setFrom(mesageRecord.getAppId() + "@" + "app")
				.setUid(mesageRecord.getMessageId()).setBroadcast(false).setParentId(mesageRecord.getParentId());

		MessageRouting.Message msg = null;

		builder.setContent(ByteString.copyFrom(mesageRecord.getContent()));

		builder.setTo(new StringBuilder(clientId).append(".")
				.append(mesageRecord.getAppId()).append("@dev").toString());

		msg = builder.build();
		ReliablePush reliablePush = ReliablePushImpl.getDefaultInstance();
		boolean saveResult = reliablePush.saveMessage(msg, mesageRecord.getAppId(),
				clientId);
		logger.debug(
				"[{}] ReliablePush[{}] [{}]",
				new Object[] {
						mesageRecord.getMessageId(),
						saveResult,
						new StringBuilder(mesageRecord.getAppId()).append(".")
								.append(clientId).toString() });
		if (!saveResult) {
			logger.warn("[{}]failed to persistence.", mesageRecord.getMessageId());
		}

		MESSAGE_SENDER.send(msg);
	}

	private final UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("[broadcast]Uncaught exception: ", e);
		}
	};

	public void broadcast(final MessageRecord parent) {
		try {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setName("Broadcast");
					Thread.currentThread().setUncaughtExceptionHandler(
							uncaughtExceptionHandler);
					
					logger.debug("[MessagePusher] broadcast group is {}", parent.getRecipient());
					int broadcastCount=tagService.tagCount(parent.getAppId(), parent.getRecipient());
					
					
					Message msg=getMessageFromMessageContext(parent,parent.getRecipient());

					ReliablePushImpl.getDefaultInstance().saveMessage(msg, parent.getAppId(),parent.getRecipient(), broadcastCount);

					logger.info(
							"[broadcast] message [{}] will be send to [{}] clients.",
							parent.getMessageId(), broadcastCount);
					MessageRecordService
							.getDefaultInstance()
							.getDao()
							.updateDelivering(parent.getMessageId());
					MESSAGE_SENDER.send(msg);
				}
			});
		} catch (RejectedExecutionException e) {
			logger.error(
					"[broadcast]Broadcast message failed in start new thread.",
					e);
		}
	}
}
