package com.babeeta.butterfly.account.controller;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientGet extends TestCase {

	public static void main(String args[]) {
		String appId = "ef6db911cc844d47a4bda7d6c9f7b9f3";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(
				"http://accounts.dev/api/account?userId=4");
		try {
			// request.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			HttpResponse response = client.execute(request);
			System.out.println("    [response status]"
					+ response.getStatusLine() + "   ,"
					+ EntityUtils.toString(response.getEntity()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
