package com.babeeta.butterfly.application.management.service;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.Role;
import com.babeeta.butterfly.application.management.entity.User;

public interface BackService {
	/***
	 * 添加用户
	 * @param user
	 * @return
	 */
	public User addUser(User user);
	
	
	/***
	 * 删除用户
	 * @param user
	 */
	public void deleteUser(User user);
	
	/***
	 * 用户登录
	 * @param userAccount
	 * @param userPass
	 * @return
	 */
	public User login(String userAccount,String userPass);
	
	/***
	 * 查询用户
	 * @param user
	 * @return
	 */
	public User queryUserByAccount(String account);
	
	/***
	 * 更新用户
	 * @param user
	 * @return
	 */
	public User updateUser(User user);
	/***
	 * 注册
	 * @param app
	 * @return
	 */
	public App addAppRegister(App app);
	
	/****
	 * 更新
	 * @param app
	 * @return
	 */
	public App updateAppRegister(App app);
	
	/***
	 * 删除
	 * @param app
	 */
	public void deleteAppRegister(App app);
	
	/***
	 * 通过id查询
	 * @param id
	 * @return
	 */
	public App queryById(String id);
	
	/****
	 * 分页查询
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAll(int offset,int limit);
	
	/****
	 * 通过用户查询
	 * @param userAccount
	 * @return
	 */
	public App queryAppByUserAccount(String userAccount);
	
	/***
	 * 添加角色
	 * @param role
	 * @return
	 */
	public Role addRole(Role role);
	
	/****
	 * 查询角色信息
	 * @param userAccount
	 * @return
	 */
	public Role queryByUserAccount(String userAccount);
	
	/***
	 * 应用注册
	 * @param map
	 * @return
	 */
	public String registerApp(Map<String,Object> map);
}
