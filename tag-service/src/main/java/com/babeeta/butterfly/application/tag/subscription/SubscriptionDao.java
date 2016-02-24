package com.babeeta.butterfly.application.tag.subscription;

/***
 * 
 * @author zeyong.xia
 * @date 2011-9-19
 */
public interface SubscriptionDao {

	
	/***
	 * 判断是否存在
	 * @param aid
	 * @param cid
	 * @return
	 */
	boolean exists(String aid, String cid);
}
