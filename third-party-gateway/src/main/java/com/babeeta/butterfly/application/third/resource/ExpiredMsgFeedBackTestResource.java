package com.babeeta.butterfly.application.third.resource;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

public class ExpiredMsgFeedBackTestResource extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		process(request, response);
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		process(request, response);
	}

	private void process(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out.println("expired msg feedback request coming>>>>");
		System.out.println(IOUtils.toString(request.getInputStream()));
		
		response.setStatus(200);
		response.getWriter().write("ok");
	}
}
