package com.babeeta.butterfly.application.third.Message;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.application.third.BasicClient;

public class ModifyMessageContentTestClient extends BasicClient {

	public ModifyMessageContentTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	public String modify(String messageId, String type, String newContent) {
		HttpPut httpPut = new HttpPut("/1/api/message/update/" + messageId
				+ "/type=" + type);
		System.out.println(httpPut.getURI());
		httpPut.setHeader("Content-type", "application/octet-stream");
		setHttpMethod(httpPut);
		setHttpContent(new ByteArrayEntity(("hello " + newContent).getBytes()));

		execute();

		return getResponseStr();
	}
}
