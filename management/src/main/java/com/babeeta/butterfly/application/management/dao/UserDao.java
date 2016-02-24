package com.babeeta.butterfly.application.management.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.entity.User;

/****
 * 
 * @author zeyong.xia
 * @date 2011-10-25
 */
public interface UserDao<T, K extends Serializable> extends IDao<T, K> {

	
	/***
	 * 用户登录
	 * @param userAccount
	 * @param userPass
	 * @return
	 */
	public User login(String userEmail,String userPass);
	
	/***
	 * 查询用户
	 * @param user
	 * @return
	 */
	public User  queryUserByEmail(String email);
	
	/****
	 * 
	 * @param user
	 * @return
	 */
	public User updateUser(User user);
	
	/***
	 * 查询用户列表
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public List<T> queryUserList(int offset,int limit,Map<String,Object> map);
}
