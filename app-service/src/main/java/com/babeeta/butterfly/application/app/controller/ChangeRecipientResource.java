package com.babeeta.butterfly.application.app.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.babeeta.butterfly.application.app.record.entity.MessageRecord;
import com.babeeta.butterfly.application.app.service.ChangeRecipientService;

/***
 * 变更收件人
 * 
 * @author zeyong.xia
 * @date 2011-9-19
 */
@Path(value = "/change")
@Scope(value = "prototype")
@Controller
public class ChangeRecipientResource {

	private ChangeRecipientService changeRecipientServiceImpl;

	private final static Logger logger = LoggerFactory
			.getLogger(ChangeRecipientResource.class);

	@PUT
	@Path(value = "/recipilent/{oldCid}/{newCid}")
	@Consumes("application/octet-stream")
	@Produces("text/plain;charset=UTF-8")
	public Response changeRecipient(@PathParam("oldCid") String oldCid,
			@PathParam("newCid") String newCid, String aid) {
		logger
				.debug(
						"[ChangeRecipientResource]changeRecipient from {} to {} aid is {}",
						new Object[] { oldCid, newCid, aid });
		if ((!this.changeRecipientServiceImpl.existCid(aid, oldCid))
				|| (!this.changeRecipientServiceImpl.existCid(aid, newCid))) {
			logger
					.debug(
							"[ChangeRecipientResource]changeRecipient from {} to {},aid is {} oldCid or newCid not exists",
							new Object[] { oldCid, newCid, aid });
			return Response.status(404).build();
		}
		// 验证 aid and key
		if (true) {
			try {
				this.changeRecipientServiceImpl.updateMessageRecordRecipient(
						oldCid, newCid, aid);
				this.changeRecipientServiceImpl.updateReliablePushCid(oldCid,
						newCid, aid);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return Response.status(500).build();
			}

		}
		return Response.status(200).build();
	}

	public void setChangeRecipientServiceImpl(
			ChangeRecipientService changeRecipientServiceImpl) {
		this.changeRecipientServiceImpl = changeRecipientServiceImpl;
	}

}
