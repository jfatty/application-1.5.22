package com.babeeta.butterfly.application.management.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request=(HttpServletRequest) arg0;

		HttpServletResponse response=(HttpServletResponse) arg1;
		request.setCharacterEncoding("UTF-8");
		String userEmail =(String)request.getSession().getAttribute("userEmail");
		if(userEmail==null||userEmail==""||userEmail.equals(""))
		{
			response.sendRedirect("loginOut.jsp");
		}
		else
		{
			chain.doFilter(arg0, arg1);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	
	public static void main(String[] args)
	{
		String[] aa={"a","b","c"};
		String[] bb={"b","c"};
		for(int i=0;i<aa.length;i++)
		{
			for(int j=0;j<bb.length;j++)
			{
				if(aa[i]==bb[j])
				{
					System.out.println(aa[i]+"true");
				}
			}
		}
	}
}
