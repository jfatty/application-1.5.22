package com.babeeta.butterfly.application.management.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.BadWords;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.entity.User;
import com.babeeta.butterfly.application.management.service.AppService;
import com.babeeta.butterfly.application.management.service.LicenceService;
import com.babeeta.butterfly.application.management.service.ManagerService;
import com.babeeta.butterfly.application.management.service.MessageService;
import com.babeeta.butterfly.application.management.service.UserService;

/***
 * 后台管理
 * @author zeyong.xia
 * @date 2011-12-13
 */
public class ManagerAction extends BaseAction{

	/**
	 * @author zeyong.xia
	 * @date 2011-12-27
	 */
	private static final long serialVersionUID = 1L;


	private final static Logger logger = LoggerFactory
	.getLogger(MessageAction.class);
	
	
	private MessageService messageServiceImpl;
	
	private AppService appServiceImpl;
	
	private ManagerService managerServiceImpl;
	
	private UserService userServiceImpl;
	
	private LicenceService licenceServiceImpl;
	
	private App app;
	
	private GroupMessage message;
	
	/****
	 * 审核消息列表
	 * @return
	 */
	public String queryCheckMessageList()
	{
		logger.debug("[ManagerAction] queryMessageList");
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("check", 0);
		int totalCount=this.messageServiceImpl.queryMessageCount(map);
		int offset=this.setPage(totalCount, request);
		List<GroupMessage> list=this.messageServiceImpl.queryMyMessageList(offset, ROWCOUNT, map);
		request.setAttribute("messageList", list);
		logger.debug("[MessageAction] queryMessageList success,size= {}",list.size());
		return SUCCESS;
	}
	
	/****
	 * 消息管理
	 * @return
	 */
	public String messageManager()
	{
		logger.debug("[ManagerAction] messageManager");
		Map<String,Object> map=new HashMap<String,Object>();
		String userEmail=request.getParameter("userEmail");
		if(userEmail==null||userEmail.equals(""))
		{
			logger.debug("[ManagerAction] messageManager,userEmail is null");
			return "error";
		}
		map.put("userEmail",userEmail);
		int totalCount=this.messageServiceImpl.queryMessageCount(map);
		int offset=this.setPage(totalCount, request);
		List<GroupMessage> list=this.messageServiceImpl.queryMyMessageList(offset, 10, map);
		request.setAttribute("messageList", list);
		request.setAttribute("userEmail", userEmail);
		logger.debug("[MessageAction] queryMessageList success,size= {}",list.size());
		return SUCCESS;
	}
	/***
	 * 应用管理
	 * @return
	 */
	public String appManager()
	{
		logger.debug("[ManagerAction] appManager");
		Map<String,Object> map=new HashMap<String,Object>();
		String userEmail=request.getParameter("userEmail");
		if(userEmail==null||userEmail.equals(""))
		{
			logger.debug("[ManagerAction] appManager,userEmail is null");
			return "error";
		}
		map.put("userEmail",userEmail);
		int totalCount=this.appServiceImpl.queryAppCount(map);
		int offset=this.setPage(totalCount, request);
		List<App> list=this.appServiceImpl.queryAppList(map, offset, ROWCOUNT);
		request.setAttribute("userEmail",userEmail);
		request.setAttribute("appList", list);
		return SUCCESS;
	}

	/***
	 * 审核应用列表
	 * @return
	 */
	public String queryCheckAppList()
	{
		logger.debug("[ManagerAction] queryAppList");
		Map<String,Object> map=new HashMap<String,Object>();
		String userEmail=request.getParameter("userEmail");
		if(userEmail!=null&&!userEmail.equals(""))
		{
			map.put("userEmail",userEmail);
		}
		else
		{
			map.put("checkStatus", "0");
		}
		int totalCount=this.appServiceImpl.queryAppCount(map);
		int offset=this.setPage(totalCount, request);
		List<App> list=this.appServiceImpl.queryAppList(map, offset, ROWCOUNT);
		request.setAttribute("appList", list);
		return SUCCESS;
	}

	
	/***
	 * 应用详细信息
	 * @return
	 */
	public String queryApp()
	{
		logger.debug("[ManagerAction] queryApp");
		String id=request.getParameter("appId");
		if(id==null||id=="")
		{
			logger.debug("[ManagerAction] queryApp,id is null");
			return ERROR;
		}
		app=this.appServiceImpl.queryById(id);
		if(app==null)
		{
			logger.debug("[ManagerAction] queryApp,app is null");
			return ERROR;
		}
		Map<String,String> map=this.hasCustom(app);
		
		request.setAttribute("custom", map);
		request.setAttribute("app", app);
		logger.debug("[ManagerAction] queryApp success");
		return SUCCESS;
	}

