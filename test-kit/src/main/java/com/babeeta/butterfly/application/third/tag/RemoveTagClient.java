package com.babeeta.butterfly.application.third.tag;

import org.apache.http.client.methods.HttpDelete;

import com.babeeta.butterfly.application.TestEnviroment;
import com.babeeta.butterfly.application.third.BasicClient;

public class RemoveTagClient extends BasicClient{

	public RemoveTagClient(String appId, String appKey) {
		super(appId, appKey);
	}
	
	public void remove(String cid,String tagList){
		HttpDelete httpDelete=new HttpDelete("/1/api/tag/client/"+cid+"/"+tagList);
		setHttpMethod(httpDelete);
		execute();
	}
	public static void main(String[] args) {
		String cid=args[0];
		String tagList=args[1];
		RemoveTagClient removeTagClient=new RemoveTagClient(TestEnviroment.APP_ID, TestEnviroment.APP_KEY);
		removeTagClient.remove(cid, tagList);
	}

}
