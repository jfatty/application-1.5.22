package com.babeeta.butterfly.application.management.action;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.service.FrontService;
import com.babeeta.butterfly.application.management.service.MessageService;

/***
 * 消息推送
 * @author zeyong.xia
 * @date 2011-12-9
 */
public class MessageAction extends BaseAction {

	/**
	 * @author zeyong.xia
	 * @date 2011-12-13
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory
	.getLogger(MessageAction.class);
	
	private FrontService frontServiceImpl;
	
	private MessageService messageServiceImpl;
	
	/***
	 * 初始化推送前信息
	 * @return
	 */
	public String initPush()
	{
		logger.debug("[MessageAction] initPush");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail=="")
		{
			logger.debug("[MessageAction] initPush,user not login");
			return "login";
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("userEmail", userEmail);
		List<App> list=this.frontServiceImpl.queryAppByEmail(map, 0, 10);
		request.setAttribute("appList", list);
		logger.debug("[MessageAction] initPush success");
		return SUCCESS;
	}

	/****
	 * 群发消息
	 * @return
	 */
	public String broadcast()
	{
		logger.debug("[MessageAction] broadcast");
		String userEmail=(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail=="")
		{
			logger.debug("[MessageAction] broadcast,user not login");
			return "login";
		}
		String appId=request.getParameter("app");
		String target=request.getParameter("target");
		String msg_format=request.getParameter("msg_format");
		String life=request.getParameter("life");
		String msg=request.getParameter("msg");
		GroupMessage gm=new GroupMessage();
		gm.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		gm.setAppName(appId);
		gm.setCheck(0);
		gm.setCreateAt(new Date());
		gm.setMessageAge(life);
		gm.setMessageContent(msg);
		gm.setMessageFormat(msg_format);
		gm.setPushTarget(target);
		gm.setUserEmail(userEmail);
		this.messageServiceImpl.addMessage(gm);
		logger.debug("[MessageAction] broadcast success");
		return SUCCESS;
	}
	
	/***
	 * 查询消息时效
	 * 
	 */
	public void queryMessageLife()
	{
		logger.debug("[MessageAction] queryMessageLife");
		String appId=request.getParameter("appId");
		System.out.println("**************"+appId);
		App app=null;
		try{
		app=this.frontServiceImpl.queryAppById(appId);
		}catch(Exception e)
		{
			logger.debug("[MessageAction] queryMessageLife ,exception is {}",e.getMessage());
		}
		String[] map=app.getMessageLifeCycle();
		StringBuffer sb=new StringBuffer();
		if(map!=null&&map.length>0)
		{
			Iterator<String> it=null;;
			while(it.hasNext())
			{
				String key=it.next();
				String value="";
				sb.append(key).append(":").append(value).append(",");
			}
		}
		else
		{
			sb.append("default:默认时效,");
		}
		PrintWriter out;
		try {
			out = response.getWriter();
		
		out.write(sb.toString().substring(0,sb.toString().length()-1));
		out.flush();
		out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("[MessageAction] queryMessageLife ,exception is {}",e.getMessage());
		}
		//request.setAttribute("map", map);
		//request.setAttribute("app", app);
		System.out.println("success");
	}
	
	/****
	 * 审核消息列表
	 * @return
	 */
	public String queryMessageList()
	{
		logger.debug("[MessageAction] queryMessageList");
		List<GroupMessage> list=this.messageServiceImpl.queryMessageList(0, 10);
		request.setAttribute("messageList", list);
		logger.debug("[MessageAction] queryMessageList success,size= {}",list.size());
		return SUCCESS;
	}
	
	public void setFrontServiceImpl(FrontService frontServiceImpl) {
		this.frontServiceImpl = frontServiceImpl;
	}

	public void setMessageServiceImpl(MessageService messageServiceImpl) {
		this.messageServiceImpl = messageServiceImpl;
	}
	
	
}
