package com.babeeta.butterfly.application.management.dao;

import java.io.Serializable;
import java.util.List;

import com.babeeta.butterfly.application.management.entity.App;
/***
 * 应用
 * @author zeyong.xia
 * @date 2011-12-9
 * @param <T>
 * @param <K>
 */
public interface AppDao<T, K extends Serializable> extends IDao<T, K> {

	/***
	 * 查询某用户的应用
	 * @param email
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAppByEmail(String email,int offset,int limit);
	
	/***
	 * 
	 * @param offset
	 * @param limit
	 * @param email
	 * @return
	 */
	public List<App> queryAppList(int offset,int limit,String email,List<String> status);
	
	/***
	 * 更新应用
	 * @param app
	 * @return
	 */
	public App updateApp(App app);
	
	/***
	 * 查询未审核的app总数
	 * @return
	 */
	public int queryNotCheckAppCount();
}
