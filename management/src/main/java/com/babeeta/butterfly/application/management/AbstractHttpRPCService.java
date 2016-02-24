package com.babeeta.butterfly.application.management;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpRPCService {
	private ThreadLocal<HttpClient> httpClientThreadLocal = new ThreadLocal<HttpClient>();
	protected static final String CONTENT_TYPE_JSON = "application/json";
	protected static final String CONTENT_TYPE_PLAIN = "text/plain;charset=UTF-8";
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractHttpRPCService.class);

	private HttpClient getHttpClient() {
		// TODO 需要优化
		if (httpClientThreadLocal.get() == null) {

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(
					new Scheme("http", 80, PlainSocketFactory
							.getSocketFactory()));

			ThreadSafeClientConnManager threadSafeClientConnManager = new ThreadSafeClientConnManager(
					schemeRegistry);
			threadSafeClientConnManager.setMaxTotal(50);
			threadSafeClientConnManager.setDefaultMaxPerRoute(50);

			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(
					threadSafeClientConnManager);

			httpClientThreadLocal.set(defaultHttpClient);
		}

		return httpClientThreadLocal.get();
	}

	protected abstract String getHost();

	protected HttpResponse invoke(HttpUriRequest httpUriRequest)
			throws ClientProtocolException, IOException {
		return getHttpClient().execute(new HttpHost(getHost()), httpUriRequest);
	}
	
	protected HttpResponse invoke(HttpUriRequest httpUriRequest,String host)
	throws ClientProtocolException, IOException {
		return getHttpClient().execute(new HttpHost(host), httpUriRequest);
	}
	public void destroy() {
		if (httpClientThreadLocal.get() != null) {
			httpClientThreadLocal.get().getConnectionManager().shutdown();
			LOGGER.info("connection manager have been shutdown.");
		}
	}

}
