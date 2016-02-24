package com.babeeta.butterfly.monitor.gateway;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.monitor.BasicHttpClient;
import com.babeeta.butterfly.monitor.Monitor;
import com.babeeta.butterfly.monitor.MonitorConfiguration;
import com.babeeta.butterfly.monitor.MonitorListener;

public class ThirdPartyGatewayMonitor extends BasicHttpClient implements
		Monitor {
	private String msgId;

	public ThirdPartyGatewayMonitor(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startup(MonitorListener listener) {
		System.out.println("<Third party gateway monitor> start......");
		HttpResponse setTagResult = setTag(MonitorConfiguration.appId,
				MonitorConfiguration.appKey, MonitorConfiguration.cid,
				MonitorConfiguration.tag);

		if (setTagResult.getStatusLine().getStatusCode() != 200) {
			listener.setResult(false, "Unexpectant response status :"
					+ setTagResult.getStatusLine().getStatusCode()
					+ " while set tag on 3rd.gateway.app ["
								+ host + ":" + port + "]");
		}

		if (!listener.isDone()) {
			HttpResponse pushResult = broadcast(MonitorConfiguration.appId,
					MonitorConfiguration.appKey, MonitorConfiguration.tag);
			if (pushResult.getStatusLine().getStatusCode() == 200) {
				msgId = getResponseStr();
			} else {
				listener.setResult(false, "Unexpectant response status :"
						+ pushResult.getStatusLine().getStatusCode()
						+ " while push message on 3rd.gateway.app ["
									+ host + ":" + port + "]");
			}
		}

		if (!listener.isDone()) {
			HttpResponse removeTagResult = removeTag(
					MonitorConfiguration.appId, MonitorConfiguration.appKey,
					MonitorConfiguration.cid, MonitorConfiguration.tag);

			if (removeTagResult.getStatusLine().getStatusCode() == 200) {
				listener.setResult(true, "success");
			} else {
				listener.setResult(false,
						"Unexpectant response status :"
								+ removeTagResult.getStatusLine()
										.getStatusCode()
								+ " while remove tag on tag.app ["
								+ host + ":" + port + "]");
			}
		}
		if (!listener.isDone()) {
			listener.setResult(false, "had something wrong happened.");
		}
	}

	private HttpResponse broadcast(String appId, String appKey, String tag) {
		HttpPost httpPost = new HttpPost(
				"/1/api/message/broadcast/" + tag
						+ "/life=24;delay=60;type=text");
		httpPost.setHeader("Content-type", "application/octet-stream");

		setHttpMethod(httpPost);
		setHttpContent(new ByteArrayEntity(MonitorConfiguration.msg.getBytes()));

		useAuth(appId, appKey);
		execute();

		return getResponse();
	}

	private HttpResponse setTag(String appId, String appKey, String cid,
			String tag) {

		HttpPut httpPut = new HttpPut("/1/api/tag/client/" + cid);
		httpPut.setHeader("Content-type", "application/octet-stream");

		setHttpMethod(httpPut);
		setHttpContent(new ByteArrayEntity(tag.getBytes()));
		useAuth(appId, appKey);
		execute();

		return getResponse();
	}

	private HttpResponse removeTag(String appId, String appKey, String cid,
			String tag) {
		HttpDelete httpDelete = new HttpDelete("/1/api/tag/client/" + cid + "/"
				+ tag);

		setHttpMethod(httpDelete);
		useAuth(appId, appKey);
		execute();

		return getResponse();
	}
}
