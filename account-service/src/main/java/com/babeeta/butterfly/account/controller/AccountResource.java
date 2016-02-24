package com.babeeta.butterfly.account.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.babeeta.butterfly.account.entity.Account;
import com.babeeta.butterfly.account.service.AccountService;

@Path("/")
@Scope(value = "prototype")
@Component("accountResource")
public class AccountResource {

	private final static Logger logger = LoggerFactory
			.getLogger(AccountResource.class);

	private AccountService accountService;

	/**
	 * 验证
	 * 
	 * @param account
	 * @return
	 */
	@POST
	@Path("/api/auth")
	@Consumes("application/json")
	@Produces("application/json")
	public Response auth(Account account) {
		return Response.ok(accountService.auth(account)).build();
	}

	/**
	 * 得到一个账户
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/api/account/{id}")
	@Produces("application/json")
	public Response getAccount(@PathParam("id") String id) {
		try {
			return Response.ok(accountService.getAccount(new Account(id)))
					.build();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return Response.serverError().build();
	}

	/**
	 * 列举账户(条件参数)
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/api/account")
	@Produces("application/json")
	public Response listAccount(@QueryParam("userId") String uid,
			@QueryParam("status") String status) {
		Account account = new Account();
		account.setStatus(status);
		Map<String, Object> map = new HashMap<String, Object>();
		if (uid != null) {
			map.put("userId", uid);
		}
		account.setExtra(map);
		try {
			ResponseBuilder response = Response.ok();
			List<Account> list = accountService.ListAccount(account);
			return response.entity(list).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return Response.serverError().build();
	}

	/**
	 * 注册
	 * 
	 * @param account
	 * @return
	 */
	@POST
	@Path("/api/register")
	@Consumes("application/json")
	@Produces("application/json")
	public Response register(Account account) {
		try {
			logger.debug("register id [{}] key [{}]", account.getId(), account
					.getSecureKey());
			return Response.ok(accountService.register(account)).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return Response.serverError().build();
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	/**
	 * 更新
	 * 
	 * @param account
	 * @param id
	 * @return
	 */
	@PUT
	@Path("/api/account/{id}")
	@Consumes("application/json")
	public Response update(Account account, @PathParam("id") String id) {
		if (account == null || !(account.getId().equals(id))) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		accountService.updateAccount(account);
		return Response.noContent().build();
	}

	@PUT
	@Path("/api/hasfield/{aid}/{field}")
	@Consumes("application/octet-stream")
	@Produces("application/json")
	public Response check(@PathParam("aid") String aid,
			@PathParam("field") String field) {
		logger.debug("[AccountResource] exists {} is has {}", aid, field);
		String flag = this.accountService.hasField(aid, field);
		if (flag != null && flag != "") {
			logger.debug("[AccountResource] life is {}", flag);
			return Response.ok(flag).build();
		}
		return Response.status(404).build();
	}

	@GET
	@Path("/api/account/expireMsgFeedbackEnable")
	@Produces("application/json")
	public Response getExpiredMsgFeedbackServiceEnableAccount() {
		try {
			return Response.ok().entity(
					accountService.getExpiredMsgFeedbackServiceEnableAccount())
					.build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Response.status(500).entity("server internal error by hudee").build();
	}
	@GET
	@Path("/api/count")
	@Produces("text/plain")
	public Response count(){
		try {
			return Response.ok().entity(accountService.count()).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Response.status(500).entity("server internal error by hudee").build();
	}

}
