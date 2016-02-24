package com.babeeta.butterfly.application.third.Message;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.application.TestEnviroment;
import com.babeeta.butterfly.application.third.BasicClient;

public class PushBroadcastMessageTestClient extends BasicClient {
	public PushBroadcastMessageTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	public String broadcast(String tagList) {
		HttpPost httpPost = new HttpPost(
				"/1/api/message/broadcast/" + tagList
						+ "/life=1;delay=1;type=text");


		httpPost.setHeader("Content-type", "application/octet-stream");

		setHttpMethod(httpPost);
		setHttpContent(new ByteArrayEntity("hello broadcast".getBytes()));

		execute();
		return getResponseStr();
	}
	public static void main(String[] args) {
		PushBroadcastMessageTestClient pushBroadcastMessageTestClient=new PushBroadcastMessageTestClient(TestEnviroment.APP_ID, TestEnviroment.APP_KEY);
		pushBroadcastMessageTestClient.broadcast(args[0]);
	}
}
