package com.babeeta.butterfly.application.third.service.app;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.third.resource.MessageContext;
import com.babeeta.butterfly.application.third.service.AbstractHttpRPCService;

public class OppoAppServiceImpl extends AbstractHttpRPCService implements
		OppoAppService {

	private static final Logger logger = LoggerFactory
			.getLogger(OppoAppServiceImpl.class);

	private static final String APP_SERVICE_HOST = "app.";

	/***
	 * 推送单条消息
	 * 
	 * @param message
	 * @param sender
	 * @param recipient
	 * @return
	 */
	public AppServiceResult pushMessage(MessageContext message, String sender,
			String recipient) {
		logger.debug(
				"[OppoAppServiceImpl] pushSingleMessage, recipient is {} ",
				recipient);
		HttpPost httpPost = new HttpPost("/message/push/single/" + sender + "/"
				+ recipient);
		httpPost.addHeader("delay", "" + message.getDelay());
		httpPost.addHeader("exptime", "" + message.getLife());
		httpPost.addHeader("DataType", message.getContentType().toString());
		httpPost.setHeader("Content-type", "application/octet-stream");
		ByteArrayEntity byteArray = new ByteArrayEntity(message.getContent());
		httpPost.setEntity(byteArray);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpPost);
			String responseString = EntityUtils.toString(httpResponse
					.getEntity());
			logger
					.debug(
							"[OppoAppServiceImpl] pushSingleMessage, responseString is {} ",
							responseString);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				logger.error("request to {} failed with response status 404",
						httpPost.getURI().toString());
				return new AppServiceResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				AppServiceResult result = new AppServiceResult(true, 200);
				result.setMessageId(responseString);
				return result;
			} else {
				logger
						.error(
								"request to {} failed with response status {},response body {}",new Object[]{
								httpPost.getURI().toString(), httpResponse
										.getStatusLine().getStatusCode(),responseString});
				return new AppServiceResult(false, 500);
			}

		} catch (Exception e) {
			logger.error("[OppoAppServiceImpl] pushSingleMessage "
					+ httpPost.getURI().getPath() + " failed.", e);
			return new AppServiceResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			httpPost.abort();
		}
	}

	/***
	 * 广播组播
	 * 
	 * @param message
	 * @param sender
	 * @param recipient
	 * @return
	 */
	public AppServiceResult pushBroadcastMessage(MessageContext message,
			String sender, String recipient) {
		logger.debug(
				"[OppoAppServiceImpl] pushBroadcastMessage, recipient is {} ",
				recipient);
		HttpPost httpPost = new HttpPost("/message/push/group/" + sender + "/"
				+ recipient);
		httpPost.addHeader("delay", "" + message.getDelay());
		httpPost.addHeader("exptime", "" + message.getLife());
		httpPost.addHeader("DataType", message.getContentType().toString());
		httpPost.setHeader("Content-type", "application/octet-stream");
		ByteArrayEntity byteArray = new ByteArrayEntity(message.getContent());
		httpPost.setEntity(byteArray);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpPost);
			logger
					.debug(
							"[OppoAppServiceImpl] pushBroadcastMessage, responseString  is {} ",
							httpResponse.getStatusLine());
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return new AppServiceResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				AppServiceResult result = new AppServiceResult(true, 200);
				result.setMessageId(EntityUtils.toString(httpResponse
						.getEntity()));
				return result;
			} else {
				return new AppServiceResult(false, 500);
			}

		} catch (Exception e) {
			LOGGER.error("[pushBroadcastMessage] "
					+ httpPost.getURI().getPath() + " failed.", e);
			return new AppServiceResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			httpPost.abort();
		}
	}

	/***
	 * 查询消息状态
	 * 
	 * @param messageId
	 * @return
	 */
	public AppServiceResult queryMessageStatus(String appId, String messageId) {
		HttpGet httpGet = new HttpGet("/message/query/" + appId + "/"
				+ messageId);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return new AppServiceResult(false, 404);
			} else if (httpResponse.getStatusLine().getStatusCode() == 200) {
				AppServiceResult result = new AppServiceResult(true, 200);
				result.setMessageStatus(EntityUtils.toString(httpResponse
						.getEntity()));
				return result;
			} else {
				return new AppServiceResult(false, 500);
			}

		} catch (Exception e) {
			LOGGER.error("[queryMessageStatus] " + httpGet.getURI().getPath()
					+ " failed.", e);
			return new AppServiceResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			httpGet.abort();
		}
	}

	/***
	 * 删除消息
	 * 
	 * @param messageId
	 * @return
	 */
	public AppServiceResult deleteMessage(String appId, String messageId) {
		HttpDelete httpDelete = new HttpDelete("/message/delete/" + appId + "/"
				+ messageId);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpDelete);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return new AppServiceResult(true, 200);
			} else {
				return new AppServiceResult(false, httpResponse.getStatusLine()
						.getStatusCode());
			}

		} catch (Exception e) {
			LOGGER.error("[deleteMessage] " + httpDelete.getURI().getPath()
					+ " failed.", e);
			return new AppServiceResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			httpDelete.abort();
		}
	}

	/****
	 * 修改消息
	 * 
	 * @param appId
	 * @param messageId
	 * @param dataType
	 * @param content
	 * @return
	 */
	public AppServiceResult modifyMessageContent(String appId,
			String messageId, String dataType, byte[] content) {
		HttpPut httpPut = new HttpPut("/message/update/" + appId + "/"
				+ messageId);

		httpPut.addHeader("DataType", dataType);
		httpPut.setHeader("Content-type", "application/octet-stream");
		ByteArrayEntity byteArray = new ByteArrayEntity(content);
		httpPut.setEntity(byteArray);

		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpPut);
			String responseString = EntityUtils.toString(httpResponse
					.getEntity());
			logger.debug("response: {}", responseString);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return new AppServiceResult(true, 200);
			} else {
				return new AppServiceResult(false, httpResponse.getStatusLine()
						.getStatusCode());
			}

		} catch (Exception e) {
			LOGGER.error("[modifyMessageContent] " + httpPut.getURI().getPath()
					+ " failed.", e);
			return new AppServiceResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			httpPut.abort();
		}
	}

	/****
	 * 更改目的地
	 * 
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 * @return
	 */
	public AppServiceResult changeRecipient(String oldCid, String newCid,
			String aid) {

		HttpPut httpPut = new HttpPut("/change/recipilent/" + oldCid + "/"
				+ newCid);

		// httpPut.addHeader("DataType", dataType);
		httpPut.setHeader("Content-type", "application/octet-stream");
		// String json="{\"id\":\""+aid+"\"}";
		ByteArrayEntity byteArray = new ByteArrayEntity(aid.getBytes());
		httpPut.setEntity(byteArray);
		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpPut);
			String responseString = EntityUtils.toString(httpResponse
					.getEntity());
			logger.debug("response: {}", responseString);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return new AppServiceResult(true, 200);
			} else {
				return new AppServiceResult(false, httpResponse.getStatusLine()
						.getStatusCode());
			}

		} catch (Exception e) {
			LOGGER.error("[modifyMessageContent] " + httpPut.getURI().getPath()
					+ " failed.", e);
			return new AppServiceResult(false, 500);
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			httpPut.abort();
		}

	}

	/****
	 * 判断某应用是否有某权限
	 * 
	 * @param oldCid
	 * @param newCid
	 * @param aid
	 * @return
	 */
	public String hasField(String aid, String field) {
		HttpPut httpPut = new HttpPut("/api/hasfield/" + aid + "/" + field);
		httpPut.setHeader("Content-type", "application/octet-stream");
		HttpResponse httpResponse = null;
		try {
			httpResponse = invoke(httpPut, "accounts.app");
			String responseString = EntityUtils.toString(httpResponse
					.getEntity());
			logger.debug("response: {}", responseString);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return responseString;
			} else {
				return "";
			}

		} catch (Exception e) {
			LOGGER.error("[modifyMessageContent] " + httpPut.getURI().getPath()
					+ " failed.", e);
			return "";
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			httpPut.abort();
		}
	}

	/***
	 * 应用注册
	 * 
	 * @param map
	 * @return
	 */
	public String registerApp(Map<String, Object> map) {

		HttpPost post = new HttpPost("/1/api/register/");
		post.setHeader("Content-type", "application/json");
		HttpResponse httpResponse = null;
		StringBuffer json = new StringBuffer("{\"id\":\"111\",\"extra\":{");
		if (map != null && map.size() > 0) {

			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = (String) map.get(key);
				json.append("\"" + key + "\":\"" + value + "\"");
				json.append(",");
			}
			json.substring(0, json.length() - 1);
			json.append("}}");
			post.setEntity(new ByteArrayEntity(json.toString().getBytes()));
		}
		try {
			httpResponse = invoke(post, "accounts.app");
			String responseString = EntityUtils.toString(httpResponse
					.getEntity());
			logger.debug("response: {}", responseString);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return responseString;
			} else {
				return "";
			}

		} catch (Exception e) {
			LOGGER.error("[modifyMessageContent] " + post.getURI().getPath()
					+ " failed.", e);
			return "";
		} finally {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			post.abort();
		}

	}

	/***
	 * 主机地址
	 */
	@Override
	protected String getHost() {
		// TODO Auto-generated method stub
		return APP_SERVICE_HOST;
	}

}
