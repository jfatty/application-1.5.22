package com.babeeta.butterfly.application.management.dao.impl;

import java.util.List;

import com.babeeta.butterfly.application.management.dao.MessageDao;
import com.babeeta.butterfly.application.management.dao.MorphiaDataStore;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

public class MessageDaoImpl extends BaseDao<GroupMessage, String> implements
		MessageDao<GroupMessage, String> {

	public MessageDaoImpl(MorphiaDataStore ds) {
		super(GroupMessage.class, ds);
		// TODO Auto-generated constructor stub
	}
	/***
	 * 查询某用户消息列表
	 * @param offset
	 * @param limit
	 * @param email
	 * @return
	 */
	public List<GroupMessage> queryMyMessageList(int offset,int limit,String email)
	{
		Query<GroupMessage> query=this.ds.createQuery(GroupMessage.class).offset(offset)
									.limit(limit).filter("userEmail", email).order("createAt");
		return query.asList();
	}
	
	/***
	 * 查询未审核消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryCheckMessageList(int offset,int limit)
	{
		Query<GroupMessage> query=this.ds.createQuery(GroupMessage.class).offset(offset)
		.limit(limit).filter("check", 0).order("createAt");
		return query.asList();
	}
	
	/***
	 * 更新消息
	 * @param message
	 * @return
	 */
	public GroupMessage updateMessage(GroupMessage message)
	{
		Query<GroupMessage> query=this.createQuery().filter("_id", message.getId());
		UpdateOperations<GroupMessage> ops=this.createUpdateOperations();
		ops.set("check", message.getCheck());
		ops.set("messageContent", message.getMessageContent());
		ops.set("messageAge", message.getMessageAge());
		ops.set("messageFormat", message.getMessageFormat());
		this.ds.update(query, ops);
		return message;
	}
	
	/***
	 * 查询未审核的message总数
	 * @return
	 */
	public int queryNotCheckMessageCount()
	{
		Query<GroupMessage> query=this.createQuery().filter("check", 0);
		if(query!=null)
		{
			return (int)query.countAll();
		}
		return 0;
	}
}
