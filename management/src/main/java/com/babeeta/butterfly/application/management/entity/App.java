package com.babeeta.butterfly.application.management.entity;

import java.util.Date;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/***
 * 应用注册
 * @author zeyong.xia
 * @date 2011-10-25
 */
 @Entity(value="app",noClassnameStored=true)
public class App {

	 @Id
	 private String id;
	 
	 private String appName;//应用名称
	 
	 private String appType;//应用类型
	 
	 private String packageName;//包名
	 
	 private String appProfile;//应用简介
	 
	 private String platForm;//平台

	 private  String[] pushMode;//推送方式

	 private String[] messageFormat;//消息格式

	 private String[] messageLifeCycle;//消息生命周期
	 
	 private String callBackUrl;//消息回调地址
	 
	 private Date createAt;//常见时间
	 
	 private String checkStatus="";//审核状态，0是未审核，1为审核通过，2为审核通过但默认,3驳回
	 
	 private String appId;
	 
	 private String appKey;//
	 
	 private String userEmail;//属于哪个用户
	 
	 private String checkView;//审核意见

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

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppProfile() {
		return appProfile;
	}

	public void setAppProfile(String appProfile) {
		this.appProfile = appProfile;
	}

	public String getPlatForm() {
		return platForm;
	}

	public void setPlatForm(String platForm) {
		this.platForm = platForm;
	}

	
	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String[] getPushMode() {
		return pushMode;
	}

	public void setPushMode(String[] pushMode) {
		this.pushMode = pushMode;
	}

	public String[] getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String[] messageFormat) {
		this.messageFormat = messageFormat;
	}

	public String[] getMessageLifeCycle() {
		return messageLifeCycle;
	}

	public void setMessageLifeCycle(String[] messageLifeCycle) {
		this.messageLifeCycle = messageLifeCycle;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getCheckView() {
		return checkView;
	}

	public void setCheckView(String checkView) {
		this.checkView = checkView;
	}
	
}
