package com.babeeta.butterfly.application.third.service.tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.third.service.AbstractHttpRPCService;

/***
 * oppo Tag定制
 * 
 * @author zeyong.xia
 * @date 2011-9-23
 */
public class OppoTagServiceImpl extends AbstractHttpRPCService implements
        OppoTagService {

	private static final Logger logger = LoggerFactory
	        .getLogger(OppoTagServiceImpl.class);

	private static final String TAG_SERVICE_HOST = "tag.app";

	@Override
	protected String getHost() {
		// TODO Auto-generated method stub
		return TAG_SERVICE_HOST;
	}

	/***
	 * 打tag
	 * 
	 * @param clientId
	 * @param aid
	 * @param groupTag
	 * @return
	 */
	public TagResult setGroupTag(String clientId, String aid, String groupTag)
	{
		logger.debug(
		        "[OppoTagServiceImpl] setGroupTag cid is {} groupTag is {}",
		        clientId, groupTag);
		HttpPut httpPut = new HttpPut("/api/set-tag-for-client/" + aid + "/"
		        + clientId);

		httpPut.setHeader(HttpHeaders.CONTENT_TYPE,
		        MediaType.APPLICATION_OCTET_STREAM);

		try {
			httpPut.setEntity(new StringEntity(groupTag));
		} catch (UnsupportedEncodingException e1) {
			logger.error("set entity failed.", e1);
			return new TagResult(false, 500);
		}

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpPut);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return new TagResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 422) {
				return new TagResult(false, 422);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return new TagResult(true, 200);
			}
			else if (httpResponse.getStatusLine().getStatusCode() == 417) {
				return new TagResult(false, 417);
			} else {
				return new TagResult(false, httpResponse.getStatusLine()
				        .getStatusCode());
			}

		} catch (Exception e) {
			logger.error("[setGroupTag] "
			        + httpPut.getURI().getPath() + " failed.", e);
			return new TagResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			httpPut.abort();
		}
	}

	/***
	 * 移除tag
	 * 
	 * @param clientId
	 * @param aid
	 * @param groupTag
	 * @return
	 */
	public TagResult removeGroupTag(String clientId, String aid, String groupTag)
	{
		logger.debug(
		        "[OppoTagServiceImpl] removeGroupTag aid is {} cid is {} groupTag is",
		        new Object[] { aid, clientId, groupTag });
		HttpDelete httpDelete = new HttpDelete("/api/remove-client-tag/" + aid
		        + "/" + clientId + "/" + groupTag);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpDelete);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return new TagResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 422) {
				return new TagResult(false, 422);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return new TagResult(true, 200);
			} else {
				return new TagResult(false, 500);
			}

		} catch (Exception e) {
			logger.error("[removeGroupTag] "
			        + httpDelete.getURI().getPath() + " failed.", e);
			return new TagResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			httpDelete.abort();
		}
	}

	/***
	 * 查询tag信息
	 * 
	 * @param clientId
	 * @param aid
	 * @return
	 */
	public TagResult listGroupTag(String clientId, String aid)
	{
		logger.debug("[OppoTagServiceImpl] listGroupTag aid is {} cid is {}",
		        aid, clientId);

		HttpGet httpGet = new HttpGet("/api/get-client-tag-list/" + aid + "/"
		        + clientId);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return new TagResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 204) {
				return new TagResult(true, 204);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				TagResult result = new TagResult(true, 200);
				String[] tagList = (String[]) JSONArray.fromObject(
				        EntityUtils
				                .toString(httpResponse.getEntity()))
				        .toArray(
				                new String[] {});
				result.setStringList(tagList);
				return result;
			} else {
				return new TagResult(false, 500);
			}

		} catch (Exception e) {
			logger.error("[listGroupTag] "
			        + httpGet.getURI().getPath() + " failed.", e);
			return new TagResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			httpGet.abort();
		}
	}

	/***
	 * 查询tag信息
	 * 
	 * @param groupTag
	 * @return
	 */
	public TagResult listDevice(String groupTag, String aid)
	{
		logger.debug(
		        "[OppoTagServiceImpl] listDevice aid is {} groupTag is {}",
		        aid, groupTag);
		HttpGet httpGet = new HttpGet("/api/get-tag-client-list/" + aid + "/"
		        + groupTag);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return new TagResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				TagResult result = new TagResult(true, 200);
				String[] tagList = (String[]) JSONArray.fromObject(
				        EntityUtils
				                .toString(httpResponse.getEntity()))
				        .toArray(
				                new String[] {});
				result.setStringList(tagList);
				return result;
			} else {
				return new TagResult(false, 500);
			}
		} catch (Exception e) {
			logger.error("[listDevice] "
			        + httpGet.getURI().getPath() + " failed.", e);
			return new TagResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			httpGet.abort();
		}
	}

	@Override
	public TagResult cleanTag(String aid, String tName) {
		logger.debug(
		        "[OppoTagServiceImpl] cleanTag aid is {} groupTag is",
		        new Object[] { aid, tName });
		HttpDelete httpDelete = new HttpDelete("/api/clean-tag/" + aid + "/"
		        + tName);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpDelete);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return new TagResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 422) {
				return new TagResult(false, 422);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return new TagResult(true, 200);
			} else {
				return new TagResult(false, 500);
			}

		} catch (Exception e) {
			logger.error("[cleanTag] "
			        + httpDelete.getURI().getPath() + " failed.", e);
			return new TagResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			httpDelete.abort();
		}
	}
}
