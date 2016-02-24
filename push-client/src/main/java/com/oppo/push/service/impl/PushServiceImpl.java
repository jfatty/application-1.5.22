package com.oppo.push.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

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


import com.oppo.push.service.PushService;

public class PushServiceImpl implements PushService{

	private ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<HttpClient>();
	
	private HttpRequestRetryHandler httpRequestRetryHandler;
	
	private String pushHost;//推送主机
	
	private final static Logger logger = LoggerFactory
	.getLogger(PushServiceImpl.class);
	
	public PushServiceImpl()
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
	
	public boolean pushToClient(String appId,String appKey,String cid,byte[] content)
	{
		


		logger.debug(
				"[MessageServiceImpl] broadcast, recipient is all ");
		
		HttpPost httpPost = new HttpPost("/1/api/message/client/"+cid+"/delay=0;life=1;type=text");
		httpPost.setHeader("Content-type", "application/octet-stream");
		String str=setAuthHeader(appId,appKey);
		httpPost.addHeader("Authorization", str);
		HttpResponse httpResponse = null;
		try {
			ByteArrayEntity byteArray = new ByteArrayEntity(content);
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

	public String getPushHost() {
		return pushHost;
	}

	public void setPushHost(String pushHost) {
		this.pushHost = pushHost;
	}
	
	
}
