package com.babeeta.butterfly.application.management.action;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.service.AppService;
import com.babeeta.butterfly.application.management.service.FrontService;
import com.babeeta.butterfly.application.management.service.LicenceService;
import com.babeeta.butterfly.application.management.service.MessageService;

/****
 * 前台
 * @author zeyong.xia
 * @date 2011-12-13
 */
public class FrontAction extends BaseAction {

	/**
	 * @author zeyong.xia
	 * @date 2011-12-13
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory
	.getLogger(MessageAction.class);
	
	private AppService appServiceImpl;//
	
	private MessageService messageServiceImpl;
	
	private LicenceService licenceServiceImpl;
	
	private FrontService frontServiceImpl;//
	
	private App app;
	
	private GroupMessage message;
	
	/***
	 * 申请应用
	 * @return
	 */
	public String addApp()
	{
		logger.debug("[FrontAction] addApp");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail=="")
		{
			logger.debug("[FrontAction] addApp,user not login");
			return "login";
		}
		app.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		app.setCheckStatus("0");
		app.setCreateAt(new Date());
		if(app.getPushMode()==null||app.getPushMode().length==0)
		{
			app.setPushMode(new String[]{"one"});
		}
		else
		{

			String[] push=app.getPushMode();
			String[] mode=new String[push.length+1];
			for(int i=0;i<mode.length;i++)
			{
				if(i==0)
				{
					mode[i]="one";
				}
				else
				{
					mode[i]=push[i-1];
				}
			}
			app.setPushMode(mode);
		
		}
		if(app.getMessageFormat()==null||app.getMessageFormat().length==0)
		{
			app.setMessageFormat(new String[]{"text"});
		}
		else
		{
			String[] message=app.getMessageFormat();
			String[] format=new String[message.length+1];
			for(int i=0;i<format.length;i++)
			{
				if(i==0)
				{
					format[i]="text";
				}
				else
				{
					format[i]=message[i-1];
				}
			}
			app.setMessageFormat(format);
		
		}
		String[] life=app.getMessageLifeCycle();
		if(life==null||life.length==0)
		{
			app.setMessageLifeCycle(new String[]{"default"});
		}
		else
		{
			String[] cycle=new String[life.length+1];
			for(int i=0;i<cycle.length;i++)
			{
				if(i==0)
				{
					cycle[i]="default";
				}
				else
				{
					cycle[i]=life[i-1];
				}
			}
			for(int i=0;i<cycle.length;i++)
			{
				if(cycle[i]=="custom"||cycle[i].equals("custom"))
				{
					String val=request.getParameter("self");
					cycle[i]=val;
				}
			}
			app.setMessageLifeCycle(cycle);
		}
//		String[] modes=request.getParameterValues("pushMode");
//		String[] lifes=request.getParameterValues("messageLifeCycle");
//		String[] formats=request.getParameterValues("messageFormat");
//		System.out.println(modes.length+" "+lifes.length+" "+formats.length);
//		app.setPushMode(modes);
//		app.setMessageFormat(formats);
//		app.setMessageLifeCycle(lifes);
		app.setUserEmail(userEmail);
		try{
		app.setCheckView("");
		app.setAppId("");
		app.setAppKey("");
		this.appServiceImpl.addApp(app);
		}
		catch(Exception e)
		{
			logger.debug("[FrontAction] addApp failure, exception is {}",e.getMessage());
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
		logger.debug("[FrontAction] queryApp");
		String id=request.getParameter("appId");
		
		if(id==null||id=="")
		{
			logger.debug("[FrontAction] queryApp,id is null");
			return ERROR;
		}
		app=this.appServiceImpl.queryById(id);
		
		if(app==null)
		{
			logger.debug("[FrontAction] queryApp,app is null");
			return ERROR;
		}
		request.setAttribute("app", app);
		logger.debug("[FrontAction] queryApp success");
		return SUCCESS;
	}
	
