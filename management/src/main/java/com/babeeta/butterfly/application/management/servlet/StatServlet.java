package com.babeeta.butterfly.application.management.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.babeeta.butterfly.application.management.dao.StatDao;

public class StatServlet extends HttpServlet{
	private static final Logger LOG=LoggerFactory.getLogger(StatServlet.class);
	private StatDao statDao;
	
	@Override
	public void init(ServletConfig config){
		statDao=(StatDao)WebApplicationContextUtils.getWebApplicationContext(config.getServletContext()).getBean("statDao");
		statDao.ensureIndex();
		LOG.info("init stat servlet finished.");
	}
	
	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
		int limit=15;
		String rate="fiveMinutes";
		
		if(request.getParameter("limit")!=null && !"".equals(request.getParameter("limit"))){
			limit=Integer.parseInt(request.getParameter("limit"));
		}
		
		if(request.getParameter("rate")!=null && !"".equals(request.getParameter("rate"))){
			rate=request.getParameter("rate");
		}
		
		List<Map> statList=statDao.getStats(rate, limit);
		
		response.getWriter().write(JSONArray.fromObject(statList).toString());
	}
}
