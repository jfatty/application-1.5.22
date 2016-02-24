package com.babeeta.butterfly.application.app.subscription.dao;

/***
 * 
 * @author zeyong.xia
 * @date 2011-9-19
 */
public interface SubscriptionDao {

	/***
	 * 通过aid,cid查询did
	 * @param aid
	 * @param cid
	 * @return
	 */
	public String querydid(String aid,String cid);
	
	/***
	 * 修改三者关系
	 * @param aid
	 * @param cid
	 * @param did
	 * @return
	 */
	public boolean updateRelationship(String aid,String cid,String did);
	
	/***
	 * 判断是否存在
	 * @param aid
	 * @param cid
	 * @return
	 */
	boolean exists(String aid, String cid);
}
