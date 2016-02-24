package com.babeeta.butterfly.application.management.service;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.entity.Role;
import com.babeeta.butterfly.application.management.entity.User;

/***
 * 用户
 * @author zeyong.xia
 * @date 2011-12-13
 */
public interface UserService {
	
	/***
	 * 新增用户
	 * @param user
	 * @return
	 */
	public User addUser(User user);
	
	/***
	 * 查询用户
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
	public List<User> queryUserList(int offset,int limit,Map<String,Object> map);
	
	/***
	 * 查询总数
	 * @return
	 */
	public int queryCount();
	
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
}
