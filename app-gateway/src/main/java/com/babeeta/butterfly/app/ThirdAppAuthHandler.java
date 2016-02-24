package com.babeeta.butterfly.app;

import javax.servlet.http.*;

import org.json.*;
import org.slf4j.*;

import com.babeeta.butterfly.*;
import com.babeeta.butterfly.app.servlet.*;
import com.babeeta.butterfly.application.reliable.*;
import com.babeeta.butterfly.rpc.*;
import com.google.protobuf.*;

/**
 * Created by IntelliJ IDEA. User: yinchong Date: 2010-12-1 Time: 11:38:51 To
 * change this template use File | Settings | File Templates.
 */
public class ThirdAppAuthHandler implements RPCHandler {
	private final static Logger logger = LoggerFactory
			.getLogger(ThirdAppAuthHandler.class);

	private final byte[] content;
	private final String aid;
	private final String cid;
	private final ThirdAppAuthListener listener;
	private final String xTarget;
	private final String msgType;

	public ThirdAppAuthHandler(String xTarget, String msgType, byte[] content,
			String aid, String cid, ThirdAppAuthListener listener) {
		this.content = content;
		this.aid = aid;
		this.cid = cid;
		this.listener = listener;
		this.xTarget = xTarget;
		this.msgType = msgType;
	}

	/**
	 * 验证异常，返回ERROR
	 * 
	 * @param message
	 *            验证的数据
	 * @param t
	 */
	@Override
	public void exceptionCaught(MessageRouting.Message message, Throwable t) {
		listener.onResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				AbstractAppServlet.STRING_ERROR);
		logger.error("[exceptionCaught]" + t.toString());
		return;
	}

	/**
	 * 向dev发送message数据
	 * 
	 * @param message
	 */
	@Override
	public void onMessage(MessageRouting.Message message) {
		String authResult = message.getContent().toStringUtf8();
		logger.debug("Authorization result [{}]. Time [{}ms]", authResult,
				(System.currentTimeMillis() - message.getDate()));
		if ("OK".equalsIgnoreCase(authResult)) {
			/**
			 * 验证成功,向dev发送message数据
			 */
			String strUUID = java.util.UUID.randomUUID().toString()
					.replaceAll("-", "");
			MessageRouting.Message.Builder builder = MessageRouting.Message
					.newBuilder().setDate(message.getDate())
					.setExpire(message.getExpire())
					.setFrom(aid + "@" + ThirdAppGatewayServer.DOMAIN)
					.setUid(strUUID);

			MessageRouting.Message msg = null;

			if ("inbox".equals(msgType)) {// 如果是inbox，将消息类型指定INBOX类型，content为json消息体
				String jsonContent = null;
				try {
					jsonContent = new String(content, "UTF-8");
					JSONObject inboxObj = new JSONObject(jsonContent);
					String inbox = inboxObj.getString("inbox");
					JSONObject inboxContent = new JSONObject(inbox);
					String title = inboxContent.getString("title");
					if (null == title || title.trim().length() == 0) {
						listener.onResponse(
								HttpServletResponse.SC_UNAUTHORIZED,
								AbstractAppServlet.STRING_TITLE_CANNOT_BE_EMPTY);
						return;
					}
				} catch (Exception e) {
					logger.error("[Inbox content error]" + e);
				}
				builder.setContent(ByteString.copyFromUtf8(jsonContent));
				builder.setMessageType(MessageRouting.MessageType.INBOX);
			} else {
				builder.setContent(ByteString.copyFrom(content));
			}

			if ("ios".equalsIgnoreCase(xTarget)) {
				builder.setTo(new StringBuilder(cid).append("@")
						.append("ios.dev").toString());
				msg = builder.build();
			} else {
				builder.setTo(new StringBuilder(cid).append(".").append(aid)
						.append("@dev").toString());
				msg = builder.build();
				ReliablePush reliablePush = ReliablePushImpl
						.getDefaultInstance();
				boolean saveResult = reliablePush.saveMessage(msg, aid, cid);
				logger.debug("[{}] ReliablePush[{}] [{}]", new Object[] {
						strUUID,
						saveResult,
						new StringBuilder(aid).append(".").append(cid)
								.toString() });
				if (!saveResult) {
					// TODO 处理没有存储成功的情况
				}
			}

			ThirdAppGatewayServer.send(msg);
			listener.onResponse(HttpServletResponse.SC_OK, strUUID);
			return;
		} else {
			listener.onResponse(HttpServletResponse.SC_UNAUTHORIZED,
					AbstractAppServlet.STRING_INVALID_APP_ID);
			return;
		}
	}
}