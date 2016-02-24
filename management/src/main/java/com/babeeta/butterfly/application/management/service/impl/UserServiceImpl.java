package com.babeeta.butterfly.application.management.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.dao.RoleDao;
import com.babeeta.butterfly.application.management.dao.UserDao;
import com.babeeta.butterfly.application.management.entity.Role;
import com.babeeta.butterfly.application.management.entity.User;
import com.babeeta.butterfly.application.management.service.UserService;

public class UserServiceImpl implements UserService {

	private final static Logger logger = LoggerFactory
	.getLogger(UserServiceImpl.class);
	
	private UserDao<User, String> userDaoImpl;
	
	private RoleDao<Role,String> roleDaoImpl;
	
	public UserServiceImpl()
	{
		/**
		System.out.println("userDaoImpl= "+userDaoImpl);
		int num=this.userDaoImpl.queryCount();
		if(num==0)
		{
			User user=new User();
			user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			user.setUserEmail("xiazeyong521@sohu.com");
			user.setUserPass("aaaaaa");
			user.setAddress("北京市");
			user.setCompanyName("巴别塔");
			user.setContactMan("张三");
			user.setContactPhone("13800138000");
			user.setCreateAt(new Date());
			user.setRoleName("root");
			this.userDaoImpl.save(user);
			
		
		}**/
	}
	
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
			logger.debug("[UserServiceImpl] has exists {}",user.getUserEmail());
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
		this.userDaoImpl.updateUser(user);
		return user;
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
	public List<User> queryList(int offset,int limit,Map<String,Object> map)
	{
		return this.userDaoImpl.queryList(offset, limit, map);
	}
	
	/***
	 * 查询用户列表
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public List<User> queryUserList(int offset,int limit,Map<String,Object> map)
	{
		return this.userDaoImpl.queryUserList(offset, limit, map);
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
	 * 查询总数
	 * @return
	 */
	public int queryCount()
	{
		int num=this.userDaoImpl.queryCount();
		if(num>0)
		{
			return num-1;
		}
		return 0;
	}
	public void setUserDaoImpl(UserDao<User, String> userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}

	public void setRoleDaoImpl(RoleDao<Role, String> roleDaoImpl) {
		this.roleDaoImpl = roleDaoImpl;
	}
	
}
