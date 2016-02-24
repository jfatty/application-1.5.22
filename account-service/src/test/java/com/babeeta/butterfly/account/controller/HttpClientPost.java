package com.babeeta.butterfly.account.controller;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpClientPost extends TestCase {

	public static void main(String args[]) {
		String appId = "b99f07a57c3c46a38847b035814e0d72";
		String appKey = "34990d31851f4cfaa614f4b273954337";
		appId="a4ba23433ecc41968934f1d042836e1b";
		appKey="0a5c962695574d0a8dc196763eab9544";
		String userId = "4";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(
				"http://accounts.dev/api/register");
		// List<NameValuePair> list = new ArrayList<NameValuePair>();
		// list.add(new BasicNameValuePair("appId", appId));
		// list.add(new BasicNameValuePair("appKey", appKey));
		request.setHeader(new BasicHeader("Content-type", "application/json"));
		String json = "{\"id\":\""
				+ appId
				+ "\",\"secureKey\":\""
				+ appKey
				+ "\",\"extra\":{\"userId\":\""
				+ userId
				+ "\",\"os\":\"microsoft-windows\",\"pin\":987654321,\"0\":\"123\"}}";
		System.out.println(json);
		ByteArrayEntity byteArray = new ByteArrayEntity(json.getBytes());
		request.setEntity(byteArray);
		try {
			// request.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			HttpResponse response = client.execute(request);
			System.out.println("    [response status]"
					+ response.getStatusLine() + "   [response result]"
					+ EntityUtils.toString(response.getEntity()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
