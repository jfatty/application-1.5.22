package com.babeeta.butterfly.app.servlet;

import java.util.regex.*;

import org.slf4j.*;

public class OldPushMessageAppServlet extends PushMessageAppServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(OldPushMessageAppServlet.class);

	public OldPushMessageAppServlet() {
		super.pattern = Pattern
				.compile("/service/push/server/([a-z0-9A-Z]{32}|[a-z0-9A-Z]{64})");
	}
}
