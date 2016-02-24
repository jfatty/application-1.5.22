package com.babeeta.butterfly.application.app.service;


public interface DelayMessageTaskService {

	/**
	 * @param task
	 */
	public boolean setupTask(DelayMessageTask task);

	/**
	 * @param messageId
	 */
	public void removeTask(String messageId);

	/**
	 * @return
	 */
	public DelayMessageTask getTimeToExecuteTask();
	
	/**
	 * 
	 */
	public void loadTaskFromDbGradually();
	
	/**
	 * 
	 */
	public void loadUnfinishedTaskFromDb();
	
	/**
	 * 
	 */
	public void releaseDelayingMessage();
}