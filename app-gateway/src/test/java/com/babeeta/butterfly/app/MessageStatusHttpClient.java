package com.babeeta.butterfly.app;

import java.io.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;

public class MessageStatusHttpClient {
	public static void main(String[] args) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(
				"http://push-api-0.hudee.com/service/client/all/message/4ed46178a1c7432b66e8076ec2fdb96");
		try {
			HttpResponse response = client.execute(request);
			System.out.println("[response status]" + response.getStatusLine()
					+ "   [response result]"
					+ EntityUtils.toString(response.getEntity()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
