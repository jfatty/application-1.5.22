package com.babeeta.butterfly.application.words.action;



import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.words.service.IWordsService;
import com.opensymphony.xwork2.ActionSupport;


public class WebAction extends ActionSupport implements ServletRequestAware {

	private final static Logger logger = LoggerFactory
	.getLogger(WebAction.class);
	
	private IWordsService wordsServiceImpl;
	
	private HttpServletRequest request;
	
	/***
	 * 新增敏感词
	 * @return
	 */

	public String addWords()
	{
		String words="";
		try {
			words=request.getParameter("words");
			System.out.println("words= "+words);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		words=words.replaceAll("[~,!,@,#,$,%,^,&,\\*,\\(,\\),\\-,——,\\+,=,:,\\?,\\？,￥]", "");
		if(words!=null)
		{
			this.wordsServiceImpl.addWords(words);
		}
		else
		{
			logger.error("add words error");
			return ERROR;
		}
		logger.info("add words success");
		return SUCCESS;
	}
	

	public boolean removeWords(String words)
	{
		words=words.replaceAll("[~,!,@,#,$,%,^,&,\\*,\\(,\\),\\-,——,\\+,=,:,\\?,\\？,￥]", "");
		
		if(words!=null)
		{		
			this.wordsServiceImpl.removeWord(words);
		}
		else
		{
			logger.error("add words error");
			return false;
		}
		logger.info("add words success");
		return true;
	}
	
	/***
	 * 新增敏感词前验证
	 * @param words
	 * @return
	 */
	
	public Response ajaxVilidateWords(String word)
	{
	    System.out.println("word= "+word);
	    boolean flag=this.wordsServiceImpl.filterWord(word);
	    return Response.ok(new Boolean(flag)).build();	    
	}
	public void setWordsServiceImpl(IWordsService wordsServiceImpl) {
		this.wordsServiceImpl = wordsServiceImpl;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}
	
}
