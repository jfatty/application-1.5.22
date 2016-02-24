package com.babeeta.butterfly.application.app.service;

import javax.servlet.ServletContext;

public interface NamedService {
	/**
	 * @param servletContext
	 */
	public void nameNewNode(ServletContext servletContext);
	
	/**
	 * @return
	 */
	public String getAppName();
	
	/**
	 * @param servletContext
	 * @return
	 */
	public boolean hasName(ServletContext servletContext);
}
