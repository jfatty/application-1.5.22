package com.babeeta.butterfly.subscription;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.babeeta.butterfly.subscription.entity.Binding;

public class SubscriptionT {
	DefaultHttpClient client = new DefaultHttpClient();
	String aid = "1";
	String cid = "2";
	String did = "3";

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testBind() {
		HttpPost request = new HttpPost(
				"http://localhost:8080/subscription/api/bind");
		request.setHeader(new BasicHeader("Content-type", "application/json"));
		String json = "{\"aid\":\""
				+ aid
				+ "\",\"cid\":\""
				+ cid
				+ "\",\"did\":\"" + did + "\"}";
		System.out.println(json);
		ByteArrayEntity byteArray = new ByteArrayEntity(json.getBytes());
		request.setEntity(byteArray);
		try {

			HttpResponse response = client.execute(request);
			String entity = EntityUtils.toString(response.getEntity());
			System.out.println("    [response status]"
					+ response.getStatusLine() + "   [response result]"
					+ entity);
			Binding binding = mapper.readValue(entity, Binding.class);
			cid = binding.getCid();
			System.out.println(cid);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUnbind() {
		HttpPost request = new HttpPost(
				"http://localhost:8080/subscription/api/unbind");
		request.setHeader(new BasicHeader("Content-type", "application/json"));
		String json = "{\"aid\":\""
				+ aid
				+ "\",\"cid\":\""
				+ cid
				+ "\"}";
		System.out.println(json);
		ByteArrayEntity byteArray = new ByteArrayEntity(json.getBytes());
		request.setEntity(byteArray);
		try {
			HttpResponse response = client.execute(request);
			System.out.println("    [response status]"
					+ response.getStatusLine() + "   [response result]"
					+ EntityUtils.toString(response.getEntity()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
