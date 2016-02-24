package com.babeeta.butterfly.application.management.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.babeeta.butterfly.application.management.util.PageModel;
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
	
	/***
	 *  分页
	 * @param totalCount
	 * @param request
	 * @return
	 */
	public int setPage(int totalCount,HttpServletRequest request)
	{
		String modelName=request.getParameter("modelName");
		String page=request.getParameter("page");
		PageModel pm=(PageModel)request.getAttribute("pageModel");
		if(page==null||page.equals(""))
		{
			page="1";
		}
		if(modelName==null)
		{
			modelName="";
		}
		if(pm==null)
		{
			pm=new PageModel();
			pm.setPage(Integer.valueOf(page));
			pm.setTotalCount(totalCount);
		}
		int offset=(pm.getCurrPage()-1)*pm.getCount();
		request.setAttribute("pageModel", pm);
		request.setAttribute("modelName", modelName);
		return offset;
	}
}
