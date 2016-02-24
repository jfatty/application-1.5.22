package com.babeeta.butterfly.application.words.controller;

import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.words.service.IWordsService;

@Path("/words")
public class FilterWordsController {

	private final static Logger logger = LoggerFactory
	.getLogger(FilterWordsController.class);
	
	private IWordsService wordsServiceImpl;
	
	/***
	 * 过滤敏感词
	 * @return
	 */
	@GET
	@Path("/filter/{word}")
	@Produces("application/json")
    public Response filter(@PathParam("word")String word)
    {
		System.out.println("filter");
		
		try
		{
			//word=URLEncoder.encode("法轮功","utf-8");
			//将字符串解码
			word=URLDecoder.decode(word, "utf-8");
			word=word.replaceAll("[~,!,@,#,$,%,^,&,\\*,\\(,\\),\\-,——,\\+,=,:,\\?,\\？,￥]", "");
			boolean flag=this.wordsServiceImpl.filterWord(word);
			System.out.println("flag= "+flag);
			return Response.ok(new Boolean(flag)).build();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		return Response.serverError().build();
    }

	public void setWordsServiceImpl(IWordsService wordsServiceImpl) {
		this.wordsServiceImpl = wordsServiceImpl;
	}
	
	
}
