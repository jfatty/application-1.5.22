package com.babeeta.butterfly.application.third;

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

import com.babeeta.butterfly.application.TestEnviroment;

public class BasicClient {

	private String appId = "76c8ee39b0bc46ad932c45279380aeee";
	private String appKey = "e808d332c1fa42d5a04e297a7624448e";
	//protected String HOST ="124.207.12.162";//"218.80.254.141";
	protected String HOST ="192.168.20.82";//"218.80.254.141";
	private HttpUriRequest httpUriRequest;
	private HttpEntity httpEntity;
	private String responseStr;
	private int responseStatus;

	public BasicClient(String appId, String appKey) {
		this.appId = appId;
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
			setAuthHeader();

			if (httpUriRequest instanceof HttpEntityEnclosingRequestBase) {
				((HttpEntityEnclosingRequest) httpUriRequest)
						.setEntity(httpEntity);
			}

			System.out.println("http " + httpUriRequest.getMethod()
					+ " request url>>>");
			System.out.println(httpUriRequest.getURI());

			System.out.println("http request header>>>");
			for (Header header : httpUriRequest.getAllHeaders()) {
				System.out.println(header.getName() + ":" + header.getValue());
			}

			HttpResponse httpResponse = client.execute(new HttpHost(HOST),
					httpUriRequest);

			System.out.println("http response status>>>");
			System.out.println(httpResponse.getStatusLine());

			System.out.println("http response content>>>");
			responseStr = EntityUtils.toString(httpResponse.getEntity());
			responseStatus = httpResponse.getStatusLine().getStatusCode();
			System.out.println(this.responseStr);
			System.out.println(">>>");
			System.out.println(">>>");

		} catch (final IOException e) {
			e.printStackTrace();
		}
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

	public void setHOST(String hOST) {
		this.HOST = hOST;
	}

	protected void setAppId(String appId) {
		this.appId = appId;
	}

	protected void setAppKey(String appKey) {
		this.appKey = appKey;
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