package com.babeeta.butterfly.application.third.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import com.babeeta.butterfly.application.third.service.app.AppServiceResult;
import com.babeeta.butterfly.application.third.service.app.OppoAppServiceImpl;
import com.babeeta.butterfly.application.third.service.words.FilterWordsService;

/***
 * 为oppo定制的服务
 * 
 * @author zeyong.xia
 * @date 2011-9-23
 */
@Controller
@Path("/1/api/message")
@Scope(value = "prototype")
public class OppoMessageResource {
	private final static Logger logger = LoggerFactory
			.getLogger(OppoMessageResource.class);

	private MessageConfig userMessageConfig;

	private MessageConfig groupMessageConfig;

	private OppoAppServiceImpl oppoAppServiceImpl;

	private FilterWordsService filterWordsServiceImpl;

	private static final int MAX_LEN = 8 * 1024;

	private String[] getAuthContent(String authorization) {
		try {
			String base64Content = authorization.split(" ")[1];
			String authContent = new String(Base64.decodeBase64(base64Content),
					"UTF-8");
			logger.debug(
					"[OppoMessageResource]getAuthContent aid is{},key is{}",
					authContent.split(":")[0], authContent.split(":")[1]);
			return authContent.split(":");
		} catch (Exception e) {
			logger.error("[authorization header] {}", e.getMessage());
			return null;
		}
	}

