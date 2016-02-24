package com.babeeta.butterfly.application.third.Message;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.application.third.BasicClient;

public class ChangeRecipientTestClient extends BasicClient{

	public ChangeRecipientTestClient(String appId, String appKey) {
		super(appId, appKey);
		// TODO Auto-generated constructor stub
	}

	
	public String change(String oldCid,String newCid)
	{
		HttpPut httpPut = new HttpPut("/1/api/message/client/change/" + oldCid
				+ "/" + newCid);
		httpPut.setHeader("Content-type", "application/octet-stream");
		setHttpMethod(httpPut);
		execute();
		return getResponseStr();
	}
}
