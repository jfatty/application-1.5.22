package com.babeeta.butterfly.application.third.listener;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.babeeta.butterfly.application.third.service.NamedService;
import com.babeeta.butterfly.application.third.service.message.FeedbackService;

public class InitListener implements ServletContextListener {

	private Timer timer = null;
	private FeedbackService feedBackService;
	private NamedService namedService;
	private static final Logger log = LoggerFactory
			.getLogger(InitListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		timer.cancel();
		feedBackService.release();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		feedBackService = (FeedbackService) WebApplicationContextUtils
				.getWebApplicationContext(sce.getServletContext()).getBean(
						"feedbackService");
		namedService = (NamedService) WebApplicationContextUtils
				.getWebApplicationContext(sce.getServletContext()).getBean(
						"namedService");

		if (namedService.hasName(sce.getServletContext())) {
			feedBackService.release();
		} else {
			namedService.nameNewNode(sce.getServletContext());
		}

		if (feedBackService.initFeedbackAccount()) {
			timer = new Timer(true);

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					feedBackService.expiredMessageFeedback();
				}

			}, 0, 2 * 60 * 1000);
		}
		log.info("InitListener start successfully");
	}

}
