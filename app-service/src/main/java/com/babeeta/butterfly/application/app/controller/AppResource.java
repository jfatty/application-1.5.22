package com.babeeta.butterfly.application.app.controller;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.babeeta.butterfly.application.app.MessageContext;
import com.babeeta.butterfly.application.app.pusher.MessagePusher;
import com.babeeta.butterfly.application.app.record.MessageRecordService;
import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.service.ChangeRecipientService;
import com.babeeta.butterfly.application.app.service.DelayMessageTask;
import com.babeeta.butterfly.application.app.service.DelayMessageTaskService;
import com.babeeta.butterfly.application.app.service.MessageCanNotModifyException;
import com.babeeta.butterfly.application.app.service.MessageNotFoundException;
import com.babeeta.butterfly.application.app.service.MessageService;
import com.babeeta.butterfly.application.app.service.NamedService;
import com.babeeta.butterfly.application.app.service.impl.DelayMessageTaskServiceImpl;
import com.babeeta.butterfly.application.reliable.MessageStatus;

@Path("/")
@Scope(value = "prototype")
@Component("appResource")
public class AppResource {
	private final static Logger logger = LoggerFactory
			.getLogger(AppResource.class);

	private ChangeRecipientService changeRecipientServiceImpl;
	private MessageService messageService;
	private DelayMessageTaskService delayMessageTaskService;
	private NamedService namedService;
	private final int MSG_CONTENT_MAX_LENGTH = 8192;

	
	@POST
	@Path("/message/push/group/{sender}/{recipient}")
	@Consumes("application/octet-stream")
	@Produces("text/plain;charset=UTF-8")
	public Response pushGroupMessage(
			@HeaderParam("DataType") String dataType,
			@HeaderParam("exptime") int exptime,
			@HeaderParam("delay") int delay,
			@PathParam("sender") String sender,
			@PathParam("recipient") String recipient,
			byte[] content) {
		logger.debug("[AppResource]pushMessage to {}. delay is {}", recipient,
				delay);
		if (recipient == null || recipient == "" || sender == null
				|| sender == "") {
			return Response.status(404).build();
		}
		if (content == null || content.length == 0) {
			return Response.status(422).build();
		}

		if (content.length > MSG_CONTENT_MAX_LENGTH) {
			return Response.status(413).build();
		}
		boolean flag = this.changeRecipientServiceImpl.existsTag(sender,
				recipient);
		logger.debug("[AppResource]pushGroupMessage to {}. existsTag is {}",
				recipient, flag);
		if (!flag) {
			return Response.status(404).build();
		}
		String strUUID = java.util.UUID.randomUUID().toString()
				.replaceAll("-", "");
		MessageRecord messageRecord=new MessageRecord();
		messageRecord.setMessageId(strUUID);
		messageRecord.setParentId(strUUID);
		messageRecord.setAppId(sender);
		messageRecord.setRecipient(recipient);
		messageRecord.setDataType(dataType);
		messageRecord.setExpire(exptime);
		messageRecord.setDelay(delay);
		messageRecord.setContent(content);
		messageRecord.setBroadcastFlag(true);
		messageRecord.setCreateAt(new Date());
		
		logger.debug("[AppResource] pushGroupMessage delay is {}", delay);
		
		if (delay <= 0) {
			messageRecord.setStatus("DELIVERING");
		} else {
			messageRecord.setDelayUntil(DateUtils.addMinutes(new Date(), delay));
			messageRecord.setStatus("DELAYING");
			DelayMessageTask task = new DelayMessageTask(messageRecord
					.getMessageId(), delay);
			boolean setupSuccess= delayMessageTaskService.setupTask(task);
			
			if(setupSuccess){
				messageRecord.setDelayExecBy(namedService.getAppName());
			}
		}
		
		if (MessageRecordService.getDefaultInstance().getDao()
				.saveMessageRecord(messageRecord)) {
			if (delay <=0) {
				logger.debug("Push message [{}] immediately.", strUUID);
				MessagePusher.getDefaultInstance().broadcast(messageRecord);
			}
			return Response.status(200).entity(strUUID).build();
		} else {
			logger.error("save message {} failed.", strUUID);
			return Response.status(500).build();
		}
	}

	@POST
	@Path("/message/push/single/{sender}/{recipient}")
	@Consumes("application/octet-stream")
	@Produces("text/plain;charset=UTF-8")
	public Response pushSingleMessage(
			@HeaderParam("DataType") String dataType,
			@HeaderParam("exptime") int exptime,
			@HeaderParam("delay") int delay,
			@PathParam("sender") String sender,
			@PathParam("recipient") String recipient,
			byte[] content) {
		try {
			return unicast(dataType, exptime, delay, sender, recipient, content);
		} catch (Exception e) {
			logger.debug("server internal error");
			logger.error(e.getMessage(),e);
			return Response.status(500).entity("server internal error").build(); 
		}
	}
	
