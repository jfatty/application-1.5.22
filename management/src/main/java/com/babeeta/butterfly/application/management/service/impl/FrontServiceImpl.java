package com.babeeta.butterfly.application.management.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.dao.AppDao;
import com.babeeta.butterfly.application.management.dao.MessageDao;
import com.babeeta.butterfly.application.management.dao.RoleDao;
import com.babeeta.butterfly.application.management.dao.UserDao;
import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.entity.Role;
import com.babeeta.butterfly.application.management.entity.User;
import com.babeeta.butterfly.application.management.service.FrontService;

/****
 * 
 * @author zeyong.xia
 * @date 2011-12-8
 */
public class FrontServiceImpl implements FrontService {
	
	private final static Logger logger = LoggerFactory
	.getLogger(FrontServiceImpl.class);
	
	private UserDao<User, String> userDaoImpl;
	
	private RoleDao<Role,String> roleDaoImpl;
	
	private AppDao<App,String> appDaoImpl;
	
	private MessageDao<GroupMessage,String> messageDaoImpl;

	/***
	 * 新增用户
	 * @param user
	 * @return
	 */
	public User addUser(User user)
	{
		User flag=this.userDaoImpl.queryUserByEmail(user.getUserEmail());
		if(flag!=null)
		{
			logger.debug("[FrontServiceImpl] has exists {}",user.getUserEmail());
			return user;
		}
		this.userDaoImpl.save(user);
		return user;
	}
	
	/***
	 * 新增用户
	 * @param user
	 * @return
	 */
	public User queryUserByEmail(String email)
	{
		return this.userDaoImpl.queryUserByEmail(email);
	}
	/***
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public User updateUser(User user)
	{
		return null;
	}
	
	/***
	 * 删除用户信息
	 * @param id
	 */
	public void deleteUser(String id)
	{
		this.userDaoImpl.deleteByEntityId(id);
	}
	
	/****
	 * 分页查询用户信息
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<User> queryUserList(int offset,int limit)
	{
		return this.userDaoImpl.queryList(offset, limit);
	}
	
	/***
	 * 查询用户总数
	 * @return
	 */
	public int queryUserCount()
	{
		return this.userDaoImpl.queryCount();
	}

	/***
	 * 判断email是否注册
	 * @param email
	 * @return
	 */
	public User queryEmail(String email)
	{
		return this.userDaoImpl.queryUserByEmail(email);
	}
	
	/***
	 * 用户登录
	 * @return
	 */
	public User login(String userEmail,String userPass)
	{
		return this.userDaoImpl.login(userEmail, userPass);
	}
	
	/***
	 * 查询角色
	 * @param email
	 * @return
	 */
	public Role queryRoleByEmail(String email)
	{
		return this.roleDaoImpl.queryByUserAccount(email);
	}
	
	/***
	 * 查询用户应用
	 * @param email
	 * @return
	 */
	public List<App> queryAppByEmail(Map<String,Object> map,int offset,int limit)
	{
		return this.appDaoImpl.queryList(offset, limit, map);
	}
	
	/***
	 * 查询用用总数
	 * @param map
	 * @return
	 */
	public int queryAppCount(Map<String,Object> map)
	{
		return this.appDaoImpl.queryCount(map);
	}
	
	/****
	 * 查询应用
	 * @param id
	 * @return
	 */
	public App queryAppById(String id)
	{
		return this.appDaoImpl.findById(id);
	}
	
	
	/***
	 * 查询某用户消息总数
	 * @param map
	 * @return
	 */
	public int queryMyMessageCount(Map<String,Object> map)
	{
		return this.messageDaoImpl.queryCount(map);
	}
	
	/***
	 * 分页查询某用户消息列表
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public List<GroupMessage> queryMyMessageList(int offset,int limit,Map<String,Object> map)
	{
		return this.messageDaoImpl.queryList(offset, limit, map);
	}
	
	public void setUserDaoImpl(UserDao<User, String> userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}

	public void setRoleDaoImpl(RoleDao<Role, String> roleDaoImpl) {
		this.roleDaoImpl = roleDaoImpl;
	}

	public void setAppDaoImpl(AppDao<App, String> appDaoImpl) {
		this.appDaoImpl = appDaoImpl;
	}

	public void setMessageDaoImpl(MessageDao<GroupMessage, String> messageDaoImpl) {
		this.messageDaoImpl = messageDaoImpl;
	}
	
	
}
