package com.babeeta.butterfly.application.app.reliablepush.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/***
 * 可靠投递
 * @author zeyong.xia
 * @date 2011-9-19
 */
@Entity(value="reliable_push",noClassnameStored=true)
public class ReliablePushBean {
	
	@Id
	public String id;
	
	public String parentId;//广播时用到
	
	public byte[] message;//消息体
	
	public String status;//消息状态，acked,expired,delivering
	
//	@Embedded(concreteClass = java.util.ArrayList.class)
//	private List<Object> key;
	
	public int type;//类型
	
	public int age;//过期时间
	
	public Date createAt;//创建时间
	
	private Date ackedAt;//ack时间
	
	private Date expiredAt;//过期时间

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}	

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getAckedAt() {
		return ackedAt;
	}

	public void setAckedAt(Date ackedAt) {
		this.ackedAt = ackedAt;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

//	public List<Object> getKey() {
//		return key;
//	}
//
//	public void setKey(List<Object> key) {
//		this.key = key;
//	}

	public Date getExpiredAt() {
		return expiredAt;
	}

	public void setExpiredAt(Date expiredAt) {
		this.expiredAt = expiredAt;
	}
		
}
