package com.babeeta.butterfly.app;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class IOSPushClient {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost(
				"http://localhost:8080/service/push/server/1234567812345678123456781234567812345678123456781234567812345678");
		post.addHeader("X-Target", "IOS");

		StringEntity entity = new StringEntity(
				"{aps:{alert:'hi', badge:21}, text:'hihihi!'}");
		entity.setContentType("application/json");

		post.setEntity(entity);

		byte[] token = (args[0] + ":" + args[1]).getBytes("utf-8");
		String authorization = "Basic "
				+ new String(Base64.encodeBase64(token), "utf-8");
		post.addHeader("Authorization", authorization);

		HttpResponse response = client.execute(post);

		System.out.println(response.getStatusLine());
		System.out.println(EntityUtils.toString(response.getEntity()));
	}

}
