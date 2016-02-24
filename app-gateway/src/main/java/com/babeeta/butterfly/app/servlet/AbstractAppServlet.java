package com.babeeta.butterfly.app.servlet;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractAppServlet extends HttpServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(AbstractAppServlet.class);

	public static final String STRING_ERROR = "ERROR_INTERNAL";// 服务器内部错误

	public static final String STRING_NOT_FIND_CLIENT_ID = "ERROR_CLIENT_NOT_FOUND";// clientId不存在
	public static final String STRING_INVALID_APP_ID = "ERROR_INVALID_ID_AND_KEY";// appId无效
	public static final String STRING_OVER_LENGTH = "ERROR_MESSAGE_LENGTH_EXCEED";// 数据超长1024
	public static final String STRING_NOT_FIND_MESSAGE_ID = "NOT_FIND_MESSAGE_ID";

	public static final String STRING_CONTENT_MUST_BE_JSON = "CONTENT_MUST_BE_JSON";

	public static final String STRING_TITLE_CANNOT_BE_EMPTY = "TITLE_CANNOT_BE_EMPTY";

	protected static final String X_TARGET = "X-Target";
	public static final AtomicLong REQUEST_COUNT = new AtomicLong(0);
	public static final AtomicLong EFFECTIVE_REQUEST_COUNT = new AtomicLong(0);

	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		defaultProcess(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		defaultProcess(arg0, arg1);
	}

	protected boolean doVerify(RequestContext rtx, HttpServletRequest arg0,
			HttpServletResponse arg1) throws IOException {
		return true;
	}

	protected void rpcInvoke(final RequestContext rtx, final AsyncContext ctx,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		// subclass to do
	}

	private void defaultProcess(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {
		response.setContentType("text/html;charset=UTF-8");
		// 记录所有到此入口的请求数量
		REQUEST_COUNT.getAndIncrement();

		final RequestContext rtx = new RequestContext();

		if (!doVerify(rtx, request, response)) {
			return;
		}

		EFFECTIVE_REQUEST_COUNT.getAndIncrement();// 记录有效的请求数量

		final AsyncContext ctx = request.startAsync();
		ctx.start(new Runnable() {
			@Override
			public void run() {
				try {
					rpcInvoke(rtx, ctx, request, response);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("RPC Invoke Exception ,{}", e.getMessage());
				} finally {
					// finish rpc invoke(whole request process to completed)
					if (request.isAsyncStarted()) {
						ctx.complete();
					}
				}
			}
		});
	}
}

class RequestContext {
	byte[] body;
	String[] authContent;
	long start;
}
