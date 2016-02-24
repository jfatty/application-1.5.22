package com.babeeta.butterfly.application.app.service;

import java.util.List;

import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.reliablepush.entity.ReliablePushBean;

/***
 * 变更收件人
 * @author zeyong.xia
 * @date 2011-9-19
 */
public interface ChangeRecipientService {

	
	
	/***
	 * 修改收件人
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 */
	public void updateMessageRecordRecipient(String oldCid,String newCid,String aid);
	
	/***
	 * 修改收件人
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 */
	public void updateReliablePushCid(String oldCid,String newCid,String aid);
	
	
	/****
	 * 判断cid是否存在
	 * @param aid
	 * @param cid
	 * @return
	 */
	public boolean existCid(String aid,String cid);
	
	
	/***
	 * 判断tag是否存在
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid,String tagName);
	
}
