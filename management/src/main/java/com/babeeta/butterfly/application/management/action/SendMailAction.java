package com.babeeta.butterfly.application.management.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.management.service.SendMailService;

/***
 * 发送邮件
 * @author zeyong.xia
 * @date 2011-12-8
 */
public class SendMailAction extends BaseAction{

	private final static Logger logger = LoggerFactory
	.getLogger(SendMailAction.class);
	private SendMailService sendMailServiceImpl;
	
	public String userEmail;
	
	/***
	 * 发送邮件
	 * @return
	 */
	public String sendmail()
	{
		logger.debug("[SendMailAction] sendmail");
		String mail=request.getParameter("userName");
		if(mail==null||mail=="")
		{
			logger.debug("[SendMailAction] sendmail failure,email is null");
			return ERROR;
		}
		this.sendMailServiceImpl.send(mail);
		return SUCCESS;
	}

	public void setSendMailServiceImpl(SendMailService sendMailServiceImpl) {
		this.sendMailServiceImpl = sendMailServiceImpl;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	
}
