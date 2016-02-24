package com.babeeta.butterfly.application.third.Message;

import org.apache.http.client.methods.HttpGet;

import com.babeeta.butterfly.application.third.BasicClient;

public class QueryMessageStatusTestClient extends BasicClient {

	public QueryMessageStatusTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	public String queryStatus(String messageId) {
		HttpGet httpGet = new HttpGet("/1/api/message/query/" + messageId);
		setHttpMethod(httpGet);

		execute();
		return getResponseStr();
	}
}
