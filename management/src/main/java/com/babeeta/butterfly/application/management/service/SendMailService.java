package com.babeeta.butterfly.application.management.service;

/***
 * 发送邮件
 * @author zeyong.xia
 * @date 2011-12-8
 */
public interface SendMailService {

	/***
	 * 邮件发送
	 * @param userEmail
	 */
	public void send(String userEmail);
}
