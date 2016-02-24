package com.babeeta.butterfly.application.third.service.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.babeeta.butterfly.application.third.service.AbstractHttpRPCService;

public class AppAccountServiceImpl extends AbstractHttpRPCService implements
		AppAccountService {
	private static final String HOST = "accounts.app.";

	@Override
	public List<Map> getFeedbackServiceEnableAccount() {
		HttpGet httpGet = new HttpGet("/api/account/expireMsgFeedbackEnable");
		HttpResponse httpResponse=null;
		try {
			 httpResponse = invoke(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				LOGGER
						.error(
								"http get request to {} failed,failed status {},response is {}",
								new Object[] {
										httpGet.getRequestLine().getUri()
												.toString(),
										httpResponse
												.getStatusLine()
												.getStatusCode(),
										EntityUtils.toString(httpResponse
												.getEntity()) });
				return null;
			}

			String responseStr = EntityUtils.toString(httpResponse.getEntity());

			LOGGER.debug("http get request to {} ok,response is {}",
					httpGet.getRequestLine().getUri().toString(), responseStr);

			JSONArray jsonArray = JSONArray.fromObject(responseStr);

			return (List<Map>) JSONArray.toList(jsonArray, HashMap.class);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}finally{
			if(httpResponse!=null && httpResponse.getEntity()!=null){
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(),e);
				}
			}
			httpGet.abort();
		}
	}

	@Override
	protected String getHost() {
		return HOST;
	}

}
