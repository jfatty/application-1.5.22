package com.babeeta.butterfly.application.reliable.dao;

public interface MessageReceiveDao {
	/**
	 * @param messageId
	 * @param clientId
	 */
	public void save(String messageId,String clientId);
	
	/**
	 * @param messageId
	 * @param clientId
	 * @return
	 */
	public boolean exists(String messageId,String clientId);
}
