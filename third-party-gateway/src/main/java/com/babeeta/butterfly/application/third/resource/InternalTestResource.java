package com.babeeta.butterfly.application.third.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

import com.babeeta.butterfly.application.third.service.tag.OppoTagService;
import com.babeeta.butterfly.application.third.service.tag.TagResult;

@Controller
@Path("/1/api/test")
@Scope(value = "prototype")
public class InternalTestResource {
	private OppoTagService tagService;
	private final static Logger logger = LoggerFactory
			.getLogger(InternalTestResource.class);

	public void setTagService(OppoTagService tagService) {
		this.tagService = tagService;
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

	private String getGroupTagListString(String tagList, String appId) {
		// prepare group name list string
		StringBuilder groupNameList = new StringBuilder();
		if (tagList.indexOf(",") > -1) {
			String[] tagArray = tagList.split(",");
			boolean append = false;
			for (String tag : tagArray) {
				if (append) {
					groupNameList.append(",");
				} else {
					append = true;
				}
				groupNameList.append(appId + "@" + tag);
			}
		} else {
			groupNameList.append(appId + "@" + tagList);
		}

		return groupNameList.toString();
	}

	@GET
	@Path("/group/{groupName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testGetDeviceListByGroupTag(
			@PathParam("groupName") String groupName,
			@HeaderParam("Authorization") String authorization) {
		logger.debug("[test resource]device list of [{}].", groupName);
		if (groupName == null) {
			return Response.status(404).build();
		}
		String[] authContent = getAuthContent(authorization);

		TagResult result = tagService.listDevice(getGroupTagListString(
				groupName, authContent[0]),"");

		if (result.isSuccess()) {
			String[] deviceList = result.getStringList();
			if (deviceList == null || deviceList.length == 0) {
				return Response.status(404).build();
			} else {
				return Response.status(200).entity(deviceList)
						.build();
			}
		} else {
			return Response.status(result.getStatusCode()).build();
		}
	}

	@GET
	@Path("/user/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testGetDeviceListByUser(
			@PathParam("userId") String userId) {
		logger.debug("[3rd resource]device list of user [{}].", userId);
		if (userId == null) {
			return Response.status(404).build();
		}

		TagResult result = tagService.listDevice("$" + userId,"");
		if (result.isSuccess()) {
			String[] deviceList = result.getStringList();
			if (deviceList == null || deviceList.length == 0) {
				return Response.status(404).build();
			} else {

				return Response.status(200).entity(deviceList)
						.build();
			}
		} else {
			return Response.status(result.getStatusCode()).build();
		}
	}
}