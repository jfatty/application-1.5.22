package com.babeeta.butterfly.application.third.service.auth;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.babeeta.butterfly.application.third.service.AbstractHttpRPCService;

public class AuthServiceImpl extends AbstractHttpRPCService implements
		AuthService {

	private static final String HOST = "accounts.app";

	@Override
	public AuthResult authenticate(String appId, String appKey) {

		HttpPost httpPost = new HttpPost("/api/auth");
		httpPost.setHeader(new BasicHeader("Content-type", "application/json"));
		String json = "{\"id\":\"" + appId + "\",\"secureKey\":\"" + appKey
				+ "\"}";
		httpPost.setEntity(new ByteArrayEntity(json.getBytes()));

		HttpResponse httpResponse = null;
		JSONObject obj = null;

		try {

			httpResponse = invoke(httpPost);

			if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
				LOGGER
						.error(
								"auth failed case request to {} response status is {},response body is {}",new Object[]{httpPost.getURI().toString(),
										httpResponse.getStatusLine().getStatusCode(),
										EntityUtils.toString(httpResponse.getEntity())});
				
				return new AuthResult(false, AuthFailedReason.serverInternalError);
			}
			
			obj = JSONObject.fromObject(EntityUtils.toString(httpResponse
					.getEntity()));
			
		} catch (Exception e) {
			LOGGER.error("request to " + httpPost.getURI().toString()
					+ " failed,reason :" + e.getMessage(), e);
			return new AuthResult(false, AuthFailedReason.serverInternalError);
		}finally{
			if(httpResponse!=null && httpResponse.getEntity()!=null){
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(),e);
				}
			}
			httpPost.abort();
		}


		if ("OK".equalsIgnoreCase(obj.get("status").toString())) {
			return new AuthResult(true, null);
		} else if ("FREEZED".equals(obj.get("status").toString())) {
			return new AuthResult(false, AuthFailedReason.freezed);
		} else {
			return new AuthResult(false, AuthFailedReason.unmatched);
		}
	}

	@Override
	protected String getHost() {
		return HOST;
	}
}
