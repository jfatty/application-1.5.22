package com.babeeta.butterfly.application.management.dao;

import java.io.Serializable;
import java.util.List;

import com.babeeta.butterfly.application.management.entity.GroupMessage;

public interface MessageDao<T, K extends Serializable> extends IDao<T, K>  {

	/***
	 * 查询某用户消息列表
	 * @param offset
	 * @param limit
	 * @param email
	 * @return
	 */
	public List<GroupMessage> queryMyMessageList(int offset,int limit,String email);
	
	/***
	 * 查询未审核消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryCheckMessageList(int offset,int limit);
	
	/***
	 * 更新消息
	 * @param message
	 * @return
	 */
	public GroupMessage updateMessage(GroupMessage message);
	
	/***
	 * 查询未审核的message总数
	 * @return
	 */
	public int queryNotCheckMessageCount();
	
	
}
