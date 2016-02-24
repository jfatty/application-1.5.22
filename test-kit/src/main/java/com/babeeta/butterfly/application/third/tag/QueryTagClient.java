package com.babeeta.butterfly.application.third.tag;

import org.apache.http.client.methods.HttpGet;

import com.babeeta.butterfly.application.third.BasicClient;

public class QueryTagClient extends BasicClient{

	public QueryTagClient(String appId, String appKey) {
		super(appId, appKey);
	}
	public String query(String clientId){
		HttpGet httpGet=new HttpGet("/1/api/tag/client/"+clientId);
		setHttpMethod(httpGet);
		execute();
		
		return getResponseStr();
	}
}
