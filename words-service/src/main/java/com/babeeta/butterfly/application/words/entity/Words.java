package com.babeeta.butterfly.application.words.entity;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/***
 * 敏感词实体Bean
 * @author zeyong.xia
 * @date 2011-8-31
 * @time 上午10:14:31
 * @group babeeta
 */
@Entity(value="words",noClassnameStored=true)
public class Words {
	
	@Id
	private String id;//ID
	
	@Indexed
	private String word;//敏感词

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
}
