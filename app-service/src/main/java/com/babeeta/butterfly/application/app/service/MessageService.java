package com.babeeta.butterfly.application.app.service;

/**
 * @author lisong
 * 
 */
public interface MessageService {

	/**
	 * @param content
	 * @param appId
	 * @param messageId
	 * @param type
	 * @throws MessageNotFoundException
	 * @throws MessageCanNotModifyException
	 */
	public void modifyMessage(byte[] content, String appId, String messageId,
			String type)
			throws MessageNotFoundException, MessageCanNotModifyException;

	/**
	 * @param appId
	 * @param messageId
	 * @throws MessageNotFoundException
	 * @throws MessageCanNotModifyException
	 */
	public void deleteMessage(String appId, String messageId)
			throws MessageNotFoundException, MessageCanNotModifyException;

	/**
	 * @param appId
	 * @param messageId
	 * @return
	 * @throws MessageNotFoundException
	 */
	public String getMessageStatus(String appId, String messageId)
			throws MessageNotFoundException;

}
