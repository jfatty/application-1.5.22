package com.babeeta.butterfly.application.third.Group;


import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import com.babeeta.butterfly.application.third.BasicClient;

public class AddGroupTagForUserTestClient extends BasicClient {
	public AddGroupTagForUserTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	private String clientId;

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String addOneGroupTag(String groupName) {
		String url = "/1/api/tag/client/" + clientId;

		HttpPut putMethod = new HttpPut(url);

		putMethod.setHeader("Content-type", "application/octet-stream");
		setHttpMethod(putMethod);
		setHttpContent(new ByteArrayEntity(groupName.getBytes()));

		execute();

		return getResponseStr();
	}

	public String addMoreGroupTag(String[] groupName) {
		String url = "/1/api/tag/client/" + clientId;

		HttpPut putMethod = new HttpPut(url);

		StringBuilder groupNameList = new StringBuilder();
		boolean append = false;
		for (String tag : groupName) {
			if (append) {
				groupNameList.append(",");
			} else {
				append = true;
			}
			groupNameList.append(tag);
		}

		putMethod.setHeader("Content-type", "application/octet-stream");
		setHttpMethod(putMethod);
		setHttpContent(new ByteArrayEntity(groupNameList.toString().getBytes()));

		execute();

		return getResponseStr();
	}
}