	/***
	 * 消息详细信息
	 * @return
	 */
	public String queryMessage()
	{
		logger.debug("[ManagerAction] queryMessage");
		String id=request.getParameter("messageId");
		if(id==null||id=="")
		{
			logger.debug("[ManagerAction] queryMessage,id is null");
			return ERROR;
		}
		message=this.messageServiceImpl.queryMessageById(id);
		if(message==null)
		{
			logger.debug("[ManagerAction] queryMessage,message is null");
			return ERROR;
		}
		String appId=this.message.getAppId();
		if(appId==null||appId.equals(""))
		{
			appId=this.message.getAppName();
		}
		App app=this.appServiceImpl.queryById(appId);
		if(app==null)
		{
			logger.debug("[FrontAction] queryMessage,app is null");
			return ERROR;
		}
		request.setAttribute("app", app);
		request.setAttribute("message", message);
		logger.debug("[ManagerAction] queryMessage success");
		return SUCCESS;
	}
	
	public String managerInfo()
	{
		logger.debug("[ManagerAction] managerInfo");
		int appCount=this.appServiceImpl.queryNotCheckAppCount();
		int messageCount=this.messageServiceImpl.queryNotCheckMessageCount();
		request.setAttribute("appCount", appCount);
		request.setAttribute("messageCount", messageCount);
		logger.debug("[ManagerAction] managerInfo success");
		return SUCCESS;
	}
	
	/****
	 * 审核应用申请
	 * @return
	 */
	public String checkApp()
	{
		logger.debug("[ManagerAction] checkapp");
		String check=request.getParameter("check_value");
		int count=this.licenceServiceImpl.appCount();
		int appCount=this.licenceServiceImpl.queryAppFromInter();
		int last=count-appCount;
		if(last<=0)
		{
			logger.debug("[ManagerAction] checkapp,appCount is outOfNum");
			return "noCount";
		}
		if(app==null)
		{
			logger.debug("[ManagerAction] checkapp,app is null");
			return "error";
		}
		if(check=="1"||check.equals("1"))
		{
			app.setCheckStatus("1");
			String result=this.appServiceImpl.registerApp(app);
			logger.debug("[ManagerAction] checkapp,register result is {}",result);
			String[] ak=result.split("\\:");
			if(ak==null||ak.length!=2)
			{
				logger.debug("[ManagerAction] checkapp,registerApp error");
				return "error";
			}
			app.setAppId(ak[0]);
			app.setAppKey(ak[1]);
		}
		else if(check=="2"||check.equals("2"))
		{
			app.setCheckStatus("2");
			String[] life=new String[]{"default"};
			app.setMessageLifeCycle(life);
			String result=this.appServiceImpl.registerApp(app);
			logger.debug("[ManagerAction] checkapp,register result is {}",result);
			String[] ak=result.split("\\:");
			if(ak==null||ak.length!=2)
			{
				logger.debug("[ManagerAction] checkapp,registerApp error");
				return "error";
			}
			app.setAppId(ak[0]);
			app.setAppKey(ak[1]);
		}
		else
		{
			app.setCheckStatus("-1");
		}
	    String checkView=request.getParameter("app.checkView");
	    //String view =app.getCheckView();
	    if(checkView==null)
	    {
	    	checkView ="";
	    }
	    //checkView=view +" "+checkView;
	    app.setCheckView(checkView+"\r\n");
		this.appServiceImpl.updateApp(app);
		logger.debug("[ManagerAction] checkApp success");
		return SUCCESS;
	}
	
