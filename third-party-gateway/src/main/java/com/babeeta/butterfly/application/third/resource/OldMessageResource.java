package com.babeeta.butterfly.application.third.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.babeeta.butterfly.application.third.service.app.AppServiceResult;
import com.babeeta.butterfly.application.third.service.app.OppoAppService;

@Controller
@Path("/service")
@Scope(value = "prototype")
public class OldMessageResource {
	private MessageConfig userMessageConfig;

	private final static Logger logger = LoggerFactory
			.getLogger(OldMessageResource.class);
	private OppoAppService oppoAppServiceImpl;

	public void setOppoAppServiceImpl(OppoAppService oppoAppServiceImpl) {
		this.oppoAppServiceImpl = oppoAppServiceImpl;
	}

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

	public void setUserMessageConfig(MessageConfig userMessageConfig) {
		this.userMessageConfig = userMessageConfig;
	}

	/******************************** PUSH MESSAGE ****************************/
	@POST
	@Path("/push/server/{clientId}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	public Response oldAppGatewayOldPushMessage(
			@PathParam("clientId") String clientId,
			@HeaderParam("exptime") int exptime,
			@HeaderParam("Authorization") String authorization,
			byte[] messageContent) {
		logger.debug("[OldMessageResource]oldPushMessage to {}.",
				clientId);
		if (messageContent.length == 0) {
			return Response.status(422).build();
		}

		if (messageContent.length > userMessageConfig.getLengthValueMax()) {
			return Response.status(413).build();
		}

		MessageType messageType = MessageType.BINARY;

		MessageContext mtx = new MessageContext(messageType,
					messageContent);

		mtx.setLife(exptime);
		mtx.setDelay(0);
		String appId = getAuthContent(authorization)[0];
		AppServiceResult result = oppoAppServiceImpl.pushMessage(mtx, appId, clientId);

		if (result.isSuccess()) {
			return Response.status(201).entity(result.getMessageId()).build();
		} else {
			return Response.status(result.getStatusCode()).build();
		}
	}

	@POST
	@Path("/client/{clientId}/message")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	public Response oldAppGatewayPushMessage(
			@PathParam("clientId") String clientId,
			@HeaderParam("exptime") int exptime,
			@HeaderParam("Authorization") String authorization,
			byte[] messageContent) {
		logger.debug("[OldMessageResource]pushMessage to {}.",
				clientId);
		if (messageContent.length == 0) {
			return Response.status(422).build();
		}

		if (messageContent.length > userMessageConfig.getLengthValueMax()) {
			return Response.status(413).build();
		}

		MessageType messageType = MessageType.BINARY;

		MessageContext mtx = new MessageContext(messageType,
					messageContent);

		mtx.setLife(exptime);
		mtx.setDelay(0);
		String appId = getAuthContent(authorization)[0];
		AppServiceResult result = oppoAppServiceImpl.pushMessage(mtx, appId, clientId);

		if (result.isSuccess()) {
			return Response.status(200).entity(result.getMessageId()).build();
		} else {
			return Response.status(result.getStatusCode()).build();
		}
	}

	/************************** QUERY MESSAGE STATUS **************************/
	@GET
	@Path("/client/all/{messageId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response oldAppGatewayMessageStatus(@HeaderParam("Authorization") String authorization,
			@PathParam("messageId") String messageId) {
		logger.debug("[OldMessageResource]queryMessageStatus [{}].",
				messageId);
		if (messageId == null) {
			return Response.status(404).build();
		}
		String appId = getAuthContent(authorization)[0];
		AppServiceResult result = oppoAppServiceImpl.queryMessageStatus(appId,messageId);

		if (result.isSuccess()) {
			return Response.status(200).entity(result.getMessageStatus())
					.build();
		} else {
			return Response.status(result.getStatusCode()).build();
		}
	}
}
