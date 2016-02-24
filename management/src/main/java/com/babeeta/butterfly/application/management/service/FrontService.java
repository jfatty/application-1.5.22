package com.babeeta.butterfly.application.management.service;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.entity.Role;
import com.babeeta.butterfly.application.management.entity.User;

/****
 * 页面前台业务逻辑
 * @author zeyong.xia
 * @date 2011-12-8
 */
public interface FrontService {

	/***
	 * 新增用户
	 * @param user
	 * @return
	 */
	public User addUser(User user);
	
	/***
	 * 新增用户
	 * @param user
	 * @return
	 */
	public User queryUserByEmail(String email);
	
	/***
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public User updateUser(User user);
	
	/***
	 * 删除用户信息
	 * @param id
	 */
	public void deleteUser(String id);
	
	/****
	 * 分页查询用户信息
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<User> queryUserList(int offset,int limit);
	
	/***
	 * 查询用户总数
	 * @return
	 */
	public int queryUserCount();
	
	/***
	 * 判断email是否注册
	 * @param email
	 * @return
	 */
	public User queryEmail(String email);
	
	/***
	 * 用户登录
	 * @return
	 */
	public User login(String userEmail,String userPass);
	
	/***
	 * 查询角色
	 * @param email
	 * @return
	 */
	public Role queryRoleByEmail(String email);
	
	/***
	 * 查询用户应用
	 * @param email
	 * @return
	 */
	public List<App> queryAppByEmail(Map<String,Object> map,int offset,int limit);
	
	/***
	 * 查询用用总数
	 * @param map
	 * @return
	 */
	public int queryAppCount(Map<String,Object> map);
	
	/****
	 * 查询应用
	 * @param id
	 * @return
	 */
	public App queryAppById(String id);
	
	
	/***
	 * 查询某用户消息总数
	 * @param map
	 * @return
	 */
	public int queryMyMessageCount(Map<String,Object> map);
	
	/***
	 * 分页查询某用户消息列表
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public List<GroupMessage> queryMyMessageList(int offset,int limit,Map<String,Object> map);
}