	/****
	 * 审核消息
	 * @return
	 */
	public String checkMessage()
	{
		logger.debug("[ManagerAction] checkMessage");
		String check=request.getParameter("message_value");
		System.out.println("message_value= "+check);
		String msgId=request.getParameter("msgId");
		if(msgId!=null&&!msgId.equals(""))
		{
			message=this.managerServiceImpl.queryMessageById(msgId);
		}
		if(check=="1"||check.equals("1"))
		{
			message.setCheck(1);
			String aid=message.getAppId();
			if(aid==null||aid.equals(""))
			{
				logger.debug("[ManagerAction] checkMessage fail, aid is null");
				return "error";
			}
			App app=this.appServiceImpl.queryById(aid);
			String appId=app.getAppId();
			String appKey=app.getAppKey();
			if(appId==null||appKey==null||appId.equals("")||appKey.equals(""))
			{
				logger.debug("[ManagerAction] checkMessage fail, appId or appKey is null");
				return "error";
			}
			logger.debug("[ManagerAction] start broadcast , appId is {} appKey is {}",appId,appKey);
			boolean flag=this.messageServiceImpl.broadcast(appId, appKey, message);
			if(flag==false)
			{
				logger.debug("[ManagerAction] broadcast fail ");
				message.setCheck(-2);
			}
		}
		else
		{
			message.setCheck(-1);
		}
		this.messageServiceImpl.updateMessage(message);
		logger.debug("[ManagerAction] checkMessage success");
		return SUCCESS;
	}
	
	
	/***
	 * 新增敏感词
	 * @return
	 */
	public String addWord()
	{
		logger.debug("[ManagerAction] addWord");
		String words="";
		try {
			words=request.getParameter("words");
			logger.debug("[ManagerAction] addWord,word is {}",words);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("[ManagerAction] addWord,word is null");
			return "error";
		}
		words=words.replaceAll("[~,!,@,#,$,%,^,&,\\*,\\(,\\),\\-,——,\\+,=,:,\\?,\\？,￥]", "");
		if(words!=null)
		{
			BadWords word=new BadWords();
			word.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			word.setCreateAt(new Date());
			word.setWord(words);
			try {
				this.messageServiceImpl.addWord(word);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.debug("[ManagerAction] addWord error ,Exception is {}",e.getMessage());
				return "error";
			}
		}
		else
		{
			logger.debug("[ManagerAction] addWord, error");
			return ERROR;
		}
		logger.debug("[ManagerAction] addWord,success");
		return SUCCESS;
	}
	
	/***
	 * 查询敏感词列表
	 * @return
	 */
	public String queryWordsList()
	{
		logger.debug("[ManagerAction] queryWordsList");
		String result=request.getParameter("result");
		if(result==null)
		{
			result="";
		}
		int totalCount=this.managerServiceImpl.queryWordCount();
		int offset =this.setPage(totalCount, request);
		List<BadWords> list=this.managerServiceImpl.queryWordList(offset, ROWCOUNT);
		request.setAttribute("words", list);
		logger.debug("[ManagerAction] queryWordsList success");
		request.setAttribute("result", result);
		return SUCCESS;
	}
	
	/***
	 * 查询敏感词列表
	 * @return
	 */
	public String deleteWord()
	{
		logger.debug("[ManagerAction] deleteWord");
		String wordId=request.getParameter("wordId");
		this.messageServiceImpl.deleteWord(wordId);
		logger.debug("[ManagerAction] deleteWord success");
		return SUCCESS;
	}
	
	/***
	 * 新增敏感词前验证
	 * @param words
	 * @return
	 */
	
