package com.babeeta.butterfly.application.third.service.tag;

import com.babeeta.butterfly.application.third.service.AbstractHttpRPCResult;

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