	/***
	 * 消息详细信息
	 * @return
	 */
	public String queryMessageById()
	{
		logger.debug("[FrontAction] queryMessageById()");
		String id=request.getParameter("messageId");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(id==null||id=="")
		{
			logger.debug("[FrontAction] queryMessageById(),id is null");
			return ERROR;
		}
		if(userEmail==null||userEmail.equals(""))
		{
			logger.debug("[FrontAction] queryMessageById(),not login");
			return "login";
		}
		message=this.messageServiceImpl.queryMessageById(id);
		String appId=this.message.getAppId();
		if(appId==null||appId.equals(""))
		{
			appId=this.message.getAppName();
		}
		App app=this.appServiceImpl.queryById(appId);
		if(app==null)
		{
			logger.debug("[FrontAction] queryMessageById(),app is null");
			return ERROR;
		}
		List<String> status=new ArrayList<String>();
		status.add("1");
		status.add("2");
		List<App> list=this.appServiceImpl.queryAppList(0,10,userEmail,status);
		request.setAttribute("appList", list);
		this.request.setAttribute("app", app);
		request.setAttribute("message", message);
		logger.debug("[FrontAction] queryMessageById() success");
		return SUCCESS;
	}
	/***
	 * 分页查询应用列表
	 * @return
	 */
	public String queryMyAppList()
	{
		logger.debug("[FrontAction] queryAppList");
		String email=(String)request.getSession().getAttribute("userEmail");
		if(email==null||email=="")
		{
			logger.debug("[FrontAction]queryMyAppList, user not login");
			return "login";
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("userEmail", email);
		int totalCount=this.appServiceImpl.queryAppCount(map);
		int offset = this.setPage(totalCount, request);
		List<App> list=this.appServiceImpl.queryMyAppList(offset, ROWCOUNT, email);
		request.setAttribute("appList", list);
		return SUCCESS;
	}
	
	
	public String queryMyMessageList()
	{
		logger.debug("[FrontAction] queryMyMessageList");
		String email=(String)request.getSession().getAttribute("userEmail");
		if(email==null||email=="")
		{
			logger.debug("[FrontAction]queryMyAppList, user not login");
			return "login";
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("userEmail", email);
		int totalCount=this.frontServiceImpl.queryMyMessageCount(map);
		int offset = this.setPage(totalCount, request);
		List<GroupMessage> list=this.frontServiceImpl.queryMyMessageList(offset, ROWCOUNT,map);

		request.setAttribute("messageList", list);
		return SUCCESS;
	}
	/***
	 * 转换
	 * @param src
	 * @return
	 */
	private Map<String,String> chansformPushMode(String[] src)
	{
		
		if(src!=null&&src.length>0)
		{
			Map<String,String> map=new HashMap<String,String>();
			for(int i=0;i<src.length;i++)
			{
				if(src[i]=="broadcast"||src[i].equals("broadcast"))
				{
					map.put(src[i],"广播");
				}
				else if(src[i]=="group"||src[i].equals("group"))
				{
					map.put(src[i],"组播");
				}
				else if(src[i]=="one"||src[i].equals("one"))
				{
					map.put(src[i],"单条");
				}
				
			}
			return map;
		}
		return null;
	}
	/***
	 * 转换
	 * @param src
	 * @return
	 */
	private Map<String,String> chansformLifeCycle(String[] src)
	{
		
		if(src!=null&&src.length>0)
		{
			Map<String,String> map=new HashMap<String,String>();
			for(int i=0;i<src.length;i++)
			{
				if(src[i]=="default"||src[i].equals("default"))
				{
					map.put(src[i],"默认");
				}
				else if(src[i]=="never"||src[i].equals("never"))
				{
					map.put(src[i],"永久");
				}
				else 
				{
					map.put(src[i],src[i]+"天");
				}
				
			}
			return map;
		}
		return null;
	}
	/***
	 * 转换
	 * @param src
	 * @return
	 */
	private Map<String,String> chansformMessageFormat(String[] src)
	{
		
		if(src!=null&&src.length>0)
		{
			Map<String,String> map=new HashMap<String,String>();
			for(int i=0;i<src.length;i++)
			{
				if(src[i]=="txt"||src[i].equals("text"))
				{
					map.put(src[i],"文本");
				}
				else if(src[i]=="audio"||src[i].equals("audio"))
				{
					map.put(src[i],"音乐");
				}
				else if(src[i]=="vedio"||src[i].equals("vedio"))
				{
					map.put(src[i],"视频");
				}
				else if(src[i]=="image"||src[i].equals("image"))
				{
					map.put(src[i],"图片");
				}
				else if(src[i]=="net"||src[i].equals("net"))
				{
					map.put(src[i],"网页");
				}
				else if(src[i]=="app"||src[i].equals("app"))
				{
					map.put(src[i],"应用");
				}
				else if(src[i]=="phone"||src[i].equals("phone"))
				{
					map.put(src[i],"电话");
				}
				else if(src[i]=="local"||src[i].equals("local"))
				{
					map.put(src[i],"地理位置");
				}
				else
				{
					
				}
				
			}
			return map;
		}
		return null;
	}
	/***
	 * 初始化推送前信息
	 * @return
	 */
	public String initPush()
	{
		logger.debug("[FrontAction] initPush");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail=="")
		{
			logger.debug("[FrontAction] initPush,user not login");
			return "login";
		}
		List<String> status=new ArrayList<String>();
		status.add("1");
		status.add("2");
		List<App> list=this.appServiceImpl.queryAppList(0,200,userEmail,status);
		request.setAttribute("appList", list);
		logger.debug("[FrontAction] initPush success");
		return SUCCESS;
	}

	/****
	 * 群发消息
	 * @return
	 */
	public String broadcast()
	{
		logger.debug("[FrontAction] broadcast");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail=="")
		{
			logger.debug("[FrontAction] broadcast,user not login");
			return "login";
		}
		String appId=request.getParameter("appName");
		if(appId==null||appId.equals(""))
		{
			logger.debug("[FrontAction] broadcast,appId is null");
			return "error";
		}
		App app=this.appServiceImpl.queryById(appId);
		if(app==null)
		{
			logger.debug("[FrontAction] broadcast,app is null");
			return "error";
		}
		String target=request.getParameter("target");
		String msg_format=request.getParameter("msg_format");
		String life=request.getParameter("life");
		String msg=request.getParameter("msg");
		GroupMessage gm=new GroupMessage();
		gm.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		gm.setAppName(app.getAppName());
		gm.setAppId(appId);
		gm.setCheck(0);
		gm.setCreateAt(new Date());
		gm.setMessageAge(life);
		gm.setMessageContent(msg);
		gm.setMessageFormat(msg_format);
		gm.setPushTarget(target);
		gm.setUserEmail(userEmail);
		this.messageServiceImpl.addMessage(gm);
		logger.debug("[FrontAction] broadcast success");
		return SUCCESS;
	}
	
