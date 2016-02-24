package com.babeeta.butterfly.application.third.Message;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.application.TestEnviroment;
import com.babeeta.butterfly.application.third.BasicClient;

public class PushSingleMessageTestClient extends BasicClient {
	private int life=10;
	private int delay=0;

	public void setLife(int life) {
		this.life = life;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public PushSingleMessageTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	public String push(String clientId, String type) {
		HttpPost httpPost = new HttpPost("/1/api/message/client/" + clientId
				+ "/delay=" + delay + ";type=" + type+";life=1");
		httpPost.setHeader("Content-type", "application/octet-stream");
		setHttpMethod(httpPost);
		try {
			setHttpContent(new ByteArrayEntity(("hello 法轮功" + clientId).getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		execute();

		return getResponseStr();
	}
	public static void main(String[] args) {
		String clientId=args[0];
		PushSingleMessageTestClient pushSingleMessageTestClient=new PushSingleMessageTestClient(TestEnviroment.APP_ID, TestEnviroment.APP_KEY);
		pushSingleMessageTestClient.push(clientId, "text");
	}
}
