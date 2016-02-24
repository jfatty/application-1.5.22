package com.oppo.push.action;

import java.io.PrintWriter;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oppo.push.service.ClientService;
import com.oppo.push.service.PushService;
import com.oppo.push.entity.Client;

public class PushAction extends BaseAction{

	/**
	 * @author zeyong.xia
	 * @date 2012-2-6
	 */
	private static final long serialVersionUID = 1L;

	private ClientService clientServiceImpl;
	
	private PushService pushServiceImpl;
	
	private String appId;
	
	private String appKey;
	
	private final static Logger logger = LoggerFactory
	.getLogger(PushAction.class);
	
	
	
	public void pushToClient()
	{
		logger.debug("[PushAction] pushToClient start");
		String message=request.getParameter("message");
		String key=request.getParameter("key");
		PrintWriter out=null;
		try{
			out=response.getWriter();
			if(message==null||message=="")
			{
				out.write("0");//消息为空
				return;
			}
			if(key==null||key=="")
			{
				out.write("-1");//key为空
				return;
			}
			Client client=this.clientServiceImpl.queryCid(key);
			if(client==null)
			{
				out.write("-2");//cid为空
				return;
			}
			
			boolean flag=this.pushServiceImpl.pushToClient(appId, appKey, client.getCid(), message.getBytes("UTF-8"));
			if(flag)
			{
				out.write("1");//cid为空
				return;
			}
			else
			{
				out.write("-3");//推送失败
				return;
			}
			
		}
		catch(Exception e)
		{
			logger.debug("[PushAction] pushToClient error is {}",e.getMessage());
		}
		finally
		{
			if(out!=null)
			{
				out.flush();
				out.close();
			}
		}
		
		logger.debug("[PushAction] pushToClient end");
	}

	@POST
	@Path("/register/client/")
	@Consumes("application/json")
	@Produces("application/json")
	public Response registerClient(@PathParam("userId")String userId,@PathParam("clientId")String clientId)
	{
		logger.debug("[registerClient] start registerClient");
		if(userId==null||userId.equals("")||clientId==null||clientId.equals(""))
		{
			logger.debug("[registerClient]  registerClient,userId or clientId is null");
			return Response.status(401).build();
		}
		Client client=this.clientServiceImpl.queryCid(clientId);
		if(client!=null)
		{
			this.clientServiceImpl.updateClient(client);
			logger.debug("[registerClient]  registerClient,userId is exist,so update");
			return Response.status(200).build();
		}
		else{
		client=new Client();
		client.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		client.setCid(clientId);
		client.setKey(userId);
		this.clientServiceImpl.addClient(client);
		logger.debug("[registerClient] end registerClient");
		return Response.status(200).build();
		}
	}
	
	public ClientService getClientServiceImpl() {
		return clientServiceImpl;
	}

	public void setClientServiceImpl(ClientService clientServiceImpl) {
		this.clientServiceImpl = clientServiceImpl;
	}

	public PushService getPushServiceImpl() {
		return pushServiceImpl;
	}

	public void setPushServiceImpl(PushService pushServiceImpl) {
		this.pushServiceImpl = pushServiceImpl;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	
	
}
