package com.babeeta.butterfly.application.third.resource;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
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

/***
 * oppo tag
 * 
 * @author zeyong.xia
 * @date 2011-9-23
 */
@Controller
@Path("/1/api/tag")
@Scope(value = "prototype")
public class OppoTagResource {
	private OppoTagService oppoTagServiceImpl;
	private final static Logger logger = LoggerFactory
	        .getLogger(OppoTagResource.class);

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
		logger.debug(
		        "[OppoTagResource] getGroupTagListString tagList is {} appId is {}",
		        tagList, appId);
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
				groupNameList.append(tag);
			}
		} else {
			groupNameList.append(tagList);
		}
		logger.debug("getGroupTagListString result is {}",
		        groupNameList.toString());
		return groupNameList.toString();
	}

	@PUT
	@Path("/client/{cid}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN)
	public Response setTagForClient(@PathParam("cid") String cid,
	        String tagList, @HeaderParam("Authorization") String authorization)
	{
		logger.debug(
		        "[OppoTagResource]setTagForClient tag [{}] to client [{}].",
		        tagList,
		        cid);
		String[] authContent = getAuthContent(authorization);
		logger.debug("[OppoTagResource] aid is {} key is", authContent[0],
		        authContent[1]);
		String aid = getAuthContent(authorization)[0];
		if (cid == null || "".equals(cid) || cid == "") {
			return Response.status(404).build();
		}
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		if (tagList == null || tagList == "" || tagList.length() == 0) {
			return Response.status(422).build();
		}
		TagResult result = oppoTagServiceImpl.setGroupTag(cid, aid,
		        getGroupTagListString(tagList, authContent[0]));

		if (result.isSuccess()) {
			return Response.status(200).build();
		} else {
			logger.debug(
			        "[OppoTagResource]setTagForClient tag [{}] fail,result={}",
			        tagList,
			        result.getStatusCode());
			return Response.status(result.getStatusCode()).build();
		}
	}

	@DELETE
	@Path("/client/{cid}/{tagList}")
	public Response removeTagForClient(
	        @PathParam("cid") String cid,
	        @PathParam("tagList") String tagList,
	        @HeaderParam("Authorization") String authorization) {
		logger.debug("[OppoTagResource]removeTag tag [{}] from client [{}].",
		        tagList,
		        cid);
		String aid = getAuthContent(authorization)[0];
		if (cid == null || "".equals(cid) || cid == "") {
			return Response.status(404).build();
		}
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		if (tagList == null || tagList == "") {
			return Response.status(404).build();
		}
		String[] authContent = getAuthContent(authorization);

		TagResult result = oppoTagServiceImpl.removeGroupTag(cid, aid,
		        getGroupTagListString(tagList, authContent[0]));

		if (result.isSuccess()) {
			return Response.status(200).build();
		} else {
			logger.debug(
			        "[OppoTagResource]removeTag tag [{}] from client [{}] fail",
			        tagList,
			        cid);
			return Response.status(result.getStatusCode()).build();
		}
	}

	@DELETE
	@Path("/clean/{aid}/{tagList}")
	public Response cleanTags(
	        @PathParam("aid") String aid,
	        @PathParam("tagList") String tagList) {
		logger.debug("[OppoTagResource]cleanTags tag [{}] from app [{}].",
		        tagList,
		        aid);

		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		if (tagList == null || tagList == "") {
			return Response.status(404).build();
		}
		TagResult result = oppoTagServiceImpl.cleanTag(aid,
		        getGroupTagListString(tagList, aid));

		if (result.isSuccess()) {
			return Response.status(200).build();
		} else {
			logger.debug(
			        "[OppoTagResource]cleanTags tag [{}] from app [{}] fail",
			        tagList,
			        aid);
			return Response.status(result.getStatusCode()).build();
		}
	}

	@GET
	@Path("/client/{cid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTagForClient(
	        @PathParam("cid") String cid,
	        @HeaderParam("Authorization") String authorization) {
		logger.debug("[OppoTagResource]getTag of client [{}].", cid);
		String aid = getAuthContent(authorization)[0];
		if (cid == null || "".equals(cid) || cid == "") {
			return Response.status(404).build();
		}
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		String[] authContent = getAuthContent(authorization);

		TagResult result = oppoTagServiceImpl.listGroupTag(cid, aid);

		if (result.isSuccess()) {
			String[] groupTagList = result.getStringList();

			if (groupTagList == null || groupTagList.length == 0) {
				return Response.ok().entity(new ArrayList()).build();
			} else {
				return Response.ok().entity(groupTagList).build();
			}
		} else {
			logger.debug("[OppoTagResource]getTag of client [{}].fail ", cid);
			return Response.status(result.getStatusCode()).build();
		}
	}

	public void setOppoTagServiceImpl(OppoTagService oppoTagServiceImpl) {
		this.oppoTagServiceImpl = oppoTagServiceImpl;
	}

}
