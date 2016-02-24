package com.oppo.push.action;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

/***
 * 
 * @author zeyong.xia
 * @date 2011-12-8
 */
public class BaseAction extends ActionSupport implements ServletRequestAware,ServletResponseAware{

	/**
	 * @author zeyong.xia
	 * @date 2011-12-8
	 */
	private static final long serialVersionUID = 1L;

	protected HttpServletRequest request;
	
	protected HttpServletResponse response;
	
	protected static final int ROWCOUNT=10;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
		
	}
	
}
