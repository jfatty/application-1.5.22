package com.babeeta.butterfly.application.app.tag;

import java.io.IOException;

import net.sf.json.JSONArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagServiceImpl extends AbstractHttpRPCService implements
		TagService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TagServiceImpl.class);
	private static final String TAG_SERVICE_HOST = "tag.app";

	@Override
	protected String getHost() {
		return TAG_SERVICE_HOST;
	}

	/***
	 * 通过tagName查询clientId
	 * 
	 * @param aid
	 * @param groupTag
	 * @return
	 */
	public TagResult listClient(String aid, String groupTag) {
		HttpGet httpGet = new HttpGet("/api/get-tag-client-list/" + aid + "/"
				+ groupTag);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				LOGGER.error("request to {} 404 ", httpGet.getURI().toURL()
						.toString());
				return new TagResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				TagResult result = new TagResult(true, 200);
				String[] tagList = (String[]) JSONArray.fromObject(
						EntityUtils.toString(httpResponse.getEntity()))
						.toArray(new String[] {});
				result.setStringList(tagList);
				return result;
			} else {
				LOGGER
						.error(
								"request to {} failed,response status {},response body {}",
								new Object[] {
										httpGet.getURI().toString(),
										httpResponse.getStatusLine()
												.getStatusCode(),
										EntityUtils.toString(httpResponse
												.getEntity()) });
				return new TagResult(false, 500);
			}
		} catch (Exception e) {
			LOGGER.error("request to " + httpGet.getURI().toString()
					+ " failed ", e);
			return new TagResult(false, 500);
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

	/***
	 * 判断tag是否存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName) {
		HttpGet httpGet = new HttpGet("/api/get-tag-tagname/" + aid + "/"
				+ tagName);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				LOGGER.error("request to {} 404 ", httpGet.getURI().toURL()
						.toString());
				return false;
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			} else {
				LOGGER
				.error(
						"request to {} failed,response status {},response body {}",
						new Object[] {
								httpGet.getURI().toString(),
								httpResponse.getStatusLine()
										.getStatusCode(),
								EntityUtils.toString(httpResponse
										.getEntity()) });
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("request to " + httpGet.getURI().toString()
					+ " failed ", e);
			return false;
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
	public int tagCount(String aid, String groupTag) {
		HttpGet httpGet = new HttpGet("/api/count/"+aid+"/"+groupTag);
		HttpResponse httpResponse=null;
		
		try {
			httpResponse=invoke(httpGet);
			return Integer.parseInt(EntityUtils.toString(httpResponse.getEntity()));
		} catch (Exception e) {
			LOGGER.error("request to " + httpGet.getURI().toString()
					+ " failed ", e);
			return 0;
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
}