	/***
	 * 广播组播
	 * 
	 * @return
	 */
	@POST
	@Path("/broadcast/{tagExpression}/{messageInfo}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("text/plain;charset=UTF-8")
	public Response pushToBroadcast(
			@PathParam("tagExpression") String tagExpression,
			@PathParam("messageInfo") PathSegment messageInfo,
			@HeaderParam("Authorization") String authorization,
			byte[] messageContent) {
		try {
			return broadcast(tagExpression, messageInfo, authorization, messageContent);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return Response.status(50).entity("server internal error").build();
		}
	}
	private Response broadcast(String tagExpression,PathSegment messageInfo,String authorization,byte[] messageContent){

		logger.debug("[OppoMessageResource] pushToBroadcast to {}",
				tagExpression);
		int lifeValue = groupMessageConfig.getLifeValueDefault();
		int delayValue = groupMessageConfig.getDelayValueDefault();
		MessageContentType contentType = null;
		String[] params = messageInfo.toString().split(";");
		String life = messageInfo.getMatrixParameters().getFirst("life");
		String delay = messageInfo.getMatrixParameters().getFirst("delay");
		String type = messageInfo.getMatrixParameters().getFirst("type");
		if (params != null && params.length > 0) {
			for (String p : params) {
				if (p.contains("delay")) {
					delay = p.substring(p.indexOf("=") + 1);
				}
				if (p.contains("life")) {
					life = p.substring(p.indexOf("=") + 1);
				}
				if (p.contains("type")) {
					type = p.substring(p.indexOf("=") + 1);
				}
			}
		}
		logger
				.debug(
						"[OppoMessageResource] pushToBroadcast ,life is {} ,delay is {},type is {} ",
						new Object[] { life, delay, type });
		String appId = getAuthContent(authorization)[0];
		String field = this.oppoAppServiceImpl.hasField(appId, "life");
		if (field == "") {
			logger
					.debug(
							"[OppoMessageResource] pushToBroadcast to {}, {} has no {} ",
							new Object[] { tagExpression, appId, "life" });
			// return Response.status(403).build();
			life = "1";
		}

		if (type == null || type.equals("") || type == "") {
			logger
					.debug(
							"[OppoMessageResource] pushToBroadcast to {}, type is null",
							tagExpression);
			return Response.status(406).build();
		}

		contentType = MessageContentType.getByTag(type);

		if (contentType == null) {
			logger.debug("pushToBroadcast to {}, contentType is null",
					tagExpression);
			return Response.status(406).build();
		}

		if (messageContent.length == 0) {
			logger.debug("pushToBroadcast to {}, messageContent.length == 0",
					tagExpression);
			return Response.status(422).build();
		}

		if (messageContent.length > groupMessageConfig.getLengthValueMax()) {
			return Response.status(413).build();
		}

		if (life != null) {
			if (field.contains("custom")) {
				if (life.equals("never") || life == "never") {
					lifeValue = -1;
				} else {
					try {
						// lifeValue =
						// (groupMessageConfig.verifyLifeValue(Integer
						// .valueOf(life)))*24;
						try {
							lifeValue = (groupMessageConfig
									.verifyLifeValue(Integer
											.valueOf(life)));
						} catch (Exception e) {
							// lifeValue=24;
							lifeValue = 1;
						}
					} catch (Exception e) {
						// lifeValue = 24;
						lifeValue = 1;
					}

				}
			} else {
				// lifeValue=24;
				lifeValue = 1;
			}
			logger.debug(
					"[oppoMessageResource] pushToBroadcast lifeValue is {}",
					lifeValue);
			// lifeValue = groupMessageConfig.verifyLifeValue(Integer
			// .valueOf(life));
		}

		if (delay != null && delay != "0") {
			try {
				delayValue = groupMessageConfig.verifyDelayValue(Integer
						.valueOf(delay));
			} catch (Exception e) {
				delayValue = 0;
			}
			// xaizeyong add filter message
			if (contentType.equals(MessageContentType.TYPE_TEXT)) {
				// AppServiceResult
				// res=filterWordsServiceImpl.filterMessage(messageContent.toString());
				// if(!res.isSuccess())
				// {
				// return Response.status(res.getStatusCode()).build();
				// }
			}
		}

		MessageType messageType = contentType
				.equals(MessageContentType.TYPE_BINARY) ? MessageType.BINARY
				: MessageType.JSON;

		MessageContext mtx = new MessageContext(messageType,
				messageContent);
		mtx.setLife(lifeValue);
		mtx.setDelay(delayValue);
		logger.debug("[oppoMessageResource] pushToBroadcast start pushing");
		AppServiceResult result = oppoAppServiceImpl.pushBroadcastMessage(mtx,
				appId,
				tagExpression);

		if (result.isSuccess()) {
			return Response.status(200).entity(result.getMessageId()).build();
		} else {
			logger.debug("[OppoMessageResource] pushToBroadcast  fail");
			return Response.status(result.getStatusCode()).build();
		}
	
	}

	@POST
	@Path("/client/{clientId}/{messageInfo}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("text/plain;charset=UTF-8")
	public Response pushToClient(
			@PathParam("clientId") String clientId,
			@PathParam("messageInfo") PathSegment messageInfo,
			@HeaderParam("Authorization") String authorization,
			byte[] messageContent) {
		
		try {
			return unicast(clientId, messageInfo, authorization, messageContent);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return Response.status(500).entity("server internal error").build();
		}
	}

	private Response unicast(String clientId, PathSegment messageInfo,String authorization,byte[] messageContent){

		logger.debug("[OppoMessageResource] pushToClient to {}", clientId);
		int lifeValue = userMessageConfig.getLifeValueDefault();
		int delayValue = userMessageConfig.getDelayValueDefault();
		MessageContentType contentType = null;
		logger.debug(
				"[OppoMessageResource] pushToClient to {},messageInfo is {}",
				clientId, messageInfo);
		String life = messageInfo.getMatrixParameters().getFirst("life");
		String delay = messageInfo.getMatrixParameters().getFirst("delay");
		String type = messageInfo.getMatrixParameters().getFirst("type");
		String[] params = messageInfo.toString().split(";");
		if (params != null && params.length > 0) {
			for (String p : params) {
				if (p.contains("delay")) {
					delay = p.substring(p.indexOf("=") + 1);
				}
				if (p.contains("life")) {
					life = p.substring(p.indexOf("=") + 1);
				}
				if (p.contains("type")) {
					type = p.substring(p.indexOf("=") + 1);
				}
			}
		}
		logger
				.debug(
						"[OppoMessageResource] pushToClient ,life is {} ,delay is {},type is {} ",
						new Object[] { life, delay, type });
		String appId = getAuthContent(authorization)[0];
		String field = this.oppoAppServiceImpl.hasField(appId, "life");
		if (field == "") {
			logger.debug(
					"[OppoMessageResource] pushToClient to {}, {} has no {} ",
					new Object[] { clientId, appId, "life" });
			life = "1";
		}

		if (clientId == null || clientId == "" || "".equals(clientId)) {
			logger.debug("[OppoMessageResource] pushToClient clientId is null");
			return Response.status(404).build();
		}
		if (type == null || type.equals("") || type == "") {
			logger.debug(
					"[OppoMessageResource] pushToClient to {},type is null",
					clientId);
			return Response.status(406).build();
		}

		contentType = MessageContentType.getByTag(type);

		if (contentType == null || contentType.equals("")) {
			logger
					.debug(
							"[OppoMessageResource] pushToClient to {},contentType is null",
							clientId);
			return Response.status(406).build();
		}

		if (messageContent.length == 0) {
			logger
					.debug(
							"[OppoMessageResource] pushToClient to {},messageContent is null",
							clientId);
			return Response.status(422).build();
		}

		if (messageContent.length > userMessageConfig.getLengthValueMax()) {
			return Response.status(413).build();
		}
		logger
				.debug(
						"[OppoMessageResource] field is {},lifeValue is {} ,life is {}",
						new Object[] { field, lifeValue, life });
		if (life != null) {
			if (field.contains("custom")) {
				if (life.equals("never") || life == "never") {
					lifeValue = -1;
				} else {
					// lifeValue = (groupMessageConfig.verifyLifeValue(Integer
					// .valueOf(life)))*24;
					try {
						lifeValue = (groupMessageConfig.verifyLifeValue(Integer
								.valueOf(life)));
					} catch (Exception e) {
						lifeValue = 1;
					}
				}

			} else {
				// return Response.status(404).build();
				// lifeValue=24;
				lifeValue = 1;
			}
			// lifeValue = userMessageConfig.verifyLifeValue(Integer
			// .valueOf(life));
		}
		logger.debug(
				"[OppoMessageResource] pushToClient to {},lifeValue is {}",
				clientId, lifeValue);
		if (delay != null) {
			try {
				delayValue = groupMessageConfig.verifyDelayValue(Integer
						.valueOf(delay));
			} catch (Exception e) {
				delayValue = 0;
			}
			// xaizeyong add filter message
			if (contentType.equals(MessageContentType.TYPE_TEXT)) {
				// 过滤敏感词
				// AppServiceResult
				// res=filterWordsServiceImpl.filterMessage(messageContent.toString());
				// if(!res.isSuccess())
				// {
				// return Response.status(res.getStatusCode()).build();
				// }
			}
		}
		MessageType messageType = contentType
				.equals(MessageContentType.TYPE_BINARY) ? MessageType.BINARY
				: MessageType.JSON;

		MessageContext mtx = new MessageContext(messageType,
					messageContent);

		mtx.setLife(lifeValue);
		mtx.setDelay(delayValue);

		AppServiceResult result = oppoAppServiceImpl.pushMessage(mtx, appId,
				clientId);

		if (result.isSuccess()) {
			return Response.status(200).entity(result.getMessageId()).build();
		} else {
			logger.debug("[OppoMessageResource] pushToClient fail");
			return Response.status(result.getStatusCode()).build();
		}

	
	}
	
	/***
	 * 变更目的地
	 * 
	 * @return
	 */
	@PUT
	@Path("/client/change/{oldCid}/{newCid}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("text/plain;charset=UTF-8")
	public Response changeRecipient(@PathParam("oldCid") String oldCid,
			@PathParam("newCid") String newCid,
			@HeaderParam("Authorization") String authorization) {
		logger
				.debug(
						"[OppoMessageResource] changeRecipient from {} to {},authorization is {}",
						new Object[] { oldCid, newCid, authorization });
		if (oldCid == null || oldCid.equals("") || oldCid == "") {
			logger
					.debug(
							"[OppoMessageResource] changeRecipient from {} to {},oldCid is null",
							oldCid, newCid);
			return Response.status(404).build();
		}
		if (newCid == null || newCid.equals("") || newCid == "") {
			logger
					.debug(
							"[OppoMessageResource] changeRecipient from {} to {},newCid is null",
							oldCid, newCid);
			return Response.status(404).build();
		}
		String appId = getAuthContent(authorization)[0];
		logger.debug("[OppoMessageResource] appId is {}", appId);
		if (appId == null || appId.equals("") || appId == "") {
			logger
					.debug(
							"[OppoMessageResource] changeRecipient from {} to {},aid is null",
							oldCid, newCid);
			return Response.status(404).build();
		}
		logger
				.debug(
						"[OppoMessageResource] changeRecipient from {} to {} ,aid is {}",
						new Object[] { oldCid, newCid, appId });
		AppServiceResult result = this.oppoAppServiceImpl.changeRecipient(
				oldCid, newCid, appId);
		if (result.isSuccess()) {
			return Response.status(200).build();
		} else {
			logger.debug("[OppoMessageResource] changeRecipient fail");
			return Response.status(result.getStatusCode()).build();
		}
	}

	/***
	 *  修改消息体
	 * 
	 * @return
	 */
	@PUT
	@Path("/update/{messageId}/type={type}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response modifyMessageContext(
			@PathParam("messageId") String messageId,
			@PathParam("type") String type,
			@HeaderParam("Authorization") String authorization,
			byte[] messageContent) {
		logger
				.debug(
						"[OppoMessageResource] modifyMessageContext ,messageId is {},type is {}",
						messageId, type);
		MessageContentType contentType = null;
		if (messageId == null || "".equals(messageId) || messageId == "") {
			return Response.status(404).build();
		}
		if (type == null || type == "") {
			return Response.status(406).build();
		}
		contentType = MessageContentType.getByTag(type);

		if (contentType == null) {
			return Response.status(406).build();
		}
		if (messageContent == null || messageContent.length == 0) {
			logger
					.debug(
							"[OppoMessageResource] modifyMessageContext ,messageId is {},messageContent is null",
							messageId);
			return Response.status(422).build();
		}
		if (messageContent.length > MAX_LEN) {
			return Response.status(413).build();
		}
		String appId = getAuthContent(authorization)[0];
		AppServiceResult result = oppoAppServiceImpl.modifyMessageContent(
				appId, messageId,
				contentType.getTag(), messageContent);

		if (result.isSuccess()) {
			return Response.status(200).build();
		} else {
			logger.debug("[OppoMessageResource] modifyMessageContext fail");
			return Response.status(result.getStatusCode()).build();
		}

	}

	/***
	 * 删除消息
	 * 
	 * @return
	 */
	@DELETE
	@Path("/delete/{messageId}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("text/plain;charset=UTF-8")
	public Response deleteMessage(@PathParam("messageId") String messageId,
			@HeaderParam("Authorization") String authorization) {
		logger.debug("[OppoMessageResource] deleteMessage ,messageId is {}",
				messageId);
		if (messageId == null || "".equals(messageId) || messageId == "") {
			return Response.status(404).build();
		}
		String appId = getAuthContent(authorization)[0];
		AppServiceResult result = oppoAppServiceImpl.deleteMessage(appId,
				messageId);

		if (result.isSuccess()) {
			return Response.status(200).build();
		} else {
			logger.debug("[OppoMessageResource] deleteMessage fail");
			return Response.status(result.getStatusCode()).build();
		}
	}

	/***
	 * 查询消息状态
	 * 
	 * @return
	 */
	@GET
	@Path("/query/{messageId}")
	@Produces("text/plain;charset=UTF-8")
	public Response queryMessageStatus(
			@PathParam("messageId") String messageId,
			@HeaderParam("Authorization") String authorization) {
		logger.debug(
				"[OppoMessageResource] queryMessageStatus ,messageId is {}",
				messageId);
		if (messageId == null || messageId == "" || "".equals(messageId)) {
			return Response.status(404).build();
		}
		String appId = getAuthContent(authorization)[0];
		AppServiceResult result = oppoAppServiceImpl.queryMessageStatus(appId
				, messageId);

		if (result.isSuccess()) {
			logger.debug("[OppoMessageResource]queryMessageStatus,result={}",
					result.getMessageStatus());
			return Response.status(200).entity(result.getMessageStatus())
					.build();
		} else {
			logger.debug("[OppoMessageResource] queryMessageStatus fail");
			return Response.status(result.getStatusCode()).build();
		}
	}

	public void setOppoAppServiceImpl(OppoAppServiceImpl oppoAppServiceImpl) {
		this.oppoAppServiceImpl = oppoAppServiceImpl;
	}

	public void setFilterWordsServiceImpl(
			FilterWordsService filterWordsServiceImpl) {
		this.filterWordsServiceImpl = filterWordsServiceImpl;
	}

	public void setUserMessageConfig(MessageConfig userMessageConfig) {
		this.userMessageConfig = userMessageConfig;
	}

	public void setGroupMessageConfig(MessageConfig groupMessageConfig) {
		this.groupMessageConfig = groupMessageConfig;
	}

}
