package com.babeeta.butterfly.application.management.entity;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/***
 * 敏感词
 * @author zeyong.xia
 * @date 2011-12-6
 */
@Entity(value="badwords",noClassnameStored=true)
public class BadWords {

	@Id
	private String id;
	
	@Indexed
	private String word;//敏感词
	
	private Date createAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	
}
