package com.babeeta.butterfly.application.third.Message;

import org.apache.http.client.methods.HttpDelete;

import com.babeeta.butterfly.application.third.BasicClient;

public class DeleteMessageTestClient extends BasicClient {

	public DeleteMessageTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	public String delete(String messageId) {
		HttpDelete httpDelete = new HttpDelete("/1/api/message/delete/" + messageId);

		setHttpMethod(httpDelete);

		execute();
		return getResponseStr();
	}

}
