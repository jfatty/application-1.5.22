package com.babeeta.butterfly.application.app.tag;

import java.io.IOException;
import java.net.SocketException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpRPCService {
	private ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<HttpClient>();
	protected static final String CONTENT_TYPE_JSON = "application/json";
	protected static final String CONTENT_TYPE_PLAIN = "text/html;charset=UTF-8";
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractHttpRPCService.class);
	
	private HttpRequestRetryHandler httpRequestRetryHandler;
	
	public AbstractHttpRPCService(){
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
		        	LOGGER.error("[{}] [{}] [{}] [{}] have been retry 5 times,stop retry.",new Object[]{uri,methodName,exception.getClass().getName(),exception.getMessage()});
		            return false;
		        }
		        if (exception instanceof NoHttpResponseException) {
		            // Retry if the server dropped connection on us
		        	LOGGER.error("[{}] [{}] [{}] [{}] http retry",new Object[]{uri,methodName,exception.getClass().getName(),exception.getMessage()});
		            return true;
		        }
		    	if((exception instanceof SocketException) && exception.getMessage().startsWith("Connection reset")){
					LOGGER.error("[{}] [{}] [{}] [{}] Connection reset retry",new Object[]{uri,methodName,exception.getClass().getName(),exception.getMessage()});
					return true;
				}
		        
		        boolean idempotent = !(httpRequest instanceof HttpEntityEnclosingRequest); 
		        if (idempotent) {
		            // Retry if the request is considered idempotent
		        	LOGGER.error("[{}] [{}] idempotent retry",new Object[]{uri,methodName});
		            return true;
		        }
		        return false;
		    }

		};
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

	protected abstract String getHost();

	protected HttpResponse invoke(HttpUriRequest httpUriRequest)
			throws ClientProtocolException, IOException {
		return getHttpClient().execute(new HttpHost(getHost()), httpUriRequest);
	}

	public void destroy() {
		if (httpClientThreadLocal.get() != null) {
			httpClientThreadLocal.get().getConnectionManager().shutdown();
			LOGGER.info("connection manager have been shutdown.");
		}
	}

}
