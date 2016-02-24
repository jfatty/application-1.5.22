package com.babeeta.butterfly.application.third.Group;

import org.apache.http.client.methods.HttpGet;

import com.babeeta.butterfly.application.third.BasicClient;

public class QueryGroupTagListOfUserTestClient extends BasicClient {
	public QueryGroupTagListOfUserTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	public String queryGroupTagOfUser(String clientId) {
		String url = "/1/api/group/device/" + clientId;

		HttpGet getMethod = new HttpGet(url);

		setHttpMethod(getMethod);

		execute();

		return getResponseStr();
	}
}