package com.babeeta.butterfly.application.app.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.app.record.dao.MessageRecordDao;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.service.DelayMessageTask;
import com.babeeta.butterfly.application.app.service.DelayMessageTaskService;
import com.babeeta.butterfly.application.app.service.NamedService;

public class DelayMessageTaskServiceImpl implements DelayMessageTaskService {
	private final static Logger logger = LoggerFactory
			.getLogger(DelayMessageTaskServiceImpl.class);
	private static final DelayMessageTaskServiceImpl defaultInstance = new DelayMessageTaskServiceImpl();

	private final DelayQueue<DelayMessageTask> SCHEDULED_TASK_QUEUE = new DelayQueue<DelayMessageTask>();
	private final Map<String, DelayMessageTask> scheduleMessageTaskMap = new HashMap<String, DelayMessageTask>();
	private NamedService namedService;
	private MessageRecordDao messageRecordDao;
	private int capacity = 10000;
	private int maxInQueueTimeDistance = 3;

	@Override
	public void loadTaskFromDbGradually() {
		logger.debug("load task from db start");
		
		if (isOutofCapacity()) {
			return;
		}
		
		int loadNum = capacity - SCHEDULED_TASK_QUEUE.size();

		Date maxDelayUtil = DateUtils.addMinutes(new Date(),
				maxInQueueTimeDistance);
		
		for (int i = 0; i < loadNum; i++) {
			if (isOutofCapacity()) {
				return;
			}

			MessageRecord messageRecord = messageRecordDao
					.findAndModifyDelayingMsg(maxDelayUtil,
							namedService.getAppName());

			if (messageRecord == null) {
				logger.info("no delaying message load from db.");
				break;
			}

			DelayMessageTask delayMessageTask = new DelayMessageTask(
					messageRecord.getMessageId(), messageRecord.getDelay());
			addTask(delayMessageTask);
		}
		
		logger.debug("load task from db finished");
	}

	private boolean isOutofCapacity() {
		if (SCHEDULED_TASK_QUEUE.size() >= capacity) {
			logger.warn("out of capacity {}", SCHEDULED_TASK_QUEUE.size());
			return true;
		}
		return false;
	}

	@Override
	public boolean setupTask(DelayMessageTask task) {
		if (task.getDelayValue() > maxInQueueTimeDistance) {
			logger.debug(
					"{} delay value great than maxInQueueTimeDistance,so ignore.",
					task.getUid());
			return false;
		}

		return addTask(task);
	}

	private boolean addTask(DelayMessageTask task) {
		if (isOutofCapacity()) {
			return false;
		}

		scheduleMessageTaskMap.put(task.getUid(), task);
		SCHEDULED_TASK_QUEUE.put(task);

		logger.debug("add task {} success,task queue size increase to {}",
				task.getUid(), SCHEDULED_TASK_QUEUE.size());
		return true;
	}

	@Override
	public void removeTask(String messageId) {
		logger.debug("remove task {} success,task queue size {}", messageId,
				SCHEDULED_TASK_QUEUE.size());
		DelayMessageTask scheduledMessageTask = scheduleMessageTaskMap
				.remove(messageId);
		SCHEDULED_TASK_QUEUE.remove(scheduledMessageTask);
		logger.debug("remove task {} success,task queue size {}", messageId,
				SCHEDULED_TASK_QUEUE.size());
	}

	@Override
	public DelayMessageTask getTimeToExecuteTask() {
		logger.debug("[ScheduledTaskService] getTimeoutTask,size ={}",
				SCHEDULED_TASK_QUEUE.size());
		DelayMessageTask task = SCHEDULED_TASK_QUEUE.poll();

		if (task == null) {
			logger.debug("No timeout task!");
		} else {
			scheduleMessageTaskMap.remove(task.getUid());
			logger.info("Message {} delay timeout!", task.getUid());
		}
		return task;
	}

	public void setMessageRecordDao(MessageRecordDao messageRecordDao) {
		this.messageRecordDao = messageRecordDao;
	}

	public void setNamedService(NamedService namedService) {
		this.namedService = namedService;
	}

	@Override
	public void loadUnfinishedTaskFromDb() {
		List<MessageRecord> messageRecordList = messageRecordDao
				.findMessageRecordByDelayingExecBy(namedService.getAppName());

		if (messageRecordList == null || messageRecordList.size() == 0) {
			logger.info("no unfinished delaying message in db");
			return;
		}

		for (MessageRecord messageRecord : messageRecordList) {

			DelayMessageTask task = new DelayMessageTask(
					messageRecord.getMessageId(), messageRecord.getDelay());
			addTask(task);
		}

		logger.info("load {} unfinished task from db success.",
				messageRecordList.size());
	}

	@Override
	public void releaseDelayingMessage() {
		messageRecordDao.unsetDelayingExecBy(namedService.getAppName());
		logger.info("{} release delay task success.",namedService.getAppName());
	}

}
