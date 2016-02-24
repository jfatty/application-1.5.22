package com.babeeta.butterfly.application.management.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.dao.BadWordDao;
import com.babeeta.butterfly.application.management.dao.MessageDao;
import com.babeeta.butterfly.application.management.entity.BadWords;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.service.MessageService;

public class MessageServiceImpl implements MessageService {

	private MessageDao<GroupMessage,String> messageDaoImpl;
	
	private BadWordDao<BadWords, String> badWordsDaoImpl;
	
	private ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<HttpClient>();
	
	private HttpRequestRetryHandler httpRequestRetryHandler;
	
	private String pushHost;//推送主机
	
	private final static Logger logger = LoggerFactory
	.getLogger(MessageServiceImpl.class);
	
	public MessageServiceImpl()
	{
		httpRequestRetryHandler = new HttpRequestRetryHandler() {

		    public boolean retryRequest(
		            IOException exception, 
		            int executionCount,
		            HttpContext context) {
		    	HttpRequest httpRequest=(HttpRequest)context.getAttribute(ExecutionContext.HTTP_REQUEST);
		    	String uri=httpRequest.getRequestLine().getUri();
		    	String methodName=httpRequest.getRequestLine().getMethod();
		    	
		        if (executionCount >= 5) {
		            // Do not retry if over max retry count
		        	logger.error("[{}] [{}] [{}] [{}] have been retry 5 times,stop retry.",new Object[]{uri,methodName,exception.getClass().getName(),exception.getMessage()});
		            return false;
		        }
		        if (exception instanceof NoHttpResponseException) {
		            // Retry if the server dropped connection on us
		        	logger.error("[{}] [{}] [{}] [{}] http retry",new Object[]{uri,methodName,exception.getClass().getName(),exception.getMessage()});
		            return true;
		        }
		    	if((exception instanceof SocketException) && exception.getMessage().startsWith("Connection reset")){
		    		logger.error("[{}] [{}] [{}] [{}] Connection reset retry",new Object[]{uri,methodName,exception.getClass().getName(),exception.getMessage()});
					return true;
				}
		        
		        boolean idempotent = !(httpRequest instanceof HttpEntityEnclosingRequest); 
		        if (idempotent) {
		            // Retry if the request is considered idempotent
		        	logger.error("[{}] [{}] idempotent retry",new Object[]{uri,methodName});
		            return true;
		        }
		        return false;
		    }

		};
	
	
	}
	
	/***
	 * 查询消息
	 * @param id
	 * @return
	 */
	public GroupMessage queryMessageById(String id)
	{
		return this.messageDaoImpl.findById(id);
	}
	
	/***
	 * 更新消息
	 * @param message
	 * @return
	 */
	public GroupMessage updateMessage(GroupMessage message)
	{
		this.messageDaoImpl.updateMessage(message);
		return message;
	}
	
	/****
	 * 查询消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryMessageList(int offset,int limit)
	{
		return this.messageDaoImpl.queryCheckMessageList(offset, limit);
	}
	
	/***
	 * 新增群发消息
	 * @param message
	 * @return
	 */
	public GroupMessage addMessage(GroupMessage message)
	{
		this.messageDaoImpl.save(message);
		return message;
	}
	
	/****
	 * 查询消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryMyMessageList(int offset,int limit,Map<String,Object> map)
	{
		return this.messageDaoImpl.queryList(offset, limit, map);
	}
	
	/***
	 * 按条件查询总数
	 * @param map
	 * @return
	 */
	public int queryMessageCount(Map<String,Object> map)
	{
		return this.messageDaoImpl.queryCount(map);
	}
	/***
	 * 查询未审核的message总数
	 * @return
	 */
	public int queryNotCheckMessageCount()
	{
		return this.messageDaoImpl.queryNotCheckMessageCount();
	}
	
	/***
	 * 新增敏感词
	 * @param word
	 * @return
	 */
	public boolean addWord(BadWords word) throws Exception 
	{
		this.badWordsDaoImpl.save(word);
		return true;
	}
	
