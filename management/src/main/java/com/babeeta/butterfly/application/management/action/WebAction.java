package com.babeeta.butterfly.application.management.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.Role;
import com.babeeta.butterfly.application.management.entity.User;
import com.babeeta.butterfly.application.management.service.BackService;
import com.opensymphony.xwork2.ActionSupport;

public class WebAction extends ActionSupport implements ServletRequestAware {

	private HttpServletRequest request;
	
	private BackService backServiceImpl;
	
	
	private final static Logger logger = LoggerFactory
	.getLogger(WebAction.class);
	
	/****
	 * 登录
	 * @return
	 */
	public String login()
	{
		logger.debug("[WebAction] login ");
		String userAccount=request.getParameter("userName");
		String userPass=request.getParameter("userPass");
		System.out.println("userAccount:"+userAccount+ "|| userPass:"+userPass);
		if(userAccount==""||userPass=="")
		{
			logger.debug("[WebAction] login ,userAccount or userPass is null");
			return ERROR;
		}
		User u=this.backServiceImpl.login(userAccount, userPass);
		if(u!=null)
		{
			request.getSession().setAttribute("user", u.getUserEmail());
			Role role=this.backServiceImpl.queryByUserAccount(userAccount);
			if(role.getRoleName().equals("check"))
			{
				List<App> list=this.backServiceImpl.queryAll(0, 30);
				request.setAttribute("list", list);
				return "check";
			}
			else
			{
				//AppRegister app=this.backServiceImpl.queryAppByUserAccount(userAccount);
				Map map=null;
				request.setAttribute("map", map);
				return "user";
			}
		}
		return ERROR;
	}
	
	
	/****
	 * 应用注册
	 * @return
	 */
	public String registerApp()
	{

		logger.debug("[WebAction] registerApp");
		String life=request.getParameter("messageTime");
		String appName=request.getParameter("appName");
		String appCom=request.getParameter("appCom");
		String appDesc=request.getParameter("appDesc");
		App app=new App();
		app.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		//app.setAppDesc(appDesc);
		app.setAppName(appName);
		//app.setAppType(life);
		app.setCreateAt(new Date());
		//app.setUserAccount((String)request.getSession().getAttribute("user"));
		System.out.println("start add app");
		app=this.backServiceImpl.addAppRegister(app);
		
		if(app!=null)
		{
			logger.debug("[WebAction] registerApp error");
			return SUCCESS;
		}
		logger.debug("[WebAction] registerApp success");
		return ERROR;
	}
	
	/****
	 * 应用审核
	 * @return
	 */
	public String checkApp()
	{
		System.out.println("check app");
		String result=request.getParameter("result");
		String appId=request.getParameter("appId");
		System.out.println(appId+"****"+result);
		if(appId=="")
		{
			return ERROR;
		}
		App app=this.backServiceImpl.queryById(appId);
		System.out.println(app.getAppType());
		if(result=="1"||result.equals("1"))
		{
			System.out.println("pass");
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("life", app.getAppType());
			String ak=this.backServiceImpl.registerApp(map);
			if(ak=="")
			{
				return ERROR;
			}
			//JSONObject obj=(JSONObject) JSON.parse(ak);
			JSONObject obj=JSONObject.fromObject(ak);
			//obj.put("result", ak);
			System.out.println(obj.toString());
			String aid=obj.getString("id");
			String key=obj.getString("secureKey");
			//System.out.println("ak="+obj.getString("id")+":"+obj.getString("secureKey"));
			request.setAttribute("aid", aid);
			request.setAttribute("key", key);
			//app.setResult("1");
			String account="";
			User u=this.backServiceImpl.queryUserByAccount(account);
			Map<String,Object> maps=new HashMap<String,Object>();
			maps.put(aid, key);
			//u.setExtra(maps);
			this.backServiceImpl.updateUser(u);
			this.backServiceImpl.updateAppRegister(app);
			return SUCCESS;
		}
		if(result=="2"||result.equals("2"))
		{
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("life", "default");
			String ak=this.backServiceImpl.registerApp(map);
			if(ak=="")
			{
				return ERROR;
			}
			JSONObject obj=JSONObject.fromObject(ak);
			//obj.put("result", ak);
			String aid=obj.getString("id");
			String key=obj.getString("secureKey");
			request.setAttribute("aid", obj.getString("id"));
			request.setAttribute("key", obj.getString("secureKey"));
			//app.setResult("2");
			String account="";
			User u=this.backServiceImpl.queryUserByAccount(account);
			Map<String,Object> maps=new HashMap<String,Object>();
			maps.put(aid, key);
			//u.setExtra(maps);
			this.backServiceImpl.updateUser(u);
			this.backServiceImpl.updateAppRegister(app);
			return SUCCESS;
		}
		//app.setResult("3");
		this.backServiceImpl.updateAppRegister(app);
		return ERROR;
	}
	
	
	public String appInfo()
	{
		String appId=request.getParameter("appId");
		App app=this.backServiceImpl.queryById(appId);
		if(app==null)
		{
			return ERROR;
		}
		request.setAttribute("app", app);
		return SUCCESS;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}

	public void setBackServiceImpl(BackService backServiceImpl) {
		this.backServiceImpl = backServiceImpl;
	}

}
