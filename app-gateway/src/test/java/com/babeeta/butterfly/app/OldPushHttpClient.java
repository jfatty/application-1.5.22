package com.babeeta.butterfly.app;

import java.io.*;

import org.apache.commons.codec.binary.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;

public class OldPushHttpClient {
	public static void main(String[] args) {
		double random = Math.random();
		String appId = "44bbcd579c294406b0099df1362b472d";
		String appKey = "2350b2fed2b2409ba15acff2841c8b5c";
		String content = "hello." + random;

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(
				"http://localhost:8080/service/push/server/5c3f7ffadbe34c6faf6394a7f3114d11");
		try {
			byte[] token = (appId + ":" + appKey).getBytes("utf-8");
			String authorization = "Basic "
					+ new String(Base64.encodeBase64(token), "utf-8");
			request.addHeader("Authorization", authorization);
			ByteArrayEntity byteArray = new ByteArrayEntity(
					content.getBytes("utf-8"));
			request.setEntity(byteArray);

			HttpResponse response = client.execute(request);
			System.out.println("[content]" + content + "    [response status]"
					+ response.getStatusLine() + "   [response result]"
					+ EntityUtils.toString(response.getEntity()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
