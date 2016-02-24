package com.babeeta.butterfly.monitor.dev;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.monitor.BasicHttpClient;
import com.babeeta.butterfly.monitor.Monitor;
import com.babeeta.butterfly.monitor.MonitorConfiguration;
import com.babeeta.butterfly.monitor.MonitorListener;

public class DevAccountMonitor extends BasicHttpClient implements Monitor {
	public DevAccountMonitor(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startup(MonitorListener listener) {
		System.out.println("<Dev account monitor> start......");

		HttpResponse authResult = auth(MonitorConfiguration.deviceId,
				MonitorConfiguration.deviceKey);
		if (authResult.getStatusLine().getStatusCode() != 200) {
			listener.setResult(false, "auth failed: "
						+ authResult.getStatusLine()
								.toString());
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
			} else {
				listener.setResult(false, obj.get("status").toString());
			}
		}

		if (!listener.isDone()) {
			listener.setResult(false, "dev account monitor: "
					+ "had something wrong happened.");
		}
	}

	private HttpResponse auth(String did, String key) {
		HttpPost httpPost = new HttpPost("/api/auth");
		String json = "{\"id\":\""
						+ did
						+ "\",\"secureKey\":\""
						+ key
						+ "\"}";
		httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
		setHttpMethod(httpPost);
		setHttpContent(new ByteArrayEntity(json.getBytes()));

		execute();

		return getResponse();
	}
}
