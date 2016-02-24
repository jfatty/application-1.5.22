package com.babeeta.butterfly.application.app;

/***
 * 
 * @update zeyong.xia add field status
 * @date 2011-9-21
 */
public class MessageContext {
	private String messageId = null;
	private String parentId = null;
	private String sender = null;
	private String recipient = null;
	private int expire = 0;
	private int delay = 0;
	private String dataType = null;
	private byte[] content = null;
	private boolean broadcastFlag;
	
	////zeyong.xia add 
    
	private String status;
	
	public void setBroadcastFlag(boolean broadcastFlag) {
		this.broadcastFlag = broadcastFlag;
	}

	public boolean getBroadcastFlag() {
		return broadcastFlag;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public int getExpire() {
		return expire;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public byte[] getContent() {
		return content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
