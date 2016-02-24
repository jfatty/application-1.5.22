package com.babeeta.butterfly.account.entity;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.babeeta.butterfly.account.json.JsonDateSerializer;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Property;

@Entity(value = "account", noClassnameStored = true)
public class Account {

	@Id
	private String id;
	@Property("key")
	private String secureKey;

	/**
	 * (NORMAl,FREEZED)
	 */
	@Indexed
	private String status;
	@Indexed
	private String expiredMsgFeedbackUrl;

	private Date createDate;

	@Embedded(concreteClass = java.util.HashMap.class)
	private Map<String, Object> extra;
	
	public Account() {
		super();
	}

	public Account(String id) {
		this.id = id;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getCreateDate() {
		return createDate;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public String getId() {
		return id;
	}

	public String getSecureKey() {
		return secureKey;
	}

	public String getStatus() {
		return status;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSecureKey(String secureKey) {
		this.secureKey = secureKey;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getExpiredMsgFeedbackUrl() {
		return expiredMsgFeedbackUrl;
	}

	public void setExpiredMsgFeedbackUrl(String expiredMsgFeedbackUrl) {
		this.expiredMsgFeedbackUrl = expiredMsgFeedbackUrl;
	}
}
