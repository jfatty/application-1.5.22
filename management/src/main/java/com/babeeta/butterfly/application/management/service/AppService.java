package com.babeeta.butterfly.application.management.service;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.entity.App;

/****
 * 应用逻辑
 * @author zeyong.xia
 * @date 2011-12-9
 */
public interface AppService {

	/***
	 * 新增应用
	 * @param app
	 * @return
	 */
	public App addApp(App app);
	
	/***
	 * 查询应用
	 * @param id
	 * @return
	 */
	public App queryById(String id);
	
	/***
	 * 更新应用
	 * @param app
	 * @return
	 */
	public App updateApp(App app);
	
	/***
	 * 删除
	 * @param id
	 * @return
	 */
	public void deleteApp(String id);
	
	/***
	 * 总数
	 * @return
	 */
	public int queryAppCount();
	
	/***
	 * 分页查询应用
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAppList(int offset,int limit);
	
	/***
	 * 查询某用户应用列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryMyAppList(int offset,int limit,String userEmail);
	
	/***
	 * 查询某用户应用列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAppList(int offset,int limit,String userEmail,List<String> list);
	
	/***
	 * 应用注册
	 * @param map
	 * @return
	 */
	public String registerApp(App app);
	
	/***
	 * 查询未审核的app总数
	 * @return
	 */
	public int queryNotCheckAppCount();
	
	/***
	 * 查询应用
	 * @param email
	 * @return
	 */
	public List<App> queryAppList(Map<String,Object> map,int offset,int limit);
	
	/***
	 * 查询用用总数
	 * @param map
	 * @return
	 */
	public int queryAppCount(Map<String,Object> map);
}