	/***
	 * 查询消息时效
	 * 
	 */
	public void queryMessageLifeAndFormat()
	{
		logger.debug("[FrontAction] queryMessageLifeAndFormat");
		String appId=request.getParameter("appId");
		if(appId==null||appId.equals(""))
		{
			logger.debug("[FrontAction] queryMessageLifeAndFormat,appId is null");
			return;
		}
		App app=null;
		try{
		app=this.appServiceImpl.queryById(appId);
		}catch(Exception e)
		{
			logger.debug("[FrontAction] queryMessageLifeAndFormat ,exception is {}",e.getMessage());
		}
		String[] life=app.getMessageLifeCycle();
		StringBuffer sb=new StringBuffer();
		if(life!=null&&life.length>0)
		{
			for(int i=0;i<life.length;i++)
			{
				String value=life[i];
				if(value=="default"||value.equals("default"))
				{
					sb.append(value).append(":").append("默认").append(",");
				}
				else if(value=="never"||value.equals("never"))
				{
					sb.append(value).append(":").append("永久").append(",");
				}
				else
				{
					sb.append(value).append(":").append(value).append("天").append(",");
				}
			}
		}
		else
		{
			sb.append("default:默认,");
		}
		sb.delete(sb.length()-1, sb.length());
		sb.append("/");
		String[] format=app.getMessageFormat();
		if(format!=null&&format.length>0)
		{
			//{'txt':'文本','audio':'音乐','vedio':'视频','image':'图片','net':'网页','app':'应用','phone':'电话','local':'地理位置'
			for(int i=0;i<format.length;i++)
			{
				String value=format[i];
				if(value=="txt"||value.equals("text"))
				{
					sb.append(value).append(":").append("文本").append(",");
				}
				else if(value=="audio"||value.equals("audio"))
				{
					sb.append(value).append(":").append("音乐").append(",");
				}
				else if(value=="vedio"||value.equals("vedio"))
				{
					sb.append(value).append(":").append("视频").append(",");
				}
				else if(value=="image"||value.equals("image"))
				{
					sb.append(value).append(":").append("图片").append(",");
				}
				else if(value=="net"||value.equals("net"))
				{
					sb.append(value).append(":").append("网页").append(",");
				}
				else if(value=="app"||value.equals("app"))
				{
					sb.append(value).append(":").append("应用").append(",");
				}
				else if(value=="phone"||value.equals("phone"))
				{
					sb.append(value).append(":").append("电话").append(",");
				}
				else if(value=="local"||value.equals("local"))
				{
					sb.append(value).append(":").append("地理位置").append(",");
				}
				else
				{
					//sb.append(value).append(":").append("文本").append(",");
				}
			}
		}
		else
		{
			sb.append("text:文本,");
		}
		PrintWriter out;
		try {
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
		logger.debug("[FrontAction] queryMessageLifeAndFormat result is {}",sb.toString().substring(0,sb.toString().length()-1));
		out.write(sb.toString().substring(0,sb.toString().length()-1));
		out.flush();
		out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("[FrontAction] queryMessageLife ,exception is {}",e.getMessage());
		}
		//request.setAttribute("map", map);
		//request.setAttribute("app", app);
		System.out.println("success");
	}

