package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageRouting.Credential;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.application.gateway.device.ServerContext;
import com.babeeta.butterfly.application.gateway.device.push.BroadcastService;
import com.babeeta.butterfly.application.gateway.device.push.MessagePusher;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor.ThreadPool;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.babeeta.butterfly.application.reliable.ReliablePushImpl;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;

public class AuthTunnelLet implements TunnelLet<Credential>, AuthTunnelLetMBean {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthTunnelLet.class);

	private final ServerContext serverContext;

	private final AtomicInteger requestCounter = new AtomicInteger();
	private final AtomicInteger successCounter = new AtomicInteger();
	private final AtomicInteger failedCounter = new AtomicInteger();
	private final AtomicInteger serviceUnavailableCounter = new AtomicInteger();

	private final int MAX_CONNECTION = 256;

	public final ThreadPoolExecutor authExecutor = new ThreadPoolExecutor(64,
			MAX_CONNECTION, 10,
			TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000));
	public final ThreadPoolExecutor reliablePushExecutor = new ThreadPoolExecutor(
			64, MAX_CONNECTION, 10,
			TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000));
	
	private final ThreadLocal<HttpClient> localHttpClient = new ThreadLocal<HttpClient>();
	
	private final static String ALL_BROADCAST_TAG="all";

	private final UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("Uncaught exception: ", e);

		}
	};

	private ThreadSafeClientConnManager httpClientConnectionManager;

	public AuthTunnelLet(ServerContext serverContext) {
		super();
		this.serverContext = serverContext;
		this.serverContext.registerMBean(
				"TunnelLet." + AuthTunnelLet.class.getSimpleName()
						+ ":name=AuthExecutor", new ThreadPool(authExecutor));
		this.serverContext.registerMBean(
				"TunnelLet." + AuthTunnelLet.class.getSimpleName()
						+ ":name=ReliablePushExecutor", new ThreadPool(
						reliablePushExecutor));
		this.serverContext.registerMBean("TunnelLet:name=AuthTunnelLet", this);
	}

	@Override
	public int getFailedCount() {
		return failedCounter.getAndSet(0);
	}

	@Override
	public int getHttpClientConnectionsInPool() {
		return httpClientConnectionManager.getConnectionsInPool();
	}

	@Override
	public int getRequestCount() {
		return requestCounter.getAndSet(0);
	}

	@Override
	public int getServiceUnavailableCount() {
		return serviceUnavailableCounter.getAndSet(0);
	}

	@Override
	public int getSuccessCount() {
		return successCounter.getAndSet(0);
	}

	@Override
	public void messageReceived(
			final TunnelContext tunnelContext, final TunnelData<Credential> data) {
		requestCounter.incrementAndGet();
		try {
			authExecutor.execute(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setName("Auth");
					Thread.currentThread().setUncaughtExceptionHandler(
							uncaughtExceptionHandler);
					onAuth(tunnelContext, data);
				}

			});
		} catch (RejectedExecutionException e) {
			serviceUnavailableCounter.incrementAndGet();
			logger.error("[{}]Auth failed due to over load.", tunnelContext
					.getChannel().getId());
			tunnelContext.getChannel().write(
					new TunnelData<MessageLite>(data.tag,
							MessageRouting.MessageCMD.RESPONSE.getNumber(),
							MessageRouting.Response
									.newBuilder()
									.setStatus("SERVICE_UNAVAILABLE")
									.build()));
		}
	}

	private String convertToJson(Credential credential) {
		return "{\"id\":\"" + credential.getId() + "\",\"secureKey\":\""
				+ credential.getSecureKey() + "\"}";
	}

	private void doOk(final TunnelContext tunnelContext,
			final TunnelData<Credential> data, Credential credential,
			HttpEntity entity) throws IOException {
		JSONObject obj = JSONObject.fromObject(EntityUtils
				.toString(entity));
		if ("OK".equalsIgnoreCase(obj.get("status").toString())) {
			successCounter.incrementAndGet();
			tunnelContext.setDeviceId(data.obj.getId());
			MessagePusher.getDefaultInstance().register(
					data.obj.getId(), tunnelContext.getChannel());
			reportToGateway(data.obj.getId());
			tunnelContext.getChannel()
					.write(
							new TunnelData<MessageLite>(data.tag, 135,
									Response
											.newBuilder()
											.setStatus("SUCCESS")
											.build()));
			logger.debug("[{}] Sent success to client. [{}]",
					tunnelContext.getChannel().getId(), obj.toString());
			reliablePush(tunnelContext, data);
		} else {
			failedCounter.incrementAndGet();
			logger.debug("[{}] Device Auth failed. [{}:{}]"
					, new Object[] { tunnelContext.getChannel().getId(),
							credential.getId(), credential.getSecureKey() });
			sendError(tunnelContext, data);
		}
	}

	private HttpClient getHttpClient() {
		HttpClient client = localHttpClient.get();
		if (client == null) {

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(
						new Scheme("http", 80, PlainSocketFactory
								.getSocketFactory()));

			ThreadSafeClientConnManager httpClientConnectionManager = new ThreadSafeClientConnManager(
					schemeRegistry);

			httpClientConnectionManager.setMaxTotal(MAX_CONNECTION);
			httpClientConnectionManager.setDefaultMaxPerRoute(MAX_CONNECTION);
		
			
			client = new DefaultHttpClient(httpClientConnectionManager);
			localHttpClient.set(client);
		}
		return client;
	}

	private void onAuth(final TunnelContext tunnelContext,
			final TunnelData<Credential> data) {
		logger.info("[{}] Auth from [{}]", data.obj.getId(),
				tunnelContext
						.getChannel().getRemoteAddress());
		long timestamp = System.currentTimeMillis();
		Credential credential = data.obj;
		String requestBody = convertToJson(credential);

		HttpPost httpPost = new HttpPost("http://accounts.dev/api/auth");
		httpPost.setHeader(new BasicHeader("Content-type",
				"application/json"));
		httpPost.setEntity(new ByteArrayEntity(requestBody.getBytes()));
		HttpEntity entity = null;
		try {
			HttpResponse httpResponse = getHttpClient().execute(
					httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();
			logger.debug("[{}]Got auth response: {}, time:{}ms",
					new Object[] { tunnelContext.getChannel().getId(), status,
							(System.currentTimeMillis() - timestamp) });
			entity = httpResponse.getEntity();
			if (HttpStatus.SC_OK == status) {
				doOk(tunnelContext, data, credential, entity);
			} else {
				logger.error(
						"Unexpectant response status :{} while auth on {}.",
						status, "accounts.dev");
				sendError(tunnelContext, data);
			}
		} catch (IOException e) {
			logger.error("Error occured while request [{}],{}",
					"accounts.dev",
					e.getMessage());
			sendError(tunnelContext, data);
		} finally {
			if (entity != null) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException ignore) {
				}
			}
			httpPost.abort();
		}
	}

	private void onReliablePush(final TunnelContext tunnelContext,
			final TunnelData<Credential> data) {
		Thread.currentThread().setName("Reliable-Push");
		ReliablePush reliablePush = ReliablePushImpl
				.getDefaultInstance();
		List<MessageRouting.Message> list = reliablePush
				.getMessagesList(data.obj.getId());
		logger.debug("[{}] ReliablePush sending [{}]",
				data.obj.getId(), list.size());
		for (MessageRouting.Message message : list) {
			tunnelContext.getChannel().write(
					new TunnelData<MessageLite>(0, 129, message));
			logger.info("[{}] [{}] msg has been redelivered to {}",new Object[]{message.getUid(),message.getDate(),data.obj.getId()});
		}
		
		List<Map> subscriptionList=getSubscriptionList(data.obj.getId());
		
		if(subscriptionList==null || subscriptionList.size()==0){
			logger.debug("not found subscription for {}",data.obj.getId());
			return;
		}
		
		logger.debug("find {} subscription for device {}",subscriptionList.size(),data.obj.getId());
		
		reliablePushBroactcastMessage(tunnelContext, subscriptionList);
	}
	
	private void reliablePush(final TunnelContext tunnelContext,
			final TunnelData<Credential> data) {
		try {
			reliablePushExecutor.execute(new Runnable() {
				@Override
				public void run() {
					onReliablePush(tunnelContext, data);
				}

			});
		} catch (RejectedExecutionException e) {
			logger.error("[{}][{}]Reliable push failed due to overload.",
					tunnelContext.getChannel().getId(),
					tunnelContext.getDeviceId());
		}
	}
	
	private void reliablePushBroactcastMessage(TunnelContext tunnelContext, List<Map> subscriptionList){
		
		for(Map subscription:subscriptionList){
			try {
				String aid=(String) subscription.get("aid");
				String cid=(String) subscription.get("cid");
				
				BroadcastService.getDefaultInstance().register(aid, ALL_BROADCAST_TAG, tunnelContext.getDeviceId(), cid);
				
				List<String> tagList=getTagList(cid, aid);
				
				if(tagList==null || tagList.size()==0){
					logger.debug("tag list is empty for {}",cid);
					continue;
				}
				
				logger.debug("find {} tag for clientId {}",tagList.size(),cid);
				
				for(String tag:tagList){
					BroadcastService.getDefaultInstance().register(aid, tag, tunnelContext.getDeviceId(), cid);
					
					List<MessageRouting.Message> messageRoutingList=ReliablePushImpl.getDefaultInstance().getDeliveringBroadcastMessageList(aid, tag,cid);
					
					if(messageRoutingList==null || messageRoutingList.size()==0){
						logger.debug("not found message for tag {} clientId {}",tag,cid);
						continue;
					}
					
					logger.debug("find {} broadcast msg for tag {} clientId {}",new Object[]{messageRoutingList.size(),tag,cid});
					
					for (MessageRouting.Message message : messageRoutingList) {
						tunnelContext.getChannel().write(
								new TunnelData<MessageLite>(0, 129, message.toBuilder().setTo(cid+"."+aid+"@dev").build()));
					}
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				continue;
			}
		}
	}
	
	private List<String> getTagList(String cid,String aid){
		HttpGet httpGet=new HttpGet("http://tag.app/api/get-client-tag-list/"+aid+"/"+cid);
		HttpResponse httpResponse=null;
		
		try {
			httpResponse=getHttpClient().execute(httpGet);
			String responseStr=EntityUtils.toString(httpResponse.getEntity());
			
			JSONArray jsonArray = JSONArray.fromObject(responseStr);

			return (List<String>) JSONArray.toList(jsonArray, String.class);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}finally{
			if(httpResponse!=null && httpResponse.getEntity()!=null){
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
			httpGet.abort();
		}
	}
	
	private List<Map> getSubscriptionList(String did){
		HttpGet httpGet=new HttpGet("http://subscription.dev/api/list/"+did);
		HttpResponse httpResponse=null;
		
		try {
			httpResponse=getHttpClient().execute(httpGet);
			String responseStr=EntityUtils.toString(httpResponse.getEntity());
			
			JSONArray jsonArray = JSONArray.fromObject(responseStr);

			return (List<Map>) JSONArray.toList(jsonArray, HashMap.class);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}finally{
			if(httpResponse!=null && httpResponse.getEntity()!=null){
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
			httpGet.abort();
		}
	}

	private void reportToGateway(String id) {
		logger.debug("[{}]Report to gateway.dev.", id);
		Message msg = Message
				.newBuilder()
				.setContent(ByteString.copyFromUtf8(id))
				.setDate(System.currentTimeMillis())
				.setFrom(
						"report@" + serverContext.messageServiceAddress)
				.setTo("update@gateway.dev")
				.setUid(UUID.randomUUID().toString()
						.replaceAll("\\-", ""))
				.build();
		serverContext.messageSender.send(msg);
	}

	private void sendError(final TunnelContext tunnelContext,
			final TunnelData<Credential> data) {
		tunnelContext.getChannel().write(
				new TunnelData<MessageLite>(data.tag, 135, Response
						.newBuilder().setStatus("ERROR").build()));
	}
}
