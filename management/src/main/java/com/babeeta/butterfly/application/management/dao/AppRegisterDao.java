package com.babeeta.butterfly.application.management.dao;

import java.util.List;

import com.babeeta.butterfly.application.management.entity.App;

/***
 * 
 * @author zeyong.xia
 * @date 2011-10-25
 */
public interface AppRegisterDao {

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
	public App queryByUserAcount(String userAccount);
}
