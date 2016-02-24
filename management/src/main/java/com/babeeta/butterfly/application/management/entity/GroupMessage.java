package com.babeeta.butterfly.application.management.entity;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/***
 * 群发消息
 * @author zeyong.xia
 * @date 2011-12-6
 */
@Entity(value="groupmessage",noClassnameStored=true)
public class GroupMessage {

	@Id
	private String id;
	
	@Indexed
	private String appName;//应用名称
	
	private String appId;
	
	private String pushTarget;//推送目标
	
	private String messageFormat;//消息格式
	
	private String messageAge;//消息时长
	
	private Date createAt;//推送时间
	
	private String messageContent;//消息内容
	
	private int check;//0为未审核，1为审核功过，-1为审核不通过
	
	private String userEmail;//用户所发

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPushTarget() {
		return pushTarget;
	}

	public void setPushTarget(String pushTarget) {
		this.pushTarget = pushTarget;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public String getMessageAge() {
		return messageAge;
	}

	public void setMessageAge(String messageAge) {
		this.messageAge = messageAge;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}
