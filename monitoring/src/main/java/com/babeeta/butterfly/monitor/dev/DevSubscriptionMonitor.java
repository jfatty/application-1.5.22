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

public class DevSubscriptionMonitor extends BasicHttpClient implements Monitor {
	private String cid = null;

	public DevSubscriptionMonitor(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void startup(MonitorListener listener) {
		System.out.println("<Dev subscription monitor> start......");
		HttpResponse bindResult = bind(MonitorConfiguration.did,
				MonitorConfiguration.appId, cid);

		if (bindResult.getStatusLine().getStatusCode() == 200) {
			JSONObject obj = null;
			try {
				String rsp = getResponseStr();
				obj = JSONObject.fromObject(rsp);
				cid = obj.getString("cid");
			} catch (ParseException e) {
				listener.setResult(false, e.getMessage());
				e.printStackTrace();
			}
		} else {
			listener.setResult(false, "Unexpectant response status :"
					+ bindResult.getStatusLine().getStatusCode()
					+ " while binding on subscription.dev ["
								+ host + ":" + port + "]");
		}

		if (!listener.isDone()) {
			HttpResponse unbindResult = unbind(MonitorConfiguration.did,
					MonitorConfiguration.appId,
					cid);

			if (unbindResult.getStatusLine().getStatusCode() == 200) {
				JSONObject obj = null;
				try {
					String rsp = getResponseStr();
					obj = JSONObject.fromObject(rsp);
					if ("OK".equalsIgnoreCase(obj.getString("status"))) {
						listener.setResult(true, "success");
					} else {
						listener.setResult(false, obj.getString("status"));
					}
				} catch (ParseException e) {
					listener.setResult(false, e.getMessage());
					e.printStackTrace();
				}
			} else {
				listener.setResult(false,
						"Unexpectant response status :"
								+ bindResult.getStatusLine().getStatusCode()
								+ " while unbinding on subscription.dev ["
								+ host + ":" + port + "]");
			}
		}
		if (!listener.isDone()) {
			listener.setResult(false, "had something wrong happened.");
		}
	}

	private HttpResponse bind(String did, String aid, String cid) {
		HttpPost httpPost = new HttpPost("/api/bind");
		String requestEntity = "{\"aid\":\""
								+ aid
								+ "\",\"cid\":\""
								+ (cid == null ? "" : cid)
								+ "\",\"did\":\""
								+ did + "\"}";
		httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
		setHttpMethod(httpPost);
		setHttpContent(new ByteArrayEntity(requestEntity.getBytes()));

		execute();

		return getResponse();
	}

	private HttpResponse unbind(String did, String aid, String cid) {
		HttpPost httpPost = new HttpPost("/api/unbind");
		String requestEntity = "{\"aid\":\""
								+ aid
								+ "\",\"cid\":\""
								+ cid
								+ "\",\"did\":\""
								+ did + "\"}";
		httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
		setHttpMethod(httpPost);
		setHttpContent(new ByteArrayEntity(requestEntity.getBytes()));

		execute();

		return getResponse();
	}
}
