package com.babeeta.butterfly.application.app.service;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.app.MessageContext;
import com.babeeta.butterfly.application.app.pusher.MessagePusher;
import com.babeeta.butterfly.application.app.record.MessageRecordService;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.reliable.MessageStatus;

/****
 * 
 * @update zeyong.xia  add update executeAt
 * @date 2011-9-21
 */
public class DelayMessageTask implements Runnable, Delayed {
	private final static Logger logger = LoggerFactory
			.getLogger(DelayMessageTask.class);
	private String uid;
	private long createAt;
	private long executeAt;
	private int delayTimeInMinute;

	public DelayMessageTask(String uid, int delayTimeInMinute) {
		this.delayTimeInMinute=delayTimeInMinute;
		this.uid = uid;
		createAt = new Date().getTime();
		executeAt = createAt + delayTimeInMinute * 60*1000;
	}

	public String getUid() {
		return uid;
	}
	
	public int getDelayValue(){
		return delayTimeInMinute;
	}

	@Override
	public int compareTo(Delayed o) {
		DelayMessageTask that = (DelayMessageTask) o;
		return this.executeAt > that.executeAt ? 1
				: (this.executeAt < that.executeAt ? -1 : 0);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(executeAt - new Date().getTime(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		logger.debug("start task,messageId is {}",uid);
		MessageRecord messageRecord = MessageRecordService.getDefaultInstance()
				.getDao().getMessageRecordbyId(uid);

		if (messageRecord != null) {
			messageRecord.setDelay(0);

			if (messageRecord.getStatus().equals("DELAYING")) {
				MessageRecordService.getDefaultInstance()
				.getDao().updateStatus(uid, MessageStatus.DELIVERING.toString());
				
				if (messageRecord.getBroadcastFlag()) {
					MessagePusher.getDefaultInstance().broadcast(messageRecord);
				} else {
					MessagePusher.getDefaultInstance().unicast(
							messageRecord.getRecipient(), messageRecord);
				}
			} else {
				logger.info("Delay message {} is in status {}.", uid,
						messageRecord.getStatus());
			}
		} else {
			logger.info("Not found delay message {}.", uid);
		}
	}
	
	///////zeyong.xia  add
	public void setExecuteAt(long delay)
	{
		this.executeAt+=delay;
	}
	
	public long getExecuteAt()
	{
		return this.executeAt;
	}
}