	/***
	 * 修改应用
	 * @return
	 */
	public String updateApp()
	{
		logger.debug("[FrontAction] updateApp");
		try{
			app.setCheckStatus("0");//需要重新审核
			if(app.getPushMode()==null||app.getPushMode().length==0)
			{
				app.setPushMode(new String[]{"one"});
			}
			else
			{
				String[] push=app.getPushMode();
				String[] mode=new String[push.length+1];
				for(int i=0;i<mode.length;i++)
				{
					if(i==0)
					{
						mode[i]="one";
					}
					else
					{
						mode[i]=push[i-1];
					}
				}
				app.setPushMode(mode);
			}
			if(app.getMessageFormat()==null||app.getMessageFormat().length==0)
			{
				app.setMessageFormat(new String[]{"text"});
			}
			else
			{

				String[] message=app.getMessageFormat();
				String[] format=new String[message.length+1];
				for(int i=0;i<format.length;i++)
				{
					if(i==0)
					{
						format[i]="text";
					}
					else
					{
						format[i]=message[i-1];
					}
				}
				app.setMessageFormat(format);
			}
			String[] life=app.getMessageLifeCycle();
			if(life==null||life.length==0)
			{
				app.setMessageLifeCycle(new String[]{"default"});
			}
			else
			{
				String[] cycle=new String[life.length+1];
				for(int i=0;i<cycle.length;i++)
				{
					if(i==0)
					{
						cycle[i]="default";
					}
					else
					{
						cycle[i]=life[i-1];
					}
				}
				for(int i=0;i<cycle.length;i++)
				{
					if(cycle[i]=="custom"||cycle[i].equals("custom"))
					{
						String val=request.getParameter("self");
						cycle[i]=val;
					}
				}
				app.setMessageLifeCycle(cycle);
			}
			this.appServiceImpl.updateApp(app);
		}catch(Exception e)
		{
			logger.debug("[FrontAction] updateApp fail ,exception is{}",e.getMessage());
			return "login";
		}
		request.setAttribute("appId", app.getId());
		logger.debug("[FrontAction] updateApp success");
		return SUCCESS;
	}
	
