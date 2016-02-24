package com.babeeta.butterfly.account.controller;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

public class HttpClientPut extends TestCase {

	public static void main(String args[]) {
		String appId = "b99f07a57c3c46a38847b035814e0d72";
		String appKey = "2";
		String userId = "4";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPut request = new HttpPut(
				"http://accounts.dev/api/account/b99f07a57c3c46a38847b035814e0d72");
		// List<NameValuePair> list = new ArrayList<NameValuePair>();
		// list.add(new BasicNameValuePair("appId", appId));
		// list.add(new BasicNameValuePair("appKey", appKey));
		request.setHeader(new BasicHeader("Content-type", "application/json"));
		String json = "{\"id\":\""
				+ appId
				+ "\",\"secureKey\":\""
				+ appKey
				+ "\",\"userId\":\""
				+ userId
				+ "\",\"extra\":{\"os\":\"HTC\",\"pin\":987654321,\"QQ\":\"123\"}}";
		System.out.println(json);
		ByteArrayEntity byteArray = new ByteArrayEntity(json.getBytes());
		request.setEntity(byteArray);
		try {
			// request.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			HttpResponse response = client.execute(request);
			System.out.println("    [response status]"
					+ response.getStatusLine());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
