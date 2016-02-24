package com.babeeta.butterfly.application.tag.entity;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
/***
 * 标签信息
 * @update zeyong.xia  add aid field
 * @date 2011-9-22
 */
@Entity(value = "TagInfo", noClassnameStored = true)
public class TagInfo {
	
	@Id
	private String id;
	
	private String clientId;
	
	private String tagName;
	
	private String aid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}
	
	
}