	/***
	 * 转入修改app
	 * @return
	 */
	public String goToUpdateApp()
	{
		logger.debug("[FrontAction] goToUpdateApp");
		String appId=request.getParameter("appId");
		if(appId==null||appId==""||appId.equals(""))
		{
			logger.debug("[FrontAction] goToUpdateApp,appId is null");
			return "error";
		}
		String result=request.getParameter("result");
		if(result==null||"".equals(result))
		{
			request.setAttribute("result", "1");
		}
		try{
		this.app=this.appServiceImpl.queryById(appId);
		String[] life=app.getMessageLifeCycle();
		if(life==null||life.length==0)
		{
			life=new String[]{"default"};
		}
		String custom="";
		boolean flag=false;
		for(int i=0;i<life.length;i++)
		{
			if(!life[i].equals("default")&&!life[i].equals("never"))
			{
				custom=life[i];
				request.setAttribute("custom_", custom);
				life[i]="custom";	
				flag=true;
			}
		}
		app.setMessageLifeCycle(life);

		if(flag)
		{
			request.setAttribute("display", "");
		}
		else
		{
			request.setAttribute("display", "none");
		}
		request.setAttribute("flag", flag);
		}
		catch(Exception e)
		{
			logger.debug("[FrontAction] goToUpdateApp,exception is {} ",e.getMessage());
			return "error";
		}
		return SUCCESS;
	}
	
	/***
	 * 查看应用详情
	 * @return
	 */
	public String appInfo()
	{
		logger.debug("[FrontAction] appInfo");
		String appId=request.getParameter("appId");
		if(appId==null||appId==""||appId.equals(""))
		{
			logger.debug("[FrontAction] appInfo,appId is null");
			return "error";
		}
		try{
		this.app=this.appServiceImpl.queryById(appId);
		Map<String,String> map=this.hasCustom(app);
		
		request.setAttribute("custom", map);
		}
		catch(Exception e)
		{
			logger.debug("[FrontAction] appInfo,exception is {} ",e.getMessage());
			return "error";
		}
		return SUCCESS;
	}
	//消息---------——
	
