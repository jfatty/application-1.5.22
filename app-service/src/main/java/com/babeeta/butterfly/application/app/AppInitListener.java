package com.babeeta.butterfly.application.app;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.babeeta.butterfly.application.app.service.DelayMessageTask;
import com.babeeta.butterfly.application.app.service.DelayMessageTaskService;
import com.babeeta.butterfly.application.app.service.NamedService;

public class AppInitListener implements ServletContextListener {

	private Timer delayingDeliveTimer = null;
	private Timer loadDelayMsgTaskTimer = null;
	private DelayMessageTaskService delayMessageTaskService;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		delayingDeliveTimer.cancel();
		loadDelayMsgTaskTimer.cancel();
	
		delayMessageTaskService.releaseDelayingMessage();
	}
	

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();

		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);

		NamedService namedService = (NamedService) applicationContext.getBean(
						"namedService");

		delayMessageTaskService = (DelayMessageTaskService) applicationContext
				.getBean("delayMessageTaskService");

		if (namedService.hasName(servletContext)) {
			delayMessageTaskService.loadUnfinishedTaskFromDb();
		} else {
			namedService.nameNewNode(servletContext);
		}
		
		initLoadDelayMsgTaskTimer(delayMessageTaskService);
		
		initDelayMsgDeliveTimer(delayMessageTaskService);
	}

	private void initDelayMsgDeliveTimer(
			final DelayMessageTaskService delayMessageTaskService) {
		delayingDeliveTimer = new Timer("deliveTimer", true);

		delayingDeliveTimer.schedule(
				new TimerTask()
				{
					@Override
					public void run() {
						DelayMessageTask task = null;
						do
						{
							task = delayMessageTaskService
									.getTimeToExecuteTask();
							if (task != null)
							{
								task.run();
							}
						} while (task != null);
					}

				}, 0, 60 * 1000);
	}

	private void initLoadDelayMsgTaskTimer(
			final DelayMessageTaskService delayMessageTaskService) {
		loadDelayMsgTaskTimer = new Timer("loadTaskTimer", true);
		loadDelayMsgTaskTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				delayMessageTaskService.loadTaskFromDbGradually();
			}
		}, 0, 60 * 1000);
	}
}
