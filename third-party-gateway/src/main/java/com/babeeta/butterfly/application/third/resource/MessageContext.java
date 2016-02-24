package com.babeeta.butterfly.application.third.resource;

import java.util.Date;

public class MessageContext {
	private int life;
	private int delay;
	private Date createAt;
	private byte[] content;
	private MessageType type;

	public MessageContext(MessageType type, byte[] content) {
		createAt = new Date();
		this.type = type;
		this.content = content;
	}

	public byte[] getContent() {
		return content;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getLife() {
		return life;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}

	public MessageType getContentType() {
		return type;
	}

	public Date getCreateTime() {
		return createAt;
	}
}
