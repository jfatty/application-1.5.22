package com.babeeta.butterfly.app.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.app.ThirdAppGatewayServer;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.babeeta.butterfly.application.reliable.ReliablePushImpl;
import com.google.protobuf.ByteString;

public class PushMessageAppServlet extends AbstractAppServlet {
	private static final long serialVersionUID = 3114986021777312107L;

	private final static Logger logger = LoggerFactory
			.getLogger(PushMessageAppServlet.class);

	protected Pattern pattern = Pattern
			.compile("/service/client/([a-z0-9A-Z]{32}|[a-z0-9A-Z]{64})/(\\bmessage\\b|\\binbox\\b)");

	private final Pattern digitalPattern = Pattern.compile("\\d+");

	private static final int MAX_EXPIRE_TIME = 60 * 60 * 24;// 过期时间最多1天(86400秒)

	private HttpClient client;

	private HttpPost httpPost;

	public PushMessageAppServlet() {

		requestOptions();

	}

	@Override
	protected boolean doVerify(RequestContext rtx, HttpServletRequest arg0,
			HttpServletResponse arg1) throws IOException {
		if (!verifyURIPattern(arg0, arg1)) {
			return false;
		}
		if (!verifyHeader(rtx, arg0, arg1)) {
			return false;
		}
		if (!verifyRequestBody(rtx, arg0, arg1)) {
			return false;
		}
		return true;
	}

	@Override
	protected void rpcInvoke(final RequestContext rtx, final AsyncContext ctx,
			final HttpServletRequest request, final HttpServletResponse response) {
		String[] authContent = rtx.authContent;
		String requestURI = request.getRequestURI();

		final String appId = authContent[0];
		final String appKey = authContent[1];
		final String clientId = getClientId(requestURI);

		final int exptime = getExptime(request);

		String json = "{\"id\":\"" + appId + "\",\"secureKey\":\"" + appKey
				+ "\"}";
		httpPost.setEntity(new ByteArrayEntity(json.getBytes()));

		try {
			HttpResponse httpResponse = client.execute(httpPost);

			if (HttpStatus.SC_OK == httpResponse.getStatusLine()
					.getStatusCode()) {
				JSONObject obj = JSONObject.fromObject(EntityUtils
						.toString(httpResponse.getEntity()));
				if ("OK".equalsIgnoreCase(obj.get("status").toString())) {
					String strUUID = java.util.UUID.randomUUID().toString()
							.replaceAll("-", "");
					MessageRouting.Message.Builder builder = MessageRouting.Message
							.newBuilder()
							.setDate(System.currentTimeMillis())
							.setExpire(exptime)
							.setFrom(appId + "@" + ThirdAppGatewayServer.DOMAIN)
							.setUid(strUUID);

					MessageRouting.Message msg = null;

					builder.setContent(ByteString.copyFrom(rtx.body));

					builder.setTo(new StringBuilder(clientId).append(".")
								.append(appId)
								.append("@dev").toString());

					msg = builder.build();
					ReliablePush reliablePush = ReliablePushImpl
								.getDefaultInstance();
					boolean saveResult = reliablePush.saveMessage(msg,
								appId,
								clientId);
					logger.debug("[{}] ReliablePush[{}] [{}]",
								new Object[] {
										strUUID,
										saveResult,
										new StringBuilder(appId).append(".")
												.append(clientId)
												.toString() });
					if (!saveResult) {
						logger.warn("[{}]failed to persistence.", strUUID);
					}

					ThirdAppGatewayServer.send(msg);
					response.getWriter().write(strUUID);
					logger.info("[{}]sending [{}]  to [{}]", new Object[] {
							appId, strUUID, clientId });
				} else if ("FREEZED".equals(obj.get("status").toString())) {
					response.setStatus(HttpStatus.SC_FORBIDDEN);
					response.getWriter().write("APPLICATION_ID_FREEZED");

				} else {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.getWriter().write("UNAUTHORIZED_ID_AND_KEY");
				}
			} else {
				logger.error("[{}]-[{}]Authentication failed.", appId, clientId);
				response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			logger.error("[{}]-[{}]Push failed.{}", new Object[] { appId,
					clientId, e.getMessage() });
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
	}

	/**
	 * 获取authorization头信息
	 * 
	 * @param authorization
	 * @return
	 */
	private String[] getAuthContent(String authorization) {
		try {
			String base64Content = authorization.split(" ")[1];
			String authContent = new String(Base64.decodeBase64(base64Content),
					"UTF-8");
			return authContent.split(":");
		} catch (Exception e) {
			logger.error("[authorization header] {}", e.getMessage());
			return null;
		}
	}

	private String getClientId(String url) {
		String clientId = "";
		Matcher m = pattern.matcher(url);
		if (m.find()) {
			clientId = m.group(1);
		}
		return clientId;
	}

	private int getExptime(HttpServletRequest request) {
		String exptimeStr = request.getHeader("exptime");
		if (exptimeStr == null) {
			// exptime为空或不是数字，返回0
			return MAX_EXPIRE_TIME;
		}
		if (!digitalPattern.matcher(exptimeStr).matches()) {
			return 0;
		}
		int exptime = Integer.parseInt(exptimeStr);
		if (exptime < 0) {
			exptime = 0;
		}
		if (exptime > MAX_EXPIRE_TIME) {
			exptime = MAX_EXPIRE_TIME;
		}
		return exptime;
	}

	/**
	 * 获取post的body
	 * 
	 * @param request
	 * @return
	 */
	private byte[] getRequestBody(RequestContext rtx, HttpServletRequest request) {
		byte[] body = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			InputStream is = request.getInputStream();

			int len = -1;
			while ((len = is.read(body)) != -1) {
				out.write(body, 0, len);
			}

		} catch (IOException e) {
			logger.error("[read request body] {}", e.getMessage());
		}
		byte[] ret = out.toByteArray();
		return ret;
	}

	private void requestOptions() {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
					new Scheme("http", PlainSocketFactory.getSocketFactory(),
							80));

		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be using the HttpClient.
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		client = new DefaultHttpClient(cm, params);
		httpPost = new HttpPost("http://accounts.app/api/auth");
		httpPost.setHeader(new BasicHeader("Content-type", "application/json"));
	}

