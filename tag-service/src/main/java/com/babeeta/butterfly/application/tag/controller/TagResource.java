package com.babeeta.butterfly.application.tag.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.babeeta.butterfly.application.tag.service.TagService;

/***
 * clientId for tag
 * 
 * @author zeyong.xia
 * @date 2011-9-27
 */
@Path("/")
@Component("tagResource")
public class TagResource {

	private final static Logger logger = LoggerFactory
	        .getLogger(TagResource.class);
	private TagService tagService;

	/***
	 * 为Client打Tag
	 * 
	 * @return
	 */
	@PUT
	@Path("/api/set-tag-for-client/{aid}/{cid}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("text/plain;charset=UTF-8")
	public Response setTagForClient(@PathParam("cid") String cid,
	        @PathParam("aid") String aid,
	        String tagName)
	{
		logger.debug("[tag resource]setTagForclient {} to {}.", tagName,
		        cid);
		if (cid == null || "".equals(cid) || cid == "") {
			return Response.status(404).build();
		}
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}

		if (!this.tagService.existsCid(aid, cid))
		{
			return Response.status(404).build();
		}
		if (tagName == null || "".equals(tagName) || tagName == "") {
			return Response.status(422).build();
		}
		if (tagName.indexOf(",") > -1) {
			String[] tagArray = tagName.split(",");
			for (String tag : tagArray) {
				tagService.registerTag(cid, tag, aid);
			}
		} else {
			tagService.registerTag(cid, tagName, aid);
		}
		return Response.status(200).build();
	}

	/***
	 * 移除Client Tag
	 * 
	 * @return
	 */
	@DELETE
	@Path("/api/remove-client-tag/{aid}/{cid}/{tagName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response removeTagForClient(@PathParam("cid") String cid,
	        @PathParam("tagName") String tagName, @PathParam("aid") String aid)
	{

		logger.debug("[tag resource]removeTagForClient {} from {}.", tagName,
		        cid);
		if (cid == null || "".equals(cid) || cid == "") {
			return Response.status(404).build();
		}
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		if (!this.tagService.existsCid(aid, cid))
		{
			return Response.status(404).build();
		}
		if (tagName == null || "".equals(tagName) || tagName == "") {
			return Response.status(422).build();
		}

		if (tagName.indexOf(",") > -1) {
			String[] tagArray = tagName.split(",");
			for (String tag : tagArray) {
				tagService.unregisterTag(cid, tag, aid);
			}
		} else {
			boolean flag = this.tagService.existsTag(aid, tagName, cid);
			if (!flag)
			{
				return Response.status(404).build();
			}
			tagService.unregisterTag(cid, tagName, aid);
		}
		return Response.status(200).build();
	}

	/***
	 * 得到TagName列表
	 * 
	 * @return
	 */
	@GET
	@Path("/api/get-client-tag-list/{aid}/{cid}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClientTagList(@PathParam("cid") String cid,
	        @PathParam("aid") String aid)
	{

		logger.debug("[tag resource]getClientTagList of {}.", cid);
		if (cid == null || "".equals(cid) || cid == "") {
			return Response.status(404).build();
		}
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		if (!this.tagService.existsCid(aid, cid))
		{
			return Response.status(404).build();
		}
		List<String> tagList = tagService.queryTag(cid, aid);

		logger.debug("[{}] has [{}] tags.", cid,
		        tagList == null ? 0 : tagList.size());
		return Response.status(200)
		        .entity(tagList == null ? Collections.EMPTY_LIST : tagList)
		        .build();
	}

	/***
	 * 得到ClientId列表
	 * 
	 * @return
	 */
	@GET
	@Path("/api/get-tag-client-list/{aid}/{tagName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClientListFromTag(@PathParam("tagName") String tagName,
	        @PathParam("aid") String aid)
	{
		logger.debug("[tag resource]getClientListFromTag of {}.", tagName);
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		if (tagName == null || "".equals(tagName) || tagName == "") {
			return Response.status(422).build();
		}
		List<String> result = new ArrayList<String>();
		if (tagName.indexOf(",") > -1) {
			String[] tagList = tagName.split(",");
			for (String tag : tagList) {
				boolean flag = this.tagService.existsTag(aid, tag);
				if (!flag)
				{
					return Response.status(404).build();
				}
				List<String> clientList = tagService.queryClient(tag, aid);
				if (clientList == null || clientList.size() == 0)
				{
					return Response.status(404).build();
				}
				logger.debug(
				        "[TagResource]getClientListFromTag [{}] have [{}] tags.",
				        clientList.size(),
				        tag);
				for (String clientId : clientList) {
					if (!result.contains(clientId)) {
						result.add(clientId);
					}
				}
			}
		} else {
			boolean flag = this.tagService.existsTag(aid, tagName);
			if (!flag)
			{
				return Response.status(404).build();
			}
			List<String> clientList = tagService.queryClient(tagName, aid);

			if (clientList == null || clientList.size() == 0)
			{
				return Response.status(404).build();
			}
			logger.debug(
			        "[TagResource]getClientListFromTag : [{}] have [{}] tags.",
			        clientList.size(),
			        tagName);
			return Response.status(200).entity(clientList).build();
		}

		if (result.size() > 0) {
			return Response.status(200).entity(result).build();
		} else {
			return Response.status(404).build();
		}

	}

	@GET
	@Path("/api/get-tag-tagname/{aid}/{tagName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response existsTag(@PathParam("tagName") String tagName,
	        @PathParam("aid") String aid)
	{
		logger.debug("[tag resource]existsTag tagName is {}.", tagName);
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}
		if (tagName == null || "".equals(tagName) || tagName == "") {
			return Response.status(422).build();
		}
		boolean flag = this.tagService.existsTag(aid, tagName);
		if (flag)
		{
			return Response.status(200).build();
		}
		return Response.status(404).build();
	}

	@GET
	@Path("/api/count/{aid}/{tagName}")
	public Response count(@PathParam("aid") String aid,
	        @PathParam("tagName") String tagName) {
		return Response.ok().entity(tagService.count(tagName, aid)).build();
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	/***
	 * 移除Client Tag
	 * 
	 * @return
	 */
	@DELETE
	@Path("/api/clean-tag/{aid}/{tagName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response cleanTag(@PathParam("tagName") String tagName,
	        @PathParam("aid") String aid)
	{

		logger.debug("[tag resource]cleanTag {}.{}.", aid, tagName);
		if (aid == null || "".equals(aid) || aid == "") {
			return Response.status(404).build();
		}

		if (tagName == null || "".equals(tagName) || tagName == "") {
			return Response.status(422).build();
		}

		if (tagName.indexOf(",") > -1) {
			String[] tagArray = tagName.split(",");
			for (String tag : tagArray) {
				tagService.cleanTag(aid, tag);
			}
		} else {
			boolean flag = this.tagService.existsTag(aid, tagName);
			if (!flag) {
				return Response.status(404).build();
			}
			tagService.cleanTag(aid, tagName);
		}
		return Response.status(200).build();
	}
}
