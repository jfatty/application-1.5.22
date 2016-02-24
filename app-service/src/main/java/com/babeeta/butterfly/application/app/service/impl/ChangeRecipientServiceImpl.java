package com.babeeta.butterfly.application.app.service.impl;

import com.babeeta.butterfly.application.app.record.dao.MessageRecordDao;
import com.babeeta.butterfly.application.app.service.ChangeRecipientService;
import com.babeeta.butterfly.application.app.subscription.dao.SubscriptionDao;
import com.babeeta.butterfly.application.app.tag.TagService;
import com.babeeta.butterfly.application.reliable.dao.ReliablePushDao;
import com.babeeta.butterfly.application.reliable.dao.ReliablePushDaoImpl;

@SuppressWarnings("deprecation")
public class ChangeRecipientServiceImpl implements ChangeRecipientService {

	private SubscriptionDao subscriptionDaoImpl;
	
	private MessageRecordDao messageRecordDaoImpl;
							
	private ReliablePushDao reliablePushDao;
	
	private TagService tagService;
	
	public ChangeRecipientServiceImpl(){
		reliablePushDao=ReliablePushDaoImpl.getDefaultInstance();
	}
	
	/***
	 * 修改收件人
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 */
	public void updateMessageRecordRecipient(String oldCid,String newCid,String aid)
	{
		this.messageRecordDaoImpl.updateRecipient(oldCid, newCid, aid);
	}
	
	/***
	 * 修改收件人
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 */
	public void updateReliablePushCid(String oldCid,String newCid,String aid)
	{
		reliablePushDao.updateCidWhenDelayingOrDelivering(oldCid, newCid, aid);
	}
	
	
	/****
	 * 判断cid是否存在
	 * @param aid
	 * @param cid
	 * @return
	 */
	public boolean existCid(String aid,String cid)
	{
		return this.subscriptionDaoImpl.exists(aid, cid);
	}
	
	/***
	 * 判断tag是否存在
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid,String tagName)
	{
		if("all".equals(tagName)){
			return true;
		}
		return this.tagService.existsTag(aid, tagName);
	}
	
	
	public void setSubscriptionDaoImpl(SubscriptionDao subscriptionDaoImpl) {
		this.subscriptionDaoImpl = subscriptionDaoImpl;
	}

	public void setMessageRecordDaoImpl(MessageRecordDao messageRecordDaoImpl) {
		this.messageRecordDaoImpl = messageRecordDaoImpl;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

}
