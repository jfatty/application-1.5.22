package com.babeeta.butterfly.application.third.resource;

public enum MessageType {
	BINARY(0, "bibary"),
	JSON(1, "json");

	private int index;
	private String tag;

	private MessageType(int index, String tag) {
		this.index = index;
		this.tag = tag;
	}

	public String toString() {
		return tag;
	}

	public int getIndex() {
		return index;
	}
}
