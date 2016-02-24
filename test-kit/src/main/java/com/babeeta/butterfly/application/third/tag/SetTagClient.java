package com.babeeta.butterfly.application.third.tag;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import com.babeeta.butterfly.application.TestEnviroment;
import com.babeeta.butterfly.application.third.BasicClient;

public class SetTagClient extends BasicClient{

	public SetTagClient(String appId, String appKey) {
		super(appId, appKey);
	}
	
	public void set(String cid,String tag){
		HttpPut httpPut=new HttpPut("/1/api/tag/client/"+cid);
		httpPut.setHeader("Content-type", "application/octet-stream");
		
		StringEntity stringEntity=null;
		try {
			stringEntity = new StringEntity(tag);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		setHttpMethod(httpPut);
		setHttpContent(stringEntity);
		
		execute();
		
	}
	public static void main(String[] args) {
		String cid=args[0];
		String tag=args[1];
		SetTagClient setupTagClient=new SetTagClient(TestEnviroment.APP_ID, TestEnviroment.APP_KEY);
		setupTagClient.set(cid, tag);
	}
	
}
