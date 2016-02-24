package com.babeeta.butterfly.application.management.util;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/***
 * 登录拦截器
 * @author zeyong.xia
 * @date 2012-1-13
 */
public class LoginInterceptor implements Interceptor{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	/***
	 * 拦截
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		// TODO Auto-generated method stub
		   Map session = invocation.getInvocationContext().getSession();
	       String userEmail = (String) session.get("userEmail");
	       if(userEmail!=null&&!"".equals(userEmail))
	       {
	    	   return invocation.invoke();
	       }
	       else
	       {
	    	   return "loginOut";
	       }
	}

}