	private boolean verifyHeader(RequestContext rtx,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		String[] authContent = getAuthContent(request
				.getHeader("Authorization"));
		if (authContent == null || authContent.length != 2
				|| authContent[0].trim().length() == 0
				|| authContent[1].trim().length() == 0) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write(STRING_INVALID_APP_ID);
			response.getWriter().flush();
			response.getWriter().close();
			return false;
		}
		rtx.authContent = authContent;
		return true;
	}

	private boolean verifyRequestBody(RequestContext rtx,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		// 获取请求的body体数据
		final byte[] body = getRequestBody(rtx, request);
		final String xTarget = request.getHeader(X_TARGET);
		String contentType = request.getContentType();
		if (request.getRequestURI().indexOf("inbox") > -1) {
			if (contentType == null
					|| !contentType.toLowerCase()
							.startsWith("application/json")) {

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write(STRING_CONTENT_MUST_BE_JSON);
				response.getWriter().flush();
				response.getWriter().close();
				return false;
			}
		}
		if ("IOS".equalsIgnoreCase(xTarget)) {
			if (body.length > 256) {
				response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
				response.getWriter().write(STRING_OVER_LENGTH);
				response.getWriter().flush();
				response.getWriter().close();
				return false;
			}
			if (contentType == null
					|| !contentType.toLowerCase()
							.startsWith("application/json")) {

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write(STRING_CONTENT_MUST_BE_JSON);
				response.getWriter().flush();
				response.getWriter().close();
				return false;
			}
		} else {
			if (body.length > ThirdAppGatewayServer.MSG_CONTENT_MAX_LENGTH) {
				response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
				response.getWriter().write(STRING_OVER_LENGTH);
				response.getWriter().flush();
				response.getWriter().close();
				return false;
			}
		}
		rtx.body = body;
		return true;
	}

	private boolean verifyURIPattern(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		String requestUrl = request.getRequestURI();
		logger.debug("[Request URL]" + requestUrl);
		Matcher m = pattern.matcher(requestUrl);
		if (!m.matches()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().flush();
			response.getWriter().close();
			return false;
		}
		return true;
	}
}