	private Response unicast( String dataType,int exptime,int delay,String sender,String recipient,	byte[] content){

		logger.debug("[AppResource] pushSingleMessage to {}.", recipient);
		if (recipient == null || sender == null) {
			return Response.status(404).build();
		}

		if (content == null || content.length == 0) {
			return Response.status(422).build();
		}

		if (content.length > MSG_CONTENT_MAX_LENGTH) {
			return Response.status(413).build();
		}
		if (!this.changeRecipientServiceImpl.existCid(sender, recipient)) {
			logger.debug("[AppResource] pushSingleMessage aid is {} cid is{}.",
					sender, recipient);
			return Response.status(404).build();
		}
		String strUUID = java.util.UUID.randomUUID().toString()
				.replaceAll("-", "");
		
		MessageRecord messageRecord=new MessageRecord();
		messageRecord.setMessageId(strUUID);
		messageRecord.setParentId(strUUID);
		messageRecord.setAppId(sender);
		messageRecord.setRecipient(recipient);
		messageRecord.setDataType(dataType);
		messageRecord.setExpire(exptime);
		messageRecord.setDelay(delay);
		messageRecord.setContent(content);
		messageRecord.setBroadcastFlag(false);
		messageRecord.setCreateAt(new Date());
		
		
		logger.debug("[AppResource] pushSingleMessage delay is {}", delay);
		if (delay <= 0) {
			messageRecord.setStatus("DELIVERING");
		} else {
			messageRecord.setDelayUntil(DateUtils.addMinutes(new Date(), delay));
			messageRecord.setStatus("DELAYING");
			DelayMessageTask task = new DelayMessageTask(messageRecord
					.getMessageId(), delay);
			boolean setupSuccess= delayMessageTaskService.setupTask(task);
			
			if(setupSuccess){
				messageRecord.setDelayExecBy(namedService.getAppName());
			}
		}

		if (MessageRecordService.getDefaultInstance().getDao()
				.saveMessageRecord(messageRecord)) {
			if (delay <= 0) {
				logger.debug("Push message [{}] immediately.", strUUID);
				MessagePusher.getDefaultInstance().unicast(recipient, messageRecord);
			}
			return Response.status(200).entity(strUUID).build();
		} else {
			logger.error("save message {} failed.", strUUID);
			return Response.status(500).build();
		}
	
	}

	@GET
	@Path("/message/query/{appId}/{messageId}")
	@Produces("text/plain;charset=UTF-8")
	public Response queryMessageStatus(@PathParam("appId") String appId,
			@PathParam("messageId") String messageId) {

		if (messageId == null || messageId == "") {
			logger.error("messageId is emtpy.");
			return Response.status(404).build();
		}

		try {
			String status = messageService.getMessageStatus(appId,messageId);

			logger.debug("message [{}] status is [{}]", messageId, status);
			return Response.ok().entity(status).build();

		} catch (MessageNotFoundException e) {
			logger.info("message [{}] not found.", e.getMessageId());
			return Response.status(404).build();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.status(500).build();
		}
	}

	@PUT
	@Path("/message/update/{appId}/{messageId}")
	@Consumes("application/octet-stream")
	@Produces("text/plain;charset=UTF-8")
	public Response updateMessage(
			@HeaderParam("DataType") String dataType,
			@PathParam("appId") String appId,
			@PathParam("messageId") String messageId,
			byte[] content) {
		try {
			messageService.modifyMessage(content,appId, messageId, dataType);
		} catch (MessageNotFoundException e) {

			logger.info("message [{}] not found", e.getMessageId());
			return Response.status(404).build();

		} catch (MessageCanNotModifyException e) {
			logger
					.info(
							"message [{}] can not modify, is broadcast [{}],message status [{}]",
							new Object[] { messageId, e.isBroadcast(),
									e.getMessageStatus() });
			return Response.status(409).build();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.status(500).build();
		}

		return Response.status(200).build();
	}

	@DELETE
	@Path("/message/delete/{appId}/{messageId}")
	@Produces("text/plain;charset=UTF-8")
	public Response deleteMessage(@PathParam("appId") String appId,
			@PathParam("messageId") String messageId) {
		try {
			messageService.deleteMessage(appId,messageId);
		} catch (MessageNotFoundException e) {
			logger.info("message [{}] not found", e.getMessageId());
			return Response.status(404).build();
		} catch (MessageCanNotModifyException e) {
			logger
					.info(
							"message [{}] can not modify, is broadcast [{}],message status [{}]",
							new Object[] { messageId, e.isBroadcast(),
									e.getMessageStatus() });
			return Response.status(409).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.status(500).build();
		}
		return Response.ok().build();
	}

	public void setChangeRecipientServiceImpl(
			ChangeRecipientService changeRecipientServiceImpl) {
		this.changeRecipientServiceImpl = changeRecipientServiceImpl;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	@PUT
	@Path("/test")
	@Consumes("application/octet-stream")
	@Produces("text/plain;charset=UTF-8")
	public Response test(byte[] result) {
		logger.debug("[AppResource]testFeedbackUrl");
		if (result != null && result.length > 0) {
			logger.debug("[AppResource]result: {}.", new String(result));
			return Response.ok().build();
		}
		return Response.status(404).build();
	}

	public void setDelayMessageTaskService(
			DelayMessageTaskService delayMessageTaskService) {
		this.delayMessageTaskService = delayMessageTaskService;
	}
	
	public void setNamedService(NamedService namedService) {
		this.namedService = namedService;
	}
}
