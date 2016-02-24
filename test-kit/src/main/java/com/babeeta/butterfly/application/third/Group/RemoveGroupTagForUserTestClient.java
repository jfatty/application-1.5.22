package com.babeeta.butterfly.application.third.Group;

import org.apache.http.client.methods.HttpDelete;

import com.babeeta.butterfly.application.third.BasicClient;

public class RemoveGroupTagForUserTestClient extends BasicClient {
	public RemoveGroupTagForUserTestClient(String appId, String appKey) {
		super(appId, appKey);
	}

	private String clientId;

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String removeOneGroupTag(String groupName) {
		String url = "/1/api/tag/client/" + clientId + "/" + groupName;

		HttpDelete deleteMethod = new HttpDelete(url);

		setHttpMethod(deleteMethod);

		execute();

		return getResponseStr();
	}

	public String removeMoreGroupTag(String[] groupName) {
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

		String url = "/1/api/tag/client/" + clientId + "/"
				+ groupNameList.toString();

		HttpDelete deleteMethod = new HttpDelete(url);

		setHttpMethod(deleteMethod);

		execute();

		return getResponseStr();
	}

}