	public void ajaxVilidateWords()
	{
		logger.debug("[ManagerAction] ajaxVilidateWords");
		String word=request.getParameter("word");
		PrintWriter out=null;
		try {
			out=response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		xml+="<result>";
		if(word==null||word.equals(""))
		{
			xml+="1</result>";
			out.write("1");
			out.close();
			return;
		}
		logger.debug("[ManagerAction] ajaxVilidateWords,word is {}",word);
	    boolean flag=this.messageServiceImpl.existsWord(word);
	    if(flag)
	    {
	    	xml+="1</result>";
	    	out.write("1");
			out.close();
			return;
	    }
	    else
	    {
	    	xml+="0</result>";
	    	out.write("0");
			out.close();
			return;
	    }
	}
	
	/***
	 * 查询公司列表
	 * @return
	 */
	public String queryCompanyList()
	{
		logger.debug("[ManagerAction] queryCompanyList");
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("roleName", "user");
		int totalCount=this.managerServiceImpl.queryUserCount(map);
		int offset =this.setPage(totalCount, request);
		List<User> list=this.managerServiceImpl.queryUserList(offset, ROWCOUNT, map);
		String type=request.getParameter("type");
		request.setAttribute("type", type);
		request.setAttribute("userList", list);
		logger.debug("[ManagerAction] queryCompanyList success");
		return SUCCESS;
	}
	
	/***
	 * 验证消息是否有敏感词
	 */
	public void ajaxCheckText()
	{
		logger.debug("[ManagerAction] ajaxCheckText");
		
		String msg=request.getParameter("msg");
		if(msg==null||msg.equals(""))
		{
			logger.debug("[ManagerAction] ajaxCheckText,msg is null");
			return ;
		}
		boolean flag=this.managerServiceImpl.filterWord(msg);
		PrintWriter out=null;
		try {
			out=response.getWriter();
			if(flag)
			{
				out.write("1");
				out.close();
				logger.debug("[ManagerAction] ajaxCheckText,true");
			}
			else
			{
				out.write("0");
				out.close();
				logger.debug("[ManagerAction] ajaxCheckText,false");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.debug("[ManagerAction] ajaxCheckText,Exception is {}",e.getMessage());
		}
		
	}
	
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
					//value[i]="custom";
				}
			}
			this.app.setMessageLifeCycle(value);
			if(!custom.equals("")&&custom!="")
			{
				map.put(custom, custom+"天");
			}
			else
			{
				map.put("custom", "自定义");
			}
			
		}
		map.put("never", "永久");
		return map;
	}
	
	public String checkPageApp()
	{
		logger.debug("[ManagerAction] checkPageApp");
		String check=request.getParameter("check_value");
		String appId =request.getParameter("appId");
		if(appId==null||appId.equals(""))
		{
			logger.debug("[ManagerAction] checkPageApp,appId is null");
			return "error";
		}
		int count=this.licenceServiceImpl.appCount();
		int appCount=this.licenceServiceImpl.queryAppFromInter();
		int last=count-appCount;
		if(last<=0)
		{
			logger.debug("[ManagerAction] checkapp,appCount is outOfNum");
			return "noCount";
		}
		this.app=this.appServiceImpl.queryById(appId);
		if(app==null)
		{
			logger.debug("[ManagerAction] checkPageApp,app is null");
			return "error";
		}
		if(check=="1"||check.equals("1"))
		{
			app.setCheckStatus("1");
			String result=this.appServiceImpl.registerApp(app);
			logger.debug("[ManagerAction] checkPageApp,register result is {}",result);
			String[] ak=result.split("\\:");
			if(ak==null||ak.length!=2)
			{
				logger.debug("[ManagerAction] checkPageApp,registerApp error");
				return "error";
			}
			app.setAppId(ak[0]);
			app.setAppKey(ak[1]);
		}
		else if(check=="2"||check.equals("2"))
		{
			app.setCheckStatus("2");
			String[] life=new String[]{"default"};
			app.setMessageLifeCycle(life);
			String result=this.appServiceImpl.registerApp(app);
			logger.debug("[ManagerAction] checkPageApp,register result is {}",result);
			String[] ak=result.split("\\:");
			if(ak==null||ak.length!=2)
			{
				logger.debug("[ManagerAction] checkPageApp,registerApp error");
				return "error";
			}
			app.setAppId(ak[0]);
			app.setAppKey(ak[1]);
		}
		else
		{
			app.setCheckStatus("-1");
		}
	    String checkView=request.getParameter("app.checkView");
	    //String view =app.getCheckView();
	    if(checkView==null)
	    {
	    	checkView ="";
	    }
//	    String view =this.app.getCheckView();
//		if(view==null)
//		{
//			view="";
//		}
	    checkView=checkView+"\r\n";
	    app.setCheckView(checkView);
		this.appServiceImpl.updateApp(app);
		logger.debug("[ManagerAction] checkPageApp success");
		return SUCCESS;	
	}
	
	public String setManager()
	{
		logger.debug("[ManagerAction] setManager");
		String[] userIds=request.getParameterValues("manager");
		if(userIds!=null&&userIds.length>0)
		{
			User user=null;
			for(String userId:userIds)
			{
				user=this.userServiceImpl.queryUserByEmail(userId);
				user.setRoleName("manager");
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("roleName", "manager");
				this.managerServiceImpl.updateUser(user,map);
			}
		}
		return SUCCESS;
	}
	
	public String updateWord()
	{
		logger.debug("[ManagerAction] updateWord");
		String wid=request.getParameter("wid");
		String result=request.getParameter("uword");
		if(wid==null||wid.equals(""))
		{
			logger.debug("[ManagerAction] updateWord error wordId is null");
			return "login";
		}
		BadWords word=this.managerServiceImpl.queryWordsById(wid);
		word.setWord(result);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("word", result);
		this.managerServiceImpl.updateWord(word,map);
		logger.debug("[ManagerAction] updateWord success");
		return SUCCESS;
	}
	
	public void setMessageServiceImpl(MessageService messageServiceImpl) {
		this.messageServiceImpl = messageServiceImpl;
	}


	public void setAppServiceImpl(AppService appServiceImpl) {
		this.appServiceImpl = appServiceImpl;
	}


	public App getApp() {
		return app;
	}


	public void setApp(App app) {
		this.app = app;
	}


	public GroupMessage getMessage() {
		return message;
	}


	public void setMessage(GroupMessage message) {
		this.message = message;
	}


	public void setManagerServiceImpl(ManagerService managerServiceImpl) {
		this.managerServiceImpl = managerServiceImpl;
	}

	public void setUserServiceImpl(UserService userServiceImpl) {
		this.userServiceImpl = userServiceImpl;
	}

	public LicenceService getLicenceServiceImpl() {
		return licenceServiceImpl;
	}

	public void setLicenceServiceImpl(LicenceService licenceServiceImpl) {
		this.licenceServiceImpl = licenceServiceImpl;
	}
	
	
}
