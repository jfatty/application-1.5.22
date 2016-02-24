package com.babeeta.butterfly.application.management.entity;

import java.util.Date;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/****
 * 角色
 * @author zeyong.xia
 * @date 2011-10-25
 */
@Entity(noClassnameStored=true,value="role")
public class Role {

	@Id
	private String id;
	
	@Indexed
	private String roleName;//角色名称
	
	@Indexed
	private String userEmail;//对应用户帐号
	
	private Date createAt;//创建时间
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	
}
