package com.babeeta.butterfly.application.reliable;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.MessageRouting;

/**
 * Created by IntelliJ IDEA. User: XYuser Date: 11-1-22 Time: 下午1:58 To change
 * this template use File | Settings | File Templates.
 */
public interface ReliablePush {
	public static final String FIELD_ID = "_id";
	public static final String FIELD_MESSAGE = "message";
	public static final String FIELD_STATUS = "status";
	public static final String FIELD_EXPIRED_TIME = "expiredTime";
	public static final String FIELD_CREATED_AT = "createdAt";
	public static final String FIELD_ACKED_AT = "ackedAt";
	public static final String FIELD_EXPIRED_AT = "expiredAt";
	public static final String FIELD_PARENT_ID = "parentId";
	public static final String FIELD_BROADCAST = "broadcast";
	public static final String FIELD_TOTAL_SUB_MESSAGE = "totalSubMessage";
	public static final String FIELD_APP_ACKED_COUNT= "appAckedCount";
	public static final String FIELD_ACKED_COUNT= "ackedCount";
	public static final String FIELD_KEY= "key";
	public static final String FIELD_EXPRIED_NOTIFY_BY= "expiredNotifyBy";
	

	/**
	 * 根据设备ID获取未成功投递的消息集合
	 * 
	 * @param did
	 *            设备ID
	 * @return 未成功投递的消息集合
	 */
	List<MessageRouting.Message> getMessagesList(String did);
	
	/**
	 * @param aid
	 * @param tag
	 * @param cid
	 * @return
	 */
	List<MessageRouting.Message> getDeliveringBroadcastMessageList(String aid,String tag,String cid);

	/**
	 * 根据消息ID获取消息的投递状态
	 * 
	 * @param uid
	 *            消息ID
	 * @return 消息状态
	 */
	String getMessageStatus(String uid);

	/**
	 * 存储push消息
	 * 
	 * @param uid
	 *            消息的ID
	 * @param aid
	 *            applicationID
	 * @param cid
	 *            clientID
	 * @param message
	 *            消息内容
	 * @return 是否成功 true=成功，false=失败
	 */
	boolean saveMessage(MessageRouting.Message message, String aid, String cid);

	/**
	 * 根据消息ID更改消息的状态
	 * 
	 * @param uid
	 *            消息的ID
	 * @param cid          
	 * @return 是否成功 true=成功，false=失败
	 */
	boolean updateAckWhenDelivering(String uid,String cid);

	/**
	 * 更改消息状态至过期（expired）
	 * 
	 * @param uid
	 *            消息的ID
	 * @return
	 */
	void updateExpire(String uid);

	/**
	 * @param id
	 * @param content
	 */
	void updateMessageContentById(String id, byte[] content);

	/**
	 * @param id
	 */
	void deleteMessageById(String id);

	void updateAppAck(String uid);

	/**
	 * 查询reliable_push记录,如果消息已经到了过期时间,将消息标记为已过期
	 * 
	 * @param uid
	 * @return
	 */
	Map queryAndModifyExpiredReliablePush(String uid);
	
	/**
	 * @param message
	 * @param aid
	 * @param cid
	 * @param totalSubMessage
	 * @return
	 */
	public boolean saveMessage(MessageRouting.Message message, String aid,
			String cid,int totalSubMessage);
}
