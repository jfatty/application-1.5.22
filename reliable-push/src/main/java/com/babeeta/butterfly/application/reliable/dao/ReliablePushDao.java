package com.babeeta.butterfly.application.reliable.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReliablePushDao {

	/**
	 * @param id
	 * @param content
	 */
	public void updateMessageContentById(String id, byte[] content);

	/**
	 * @param id
	 */
	public void deleteMessageById(String id);

	/**
	 * @param maxExpiredTime
	 * @param expiredAt
	 */
	public void updateDeliveringMsgStatusToExpired(Date maxExpiredTime,
			Date expiredAt);

	/**
	 * @param appId
	 * @return
	 */
	public List<String> getExpiredMessageId(String appId);

	/**
	 * @param messageIdList
	 * @param status
	 */
	public void updateMessageStatus(List<String> messageIdList, String status);

	/**
	 * @param messageId
	 * @param ackedTime
	 */
	public void updateAckedWhenDelivering(String messageId, Date ackedTime);

	/**
	 * @param messageId
	 * @param ackedTime
	 */
	public void updateAppAcked(String messageId, Date ackedTime);

	/**
	 * @param id
	 * @return
	 */
	public Map findById(String id);

	/**
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 */
	public void updateCidWhenDelayingOrDelivering(String oldCid, String newCid,
			String aid);

	/**
	 * @param statusQueryField
	 * @param modifyExpiredNotifyBy
	 * @return
	 */
	public Map findAndModifyExpriedMsg(String appIdQueryField,
			String modifyExpiredNotifyBy);

	/**
	 * @param maxExpiredQueryField
	 * @param modifyExpriedNotifyBy
	 * @return
	 */
	public Map findAndModifyDeliveringMsg(String appIdQueryField, Date maxExpiredQueryField,
			String modifyExpriedNotifyBy);
	
	
	/**
	 * @param expiredNotifyByQueryField
	 */
	public void unsetExpiredNotifyBy(String expiredNotifyByQueryField);
	
	/**
	 * @param id
	 * @param increaseCount
	 */
	public void increaseAckedCount(String id,int increaseCount);
	
	/**
	 * @param id
	 * @param increaseCount
	 */
	public void increaseAppAckedCount(String id,int increaseCount);
}