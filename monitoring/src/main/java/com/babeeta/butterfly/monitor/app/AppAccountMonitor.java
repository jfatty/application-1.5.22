package com.babeeta.butterfly.monitor.app;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.monitor.BasicHttpClient;
import com.babeeta.butterfly.monitor.Monitor;
import com.babeeta.butterfly.monitor.MonitorConfiguration;
import com.babeeta.butterfly.monitor.MonitorListener;

public class AppAccountMonitor extends BasicHttpClient implements Monitor {
	public AppAccountMonitor(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startup(MonitorListener listener) {
		System.out.println("<App account monitor> start......");
		HttpResponse verifyResult = verify(MonitorConfiguration.appId,
				MonitorConfiguration.appKey);
		if (verifyResult.getStatusLine().getStatusCode() != 200) {
			listener.setResult(false, verifyResult.getStatusLine().toString());
		} else {
			JSONObject obj = null;
			try {
				String rsp = getResponseStr();
				obj = JSONObject.fromObject(rsp);
			} catch (ParseException e) {
				listener.setResult(false, e.getMessage());
				e.printStackTrace();
			}
			if ("OK".equalsIgnoreCase(obj.get("status").toString())) {
				listener.setResult(true, null);
			} else if ("FREEZED".equals(obj.get("status").toString())) {
				listener.setResult(false, "Freezed");
			} else {
				listener.setResult(false, "Unmatched");
			}
		}
	}

	private HttpResponse verify(String appId, String appKey) {
		HttpPost httpPost = new HttpPost("/api/auth");
		String json = "{\"id\":\""
						+ appId
						+ "\",\"secureKey\":\""
						+ appKey
						+ "\"}";
		httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
		setHttpMethod(httpPost);
		setHttpContent(new ByteArrayEntity(json.getBytes()));

		execute();

		return getResponse();
	}
}
