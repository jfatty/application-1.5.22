package com.babeeta.butterfly.application.third.service;

import javax.servlet.ServletContext;

public interface NamedService {

	/**
	 * @param servletContext
	 */
	public String nameNewNode(ServletContext servletContext);
	
	/**
	 * @return
	 */
	public String getNodeName();
	
	/**
	 * @param servletContext
	 * @return
	 */
	public boolean hasName(ServletContext servletContext);

}
