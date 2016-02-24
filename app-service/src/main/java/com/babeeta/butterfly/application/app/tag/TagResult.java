package com.babeeta.butterfly.application.app.tag;


public class TagResult extends AbstractHttpRPCResult {
	public TagResult(boolean success, int statusCode) {
		super(success, statusCode);
	}

	private String[] stringList;

	public void setStringList(String[] stringList) {
		this.stringList = stringList;
	}

	public String[] getStringList() {
		return stringList;
	}
}
