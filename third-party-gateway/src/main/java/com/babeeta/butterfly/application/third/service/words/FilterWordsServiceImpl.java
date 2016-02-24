package com.babeeta.butterfly.application.third.service.words;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.third.service.AbstractHttpRPCService;
import com.babeeta.butterfly.application.third.service.app.AppServiceResult;

public class FilterWordsServiceImpl extends AbstractHttpRPCService implements FilterWordsService {

	private static final Logger LOGGER = LoggerFactory
	.getLogger(FilterWordsServiceImpl.class);
	
	private static final String WORDS_SERVICE_HOST = "app.";
	/****
	 * 过滤文本消息
	 * xiazeyong add
	 * @param message
	 * @return
	 */
	public AppServiceResult filterMessage(String message)
	{
		LOGGER.debug("filterMessage");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request=null;
		HttpResponse response=null;
		try {
			request = new HttpPost("/words/filter");
			String json = "{\"word\":\""+message+"\""+"}";
			System.out.println(json.toString());
			request.setHeader(new BasicHeader("Content-type", "application/json"));
			ByteArrayEntity byteArray = new ByteArrayEntity(json.getBytes());
			request.setEntity(byteArray);
			response = client.execute(request);
			String result= EntityUtils.toString(response.getEntity());
			if(result.equals("no")||result=="no")
			{
				return new AppServiceResult(true, 200);
			}
			else
			{
				return new AppServiceResult(false, 417);
			}
		}
		catch(Exception e)
		{
			LOGGER.error("[filterMessage]"+ request.getURI().getPath() + " failed.", e);
			return new AppServiceResult(false, 500);
		}finally{
			if(response!=null && response.getEntity()!=null){
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(),e);
				}
			}
			request.abort();
		}
	}
	@Override
	protected String getHost() {
		// TODO Auto-generated method stub
		return WORDS_SERVICE_HOST;
	}

}
