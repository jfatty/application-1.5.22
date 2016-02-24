package com.babeeta.butterfly.application.management.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.dao.AppDao;
import com.babeeta.butterfly.application.management.dao.MorphiaDataStore;
import com.babeeta.butterfly.application.management.entity.App;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

/****
 * 应用
 * @author zeyong.xia
 * @date 2011-12-9
 */
public class AppDaoImpl extends BaseDao<App, String> implements AppDao<App, String> {

	public AppDaoImpl(MorphiaDataStore ds) {
		super(App.class, ds);
		// TODO Auto-generated constructor stub
	}

	/***
	 * 查询某用户的应用
	 * @param email
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAppByEmail(String email,int offset,int limit)
	{
		Query<App> query=this.ds.createQuery(App.class).offset(offset).limit(limit).filter("userEmail", email).order("-createAt");
		List<App> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return list;
		}
		return null;
	}
	/***
	 * 
	 * @param offset
	 * @param limit
	 * @param email
	 * @return
	 */
	public List<App> queryAppList(int offset,int limit,String email,List<String> status)
	{
		Query<App> query=this.ds.createQuery(App.class).offset(offset).limit(limit).order("-createAt");
		if(email!=null&&email!="")
		{
			query.filter("userEmail", email);
		}
		query.field("checkStatus").in(status);		
		List<App> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return list;
		}
		return null;
	}
	/***
	 * 更新应用
	 * @param app
	 * @return
	 */
	public App updateApp(App app)
	{
		Query<App> query=createQuery().filter("_id", app.getId());
		UpdateOperations<App> uos=this.createUpdateOperations();
		uos.set("appName", app.getAppName());
		uos.set("appType", app.getAppType());
		uos.set("packageName", app.getPackageName());
		uos.set("appProfile", app.getAppProfile());
		uos.set("platForm", app.getPlatForm());
		uos.set("checkView", app.getCheckView());
		if(app.getPushMode()==null)
		{
			String[] map=new String[]{"broadcast"};
			uos.set("pushMode", map);
		}
		else{
			uos.set("pushMode", app.getPushMode());
		}
		if(app.getMessageFormat()==null)
		{
			String[] map=new String[]{"txt"};
			uos.set("messageFormat", map);
		}
		else{
			uos.set("messageFormat", app.getMessageFormat());
		}
		if(app.getPushMode()==null)
		{
			String[] map=new String[]{"default"};
			uos.set("pushMode", map);
		}
		else{
			uos.set("messageLifeCycle", app.getMessageLifeCycle());
		}
		uos.set("callBackUrl", app.getCallBackUrl());
		uos.set("checkStatus", app.getCheckStatus());
		if(app.getAppId()==null||app.getAppId().equals(""))
		{
			uos.set("appId", "");
		}
		else{
		uos.set("appId", app.getAppId());
		}
		if(app.getAppKey()==null)
		{
			uos.set("appKey", "");
		}
		else{
		uos.set("appKey", app.getAppKey());
		}
		this.ds.update(query, uos);
		return app;
	}
	
	/***
	 * 查询未审核的app总数
	 * @return
	 */
	public int queryNotCheckAppCount()
	{
		Query<App> query=this.ds.createQuery(App.class).filter("checkStatus", "0");
		if(query!=null)
		{
			return (int)query.countAll();
		}
		return 0;
		
	}
}
