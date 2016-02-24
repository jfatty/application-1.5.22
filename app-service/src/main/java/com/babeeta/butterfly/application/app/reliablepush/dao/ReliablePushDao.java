package com.babeeta.butterfly.application.app.reliablepush.dao;

import java.util.List;

import com.babeeta.butterfly.application.app.reliablepush.entity.ReliablePushBean;

/***
 * 
 * @author zeyong.xia
 * @date 2011-9-19
 */
public interface ReliablePushDao {

	/***
	 * 查询未ack的消息id
	 * @param aid
	 * @param cid
	 * @return
	 */
	public List<ReliablePushBean> queryNotAppAckMessageId(String aid,String cid);
	
	/***
	 * 锁定消息状态
	 */
	public void updateStatus(List<ReliablePushBean> list);
	
	/***
	 * 重新设置消息状态
	 */
	public void updateStatusToPush(List<ReliablePushBean> list);
	
	/***
	 * 修改cid
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 */
	public void updateCid(String oldCid,String newCid,String aid);
	
	/***
	 * 更新消息内容
	 * @param content
	 * @param parentId
	 */
	public boolean updateMessage(byte[] content,String parentId);
	
	/***
	 * 将未投递成功的消息置为删除状态
	 * @param parentId
	 */
	public boolean  deleteMessage(String parentId);
	
	/***
	 * 变更目的地
	 * @param aid
	 * @param oldCid
	 * @param newCid
	 */
	public void updateRecipient(String aid,String oldCid,String newCid);
	
	/***
	 * 查询消息
	 * @param uid
	 * @return
	 */
	public String queryByParentId(String uid);
	
}
