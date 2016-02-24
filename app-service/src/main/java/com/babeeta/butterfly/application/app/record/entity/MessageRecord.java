package com.babeeta.butterfly.application.app.record.entity;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

/***
 * 添加createAt and delay
 * 
 * @update zeyong.xia
 * @date 2011-9-22
 */
@Entity(value = "MessageRecord", noClassnameStored = true)
public class MessageRecord {
	@Id
	private String messageId;
	private String appId;
	private String recipient;
	private String status;
	private Date lastModified;
	private String dataType;
	private byte[] content;
	private int expire;
	private boolean broadcastFlag;
	private String parentId;
	// ///////zeyong.xia add
	private Date createAt;// 创建时间
	private int delay;// 延时时间，分钟，应该小于24×60
	private Date delayUntil;
	private String delayExecBy;

	public void setBroadcastFlag(boolean broadcastFlag) {
		this.broadcastFlag = broadcastFlag;
	}

	public boolean getBroadcastFlag() {
		return broadcastFlag;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public Date getDelayUntil() {
		return delayUntil;
	}

	public void setDelayUntil(Date delayUntil) {
		this.delayUntil = delayUntil;
	}

	public String getDelayExecBy() {
		return delayExecBy;
	}

	public void setDelayExecBy(String delayExecBy) {
		this.delayExecBy = delayExecBy;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
}
