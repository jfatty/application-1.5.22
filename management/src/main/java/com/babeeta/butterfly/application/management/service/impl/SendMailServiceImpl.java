package com.babeeta.butterfly.application.management.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.velocity.VelocityEngineUtils;
import com.babeeta.butterfly.application.management.service.SendMailService;

/****
 * 邮件发送器
 * @author zeyong.xia
 * @date 2011-12-8
 */
public class SendMailServiceImpl implements SendMailService {

	private MailSender mailSender;
	
	private String from;
	
	private String resetPwdUrl;
	
	private String host;
	
	private VelocityEngine velocityEngine;
	
	public static final String TEMPLATE_HOME = "mail/";
	
	@Override
	public void send(String userEmail) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("email", userEmail);
		context.put("modifyPwdUrl", resetPwdUrl(userEmail));
		
		try{
		String content = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, TEMPLATE_HOME + "plain.vm", context);
		String subject = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, TEMPLATE_HOME + "subject.vm", context);

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(userEmail);
		msg.setFrom(from);
		msg.setSubject(subject);
		msg.setText(content);

		mailSender.send(msg);
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	

	}
	
	private String resetPwdUrl(String user) {
		return host+"resetPass.jsp?userEmail="+user+"&token="+UUID.randomUUID().toString().replaceAll("-", "");
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public String getResetPwdUrl() {
		return resetPwdUrl;
	}

	public void setResetPwdUrl(String resetPwdUrl) {
		this.resetPwdUrl = resetPwdUrl;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	

}