	/***
	 * 消息详细信息
	 * @return
	 */
	public String viewMessageInfo()
	{
		logger.debug("[FrontAction] viewMessageInfo");
		String id=request.getParameter("messageId");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail.equals(""))
		{
			logger.debug("[FrontAction] viewMessageInfo,not login");
			return "login";
		}
		if(id==null||id=="")
		{
			logger.debug("[FrontAction] viewMessageInfo,id is null");
			return ERROR;
		}
		message=this.messageServiceImpl.queryMessageById(id);
		List<String> status=new ArrayList<String>();
		status.add("1");
		status.add("2");
		List<App> list=this.appServiceImpl.queryAppList(0,10,userEmail,status);
		request.setAttribute("appList", list);
		if(message==null)
		{
			logger.debug("[FrontAction] viewMessageInfo,message is null");
			return ERROR;
		}
		request.setAttribute("message", message);
		logger.debug("[FrontAction] viewMessageInfo success");
		return SUCCESS;
	}
	
	/***
	 * 修改消息
	 * @return
	 */
	public String updateMessage()
	{
		logger.debug("[FrontAction] updateMessage");
		try{
			this.message.setCheck(0);
		this.messageServiceImpl.updateMessage(message);
		}catch(Exception e)
		{
			logger.debug("[FrontAction] updateMessage fail ,exception is{}",e.getMessage());
			return "login";
		}
		request.setAttribute("messageId", message.getId());
		logger.debug("[FrontAction] updateMessage success");
		return SUCCESS;
	}
	/***
	 * 转入修改app
	 * @return
	 */
	public String goToUpdateMessage()
	{
		logger.debug("[FrontAction] goToUpdateMessage");
		String messageId=request.getParameter("messageId");
		if(messageId==null||messageId==""||messageId.equals(""))
		{
			logger.debug("[FrontAction] goToUpdateApp,messageId is null");
			return "error";
		}
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail.equals(""))
		{
			logger.debug("[FrontAction] queryMessage,not login");
			return "login";
		}
		message=this.messageServiceImpl.queryMessageById(messageId);
		String appId=this.message.getAppId();
		String result=request.getParameter("result");
		if(result==null||"".equals(result))
		{
			request.setAttribute("result", "1");
		}
		App app=this.appServiceImpl.queryById(appId);
		Map<String,String> format=chansformMessageFormat(app.getMessageFormat());
		Map<String,String> life=chansformLifeCycle(app.getMessageLifeCycle());
		if(message==null)
		{
			logger.debug("[FrontAction] queryMessage,message is null");
			return ERROR;
		}
		List<String> status=new ArrayList<String>();
		status.add("1");
		status.add("2");
		List<App> list=this.appServiceImpl.queryAppList(0,10,userEmail,status);
		request.setAttribute("format", format);
		request.setAttribute("life", life);
		request.setAttribute("appList", list);
		this.request.setAttribute("app", app);
		request.setAttribute("message", message);
		logger.debug("[FrontAction] queryMessage success");
		return SUCCESS;
	}
	
	/***
	 * 消息信息
	 * @return
	 */
	public String messageInfo()
	{
		logger.debug("[FrontAction] messageInfo");
		String messageId=request.getParameter("messageId");
		if(messageId==null||messageId==""||messageId.equals(""))
		{
			logger.debug("[FrontAction] appInfo,messageId is null");
			return "error";
		}
		try{
			this.message=this.messageServiceImpl.queryMessageById(messageId);
		}
		catch(Exception e)
		{
			logger.debug("[FrontAction] appInfo,exception is {} ",e.getMessage());
			return "error";
		}
		return SUCCESS;
	}
	//-------------------------------------------
	
	public Map<String,String> hasCustom(App app)
	{
		Map<String,String> map=new HashMap<String,String>();
		map.put("default", "1天");
		if(app==null)
		{		
			map.put("custom", "自定义");
		}
		else
		{
			String[] value=app.getMessageLifeCycle();
			String custom="";
			for(int i=0;i<value.length;i++)
			{
				if(value[i]!=null&&!value[i].equals("default")&&!value[i].equals("never"))
				{
					custom=value[i];
					request.setAttribute("custom_", custom);
					value[i]="custom";
				}
			}
			this.app.setMessageLifeCycle(value);
			if(!custom.equals("")&&custom!="")
			{
				map.put("custom", custom+"天");
			}
			else
			{
				map.put("custom", "自定义");
			}
			
		}
		map.put("never", "永久");
		return map;
	}
	
	public void ajaxCount()
	{
		logger.debug("[FrontAction]start ajaxCount");
		int count=this.licenceServiceImpl.appCount();
		int appcount=this.licenceServiceImpl.queryAppFromInter();
		int last=count-appcount;
		PrintWriter out=null;
		try {
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			if(last<=0)
			{
			   out.write("0");	
			}
			else
			{
				out.write("1");
			}
		}
		catch(Exception e)
		{
			logger.debug("[FrontAction]ajaxCount,exception is {}",e.getMessage());
		}
		finally
		{
			if(out!=null)
			{
				out.flush();
				out.close();
			}
		}
		logger.debug("[FrontAction] end ajaxCount");
	}
	
	public void setAppServiceImpl(AppService appServiceImpl) {
		this.appServiceImpl = appServiceImpl;
	}

	public void setMessageServiceImpl(MessageService messageServiceImpl) {
		this.messageServiceImpl = messageServiceImpl;
	}

	public void setApp(App app) {
		this.app = app;
	}
	
	public App getApp()
	{
		return this.app;
	}

	public GroupMessage getMessage() {
		return message;
	}

	public void setMessage(GroupMessage message) {
		this.message = message;
	}

	public void setFrontServiceImpl(FrontService frontServiceImpl) {
		this.frontServiceImpl = frontServiceImpl;
	}

	public LicenceService getLicenceServiceImpl() {
		return licenceServiceImpl;
	}

	public void setLicenceServiceImpl(LicenceService licenceServiceImpl) {
		this.licenceServiceImpl = licenceServiceImpl;
	}
	
	
}
