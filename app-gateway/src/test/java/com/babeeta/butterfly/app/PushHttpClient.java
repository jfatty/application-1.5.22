package com.babeeta.butterfly.app;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class PushHttpClient {
	public static void main(String[] args) {
		double random = Math.random();
		String appId = "8ce045a7d9354c3c97e5e5214d03258a";
		String appKey = "8f6200236350438e92361ddfa3713893";
		String content = "hello." + random;

		HttpClient client = new DefaultHttpClient();
		/*
		 * HttpPost request = new HttpPost(
		 * "http://localhost:8080/push/service/server/5c3f7ffadbe34c6faf6394a7f3114d11"
		 * );
		 */
		HttpPost request = new HttpPost(
				"http://localhost/service/client/8ce045a7d9354c3c97e5e5214d03258a/message");
		try {
			byte[] token = (appId + ":" + appKey).getBytes("utf-8");
			String authorization = "Basic "
					+ new String(Base64.encodeBase64(token), "utf-8");
			request.addHeader("Authorization", authorization);
			request.addHeader("exptime", "20");
			// request.addHeader("Content-type",
			// "application/json;charset=UTF-8");
			ByteArrayEntity byteArray = new ByteArrayEntity(
					content.getBytes("utf-8"));
			request.setEntity(byteArray);

			HttpResponse response = client.execute(request);
			System.out.println("[content]" + content + "    [response status]"
					+ response.getStatusLine() + "   [response result]"
					+ EntityUtils.toString(response.getEntity()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
