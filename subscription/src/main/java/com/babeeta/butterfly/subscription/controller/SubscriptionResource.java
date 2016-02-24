package com.babeeta.butterfly.subscription.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.babeeta.butterfly.subscription.entity.Binding;
import com.babeeta.butterfly.subscription.service.SubscriptionService;

@Path("/")
@Scope(value = "prototype")
@Component("subscriptionResource")
public class SubscriptionResource {

	private final static Logger logger = LoggerFactory
			.getLogger(SubscriptionResource.class);

	private SubscriptionService subscriptionService;

	@POST
	@Path("/api/bind")
	@Consumes("application/json")
	@Produces("application/json")
	public Response bind(Binding binding) {
		logger.debug("[{},{},{}] on binding.", new Object[] { binding.getAid(),
				binding.getCid(), binding.getDid() });
		ResponseBuilder builder = Response.ok();
		try {
			builder.entity(subscriptionService.bind(binding));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Response.serverError().build();
		}
		return builder.build();
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	@POST
	@Path("/api/unbind")
	@Consumes("application/json")
	@Produces("application/json")
	public Response unbind(Binding binding) {
		logger.debug("[{},{}] on unbinding.", binding.getAid(),
				binding.getCid());
		ResponseBuilder builder = Response.ok();
		try {
			builder.entity(subscriptionService.unbind(binding));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Response.serverError().build();
		}
		return builder.build();
	}
	@GET
	@Path("/api/list/{did}")
	@Produces("application/json")
	public Response list(@PathParam("did") String did){
		ResponseBuilder builder = Response.ok();
		try {
			builder.entity(subscriptionService.list(did));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Response.serverError().build();
		}
		return builder.build();
	}
}