	/***
	 * 删除敏感词
	 * @param word
	 */
	public void deleteWord(String word)
	{
		this.badWordsDaoImpl.deleteByEntityId(word);
	}
	
	/***
	 * 判断敏感词是否存在
	 * @param word
	 * @return
	 */
	public boolean existsWord(String word)
	{
		return this.badWordsDaoImpl.existsWord(word);
	}
	
	/***
	 * 分页查询敏感词列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<BadWords> queryWordList(int offset,int limit)
	{
		return this.badWordsDaoImpl.queryList(offset, limit);
	}
	
	/***
	 * 查询敏感词总数
	 * @param offset
	 * @param limit
	 * @return
	 */
	public int queryWordCount()
	{
		return this.badWordsDaoImpl.queryCount();
	}
	
	/***
	 * 广播接口
	 * @param appId
	 * @param appKey
	 * @param message
	 */
	public boolean broadcast(String appId,String appKey,GroupMessage message)
	{

		logger.debug(
				"[MessageServiceImpl] broadcast, recipient is all ");
		String life=message.getMessageAge();
		if(life=="default"||life.equals("default"))
		{
			life="1";
		}
		HttpPost httpPost = new HttpPost("/1/api/message/broadcast/all/delay=0;life="+life+";type="+message.getMessageFormat());
		httpPost.setHeader("Content-type", "application/octet-stream");
		String str=setAuthHeader(appId,appKey);
		httpPost.addHeader("Authorization", str);
		HttpResponse httpResponse = null;
		try {
			ByteArrayEntity byteArray = new ByteArrayEntity(message.getMessageContent().getBytes("UTF-8"));
			httpPost.setEntity(byteArray);
			httpResponse = invoke(httpPost);
			logger
					.debug(
							"[MessageServiceImpl] broadcast, responseString  is {} ",
							httpResponse.getStatusLine());
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				return true;
			}
			else
			{
				return false;
			}

		} catch (Exception e) {
			logger.error("[MessageServiceImpl] "
					+ httpPost.getURI().getPath() + " failed.", e);
			return false;
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error("[MessageServiceImpl]"+e.getMessage(), e);
				}
			}
			httpPost.abort();
		}
	
	}
	public HttpResponse invoke(HttpUriRequest httpUriRequest)
	throws ClientProtocolException, IOException {
		return getHttpClient().execute(new HttpHost(this.getPushHost()), httpUriRequest);
	}
	
	private HttpClient getHttpClient() {
		// TODO 需要优化
		if (httpClientThreadLocal.get() == null) {

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(
					new Scheme("http", 80, PlainSocketFactory
							.getSocketFactory()));

			ThreadSafeClientConnManager threadSafeClientConnManager = new ThreadSafeClientConnManager(
					schemeRegistry);
			threadSafeClientConnManager.setMaxTotal(800);
			threadSafeClientConnManager.setDefaultMaxPerRoute(800);

			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(
					threadSafeClientConnManager);
			
			defaultHttpClient.setHttpRequestRetryHandler(httpRequestRetryHandler);
			
			httpClientThreadLocal.set(defaultHttpClient);
		}

		return httpClientThreadLocal.get();
	}
	private String setAuthHeader(String appId,String appKey) {

		byte[] token = null;
		String authorization = null;
		try {
			token = (appId + ":" + appKey).getBytes("utf-8");
			authorization = "Basic "
					+ new String(Base64.encodeBase64(token), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return authorization;
		//httpUriRequest.addHeader("Authorization", authorization);

	}
	public void setMessageDaoImpl(MessageDao<GroupMessage,String> messageDaoImpl) {
		this.messageDaoImpl = messageDaoImpl;
	}

	public void setBadWordsDaoImpl(BadWordDao<BadWords, String> badWordsDaoImpl) {
		this.badWordsDaoImpl = badWordsDaoImpl;
	}

	public String getPushHost() {
		return pushHost;
	}

	public void setPushHost(String pushHost) {
		this.pushHost = pushHost;
	}
	
	

}
