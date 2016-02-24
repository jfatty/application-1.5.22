package com.babeeta.butterfly.app.servlet;

import java.io.*;
import java.util.regex.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

import com.babeeta.butterfly.application.reliable.*;

public class MessageStatusAppServlet extends AbstractAppServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(MessageStatusAppServlet.class);

	final Pattern pattern = Pattern
			.compile("/service/client/all/message/([0-9a-z]{32})");// 模板：/service/client/all/message/{67ff5f2b64a6459abe3e6ab17d47b19e}

	@Override
	protected boolean doVerify(RequestContext rtx, HttpServletRequest arg0,
			HttpServletResponse arg1) throws IOException {
		// TODO Auto-generated method stub
		if (!verifyURIPattern(arg0, arg1)) {
			return false;
		}
		return true;
	}

	@Override
	protected void rpcInvoke(final RequestContext rtx, final AsyncContext ctx,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		// TODO Auto-generated method stub
		super.rpcInvoke(rtx, ctx, request, response);
		String uuid = request.getRequestURI().substring(
				request.getRequestURI().lastIndexOf('/') + 1);
		String status = ReliablePushImpl.getDefaultInstance().getMessageStatus(
				uuid);
		if (status != null) {
			StringBuffer ret = new StringBuffer("{\"status\":\"")
					.append(status).append("\"}");
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(ret.toString());
			response.getWriter().flush();
			response.getWriter().close();
			return;
		}
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.getWriter().write(STRING_NOT_FIND_MESSAGE_ID);
		response.getWriter().flush();
		response.getWriter().close();
	}

	private boolean verifyURIPattern(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		String requestUrl = request.getRequestURI();
		logger.debug("[Request URL]" + requestUrl);
		Matcher m = pattern.matcher(requestUrl);
		if (!m.matches()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write(STRING_NOT_FIND_MESSAGE_ID);
			response.getWriter().flush();
			response.getWriter().close();
			return false;
		}
		return true;
	}

}
