package com.babeeta.butterfly.monitor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicHttpClient {
	private final static Logger logger = LoggerFactory
			.getLogger(BasicHttpClient.class);
	protected String host;
	protected int port;
	private HttpUriRequest httpUriRequest;
	private HttpEntity httpEntity;
	private String responseStr;
	private int responseStatus;
	private HttpResponse httpResponse;
	private boolean useAuth = false;
	private String appId;
	private String appKey;

	public BasicHttpClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void useAuth(String appId, String appKey) {
		useAuth = true;
		this.appId = appId;
		this.appKey = appKey;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getResponseStr() {
		return responseStr;
	}

	protected void execute() {
		HttpParams params = new BasicHttpParams();

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		HttpClient client = new DefaultHttpClient(new SingleClientConnManager(
				params, schemeRegistry), params);

		try {
			if (useAuth) {
				setAuthHeader();
			}
			if (httpUriRequest instanceof HttpEntityEnclosingRequestBase) {
				((HttpEntityEnclosingRequest) httpUriRequest)
						.setEntity(httpEntity);
			}

			logger.debug("http " + httpUriRequest.getMethod()
					+ " request url>>>");
			logger.debug("" + httpUriRequest.getURI());

			logger.debug("http request header>>>");
			for (Header header : httpUriRequest.getAllHeaders()) {
				logger.debug(header.getName() + ":" + header.getValue());
			}

			httpResponse = client.execute(
					new HttpHost(host, port),
					httpUriRequest);

			logger.debug("http response status>>>");
			logger.debug("" + httpResponse.getStatusLine());

			logger.debug("http response content>>>");
			responseStr = EntityUtils.toString(httpResponse.getEntity());
			responseStatus = httpResponse.getStatusLine().getStatusCode();
			logger.debug(this.responseStr);
			logger.debug(">>>");
			logger.debug(">>>");

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public HttpResponse getResponse() {
		return httpResponse;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setHttpContent(HttpEntity httpEntity) {
		this.httpEntity = httpEntity;
	}

	public void setHttpMethod(HttpUriRequest httpUriRequest) {
		this.httpUriRequest = httpUriRequest;
	}

	private void setAuthHeader() {

		byte[] token = null;
		String authorization = null;
		try {
			token = (appId + ":" + appKey).getBytes("utf-8");
			authorization = "Basic "
					+ new String(Base64.encodeBase64(token), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		httpUriRequest.addHeader("Authorization", authorization);

	}
}
