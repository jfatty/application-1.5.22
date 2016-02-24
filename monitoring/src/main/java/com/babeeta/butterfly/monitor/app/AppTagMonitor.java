package com.babeeta.butterfly.monitor.app;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.monitor.BasicHttpClient;
import com.babeeta.butterfly.monitor.Monitor;
import com.babeeta.butterfly.monitor.MonitorConfiguration;
import com.babeeta.butterfly.monitor.MonitorListener;

public class AppTagMonitor extends BasicHttpClient implements Monitor {

	public AppTagMonitor(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startup(MonitorListener listener) {
		System.out.println("<App tag monitor> start......");
		HttpResponse setTagResult = setTag(MonitorConfiguration.appId,
				MonitorConfiguration.cid, MonitorConfiguration.tag);

		if (setTagResult.getStatusLine().getStatusCode() != 200) {
			listener.setResult(false, "Unexpectant response status :"
					+ setTagResult.getStatusLine().getStatusCode()
					+ " while set tag on tag.app ["
								+ host + ":" + port + "]");
		}

		if (!listener.isDone()) {
			HttpResponse removeTagResult = removeTag(
					MonitorConfiguration.appId, MonitorConfiguration.cid,
					MonitorConfiguration.tag);

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

	private HttpResponse setTag(String aid, String cid, String tag) {
		HttpPut httpPut = new HttpPut("/api/set-tag-for-client/" + aid + "/"
				+ cid);
		httpPut.setHeader("Content-type", "application/octet-stream");
		setHttpMethod(httpPut);
		setHttpContent(new ByteArrayEntity(tag.getBytes()));

		execute();

		return getResponse();
	}

	private HttpResponse removeTag(String aid, String cid, String tag) {
		HttpDelete httpDelete = new HttpDelete("/api/remove-client-tag/" + aid
				+ "/" + cid + "/" + tag);

		setHttpMethod(httpDelete);

		execute();

		return getResponse();
	}
}
