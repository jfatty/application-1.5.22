package com.babeeta.butterfly.application.app.record.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.app.MessageContext;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;

public interface MessageRecordDao {
	public boolean saveMessageRecord(MessageContext mtx);
	
	public boolean saveMessageRecord(MessageRecord messageRecord);

	public boolean updateDelivering(String messageId);

	public void modifyMessageRecordContent(String messageId, String dataType,
			byte[] content);

	public MessageRecord getMessageRecordbyId(String messageId);

	/***
	 * 修改收件人
	 * 
	 * @param oldCid
	 * @param newCid
	 */
	public void updateRecipient(String oldCid, String newCid, String aid);

	/****
	 * 更新状态
	 * 
	 * @param uid
	 * @param status
	 */
	public void updateStatus(String uid, String status);

	/***
	 * 是否为广播
	 * 
	 * @param uid
	 * @return
	 */
	public boolean isBroadCast(String uid);

	/***
	 * 判断是否存在
	 * 
	 * @param messageId
	 * @return
	 */
	public boolean existsMessageId(String messageId);
	
	/**
	 * @param status
	 * @return
	 */
	public List<MessageRecord> findMessageRecordByDelayingExecBy(String delayingExecBy);
	
	
	/**
	 * @param maxDelayUtil
	 * @param modifyToDelayingExecBy
	 * @return
	 */
	public MessageRecord findAndModifyDelayingMsg(Date maxDelayUtil,String modifyToDelayingExecBy);
	
	/**
	 * @param queryDelayExecby
	 */
	public void unsetDelayingExecBy(String queryDelayExecby);
}