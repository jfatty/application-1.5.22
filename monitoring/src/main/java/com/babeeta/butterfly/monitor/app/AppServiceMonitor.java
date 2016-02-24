package com.babeeta.butterfly.monitor.app;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.babeeta.butterfly.monitor.BasicHttpClient;
import com.babeeta.butterfly.monitor.Monitor;
import com.babeeta.butterfly.monitor.MonitorConfiguration;
import com.babeeta.butterfly.monitor.MonitorListener;

public class AppServiceMonitor extends BasicHttpClient implements Monitor {

	public AppServiceMonitor(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startup(MonitorListener listener) {
		String msgId = null;
		System.out.println("<App service monitor> start......");

		HttpResponse unicastResult = pushUnicastMessage(
				MonitorConfiguration.cid, MonitorConfiguration.appId,
				MonitorConfiguration.msg);

		if (unicastResult.getStatusLine().getStatusCode() == 200) {
			msgId = getResponseStr();
		} else {
			listener.setResult(false, "Unexpectant response status :"
						+ unicastResult.getStatusLine().getStatusCode()
						+ " while push unicast message on service.app ["
									+ host + ":" + port + "]");
		}

		if (!listener.isDone()) {
			HttpResponse deleteResult = deleteMessage(
					MonitorConfiguration.appId, msgId);

			if (deleteResult.getStatusLine().getStatusCode() != 200) {
				listener.setResult(false,
						"Unexpectant response status :"
								+ deleteResult.getStatusLine()
										.getStatusCode()
								+ " while remove message on service.app ["
								+ host + ":" + port + "]");
			} else {
				listener.setResult(true, "success");
			}
		}

		if (!listener.isDone()) {
			listener.setResult(false, "had something wrong happened.");
		}

	}

	private HttpResponse pushUnicastMessage(String recipient, String sender,
			String message) {
		HttpPost httpPost = new HttpPost("/message/push/single/"
				+ sender + "/" + recipient);
		httpPost.addHeader("delay", "60");
		httpPost.addHeader("exptime", "24");
		httpPost.addHeader("DataType", "bibary");
		httpPost.setHeader("Content-type", "application/octet-stream");

		setHttpMethod(httpPost);
		setHttpContent(new ByteArrayEntity(message.getBytes()));
		execute();

		return getResponse();
	}

	private HttpResponse deleteMessage(String sender, String messageId) {
		HttpDelete httpDelete = new HttpDelete("/message/delete/" + sender
				+ "/"
				+ messageId);
		httpDelete.setHeader("Content-type", "application/octet-stream");
		setHttpMethod(httpDelete);
		execute();

		return getResponse();
	}
}
