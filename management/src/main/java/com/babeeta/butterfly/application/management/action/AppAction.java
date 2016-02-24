package com.babeeta.butterfly.application.management.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.service.AppService;

/***
 * 应用
 * @author zeyong.xia
 * @date 2011-12-9
 */
public class AppAction extends BaseAction{

	/**
	 * @author zeyong.xia
	 * @date 2011-12-9
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory
	.getLogger(AppAction.class);
	
	private AppService appServiceImpl;//
	
	private App app;//应用信息
	
	/***
	 * 申请应用
	 * @return
	 */
	public String addApp()
	{
		logger.debug("[AppAction] addApp");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail=="")
		{
			logger.debug("[AppAction] addApp,user not login");
			return "login";
		}
		app.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		app.setCheckStatus("0");
		app.setCreateAt(new Date());
		String[] modes=request.getParameterValues("pushMode");
		String[] lifes=request.getParameterValues("messageLifeCycle");
		String[] formats=request.getParameterValues("messageFormat");
		app.setPushMode(modes);
		app.setMessageFormat(formats);
		app.setMessageLifeCycle(lifes);
		app.setUserEmail(userEmail);
		try{
		this.appServiceImpl.addApp(app);
		}
		catch(Exception e)
		{
			logger.debug("[AppAction] addApp failure, exception is {}",e.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
	
	/***
	 * 应用详细信息
	 * @return
	 */
	public String queryApp()
	{
		logger.debug("[AppAction] queryApp");
		String id=request.getParameter("appId");
		if(id==null||id=="")
		{
			logger.debug("[AppAction] queryApp,id is null");
			return ERROR;
		}
		app=this.appServiceImpl.queryById(id);
		if(app==null)
		{
			logger.debug("[AppAction] queryApp,app is null");
			return ERROR;
		}
		request.setAttribute("app", app);
		return SUCCESS;
	}
	
	/***
	 * 更新应用
	 * @return
	 */
	public String updateApp()
	{
		return SUCCESS;
	}
	
	/***
	 * 分页查询应用列表
	 * @return
	 */
	public String queryAppList()
	{
		logger.debug("[AppAction] queryAppList");
		String page=request.getParameter("page");
		List<App> list=this.appServiceImpl.queryAppList(0, 10);
		System.out.println(list.size());
		request.setAttribute("appList", list);
		return SUCCESS;
	}

	/***
	 * 转换
	 * @param src
	 * @return
	 */
	private Map<String,String> chansform(String[] src)
	{
		
		if(src!=null&&src.length>0)
		{
			Map<String,String> map=new HashMap<String,String>();
			for(int i=0;i<src.length;i++)
			{
				map.put(String.valueOf(i), src[i]);
			}
			return map;
		}
		return null;
	}
	
	
	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public void setAppServiceImpl(AppService appServiceImpl) {
		this.appServiceImpl = appServiceImpl;
	}
	
	
}
