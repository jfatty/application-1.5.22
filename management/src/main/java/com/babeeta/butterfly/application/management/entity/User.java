package com.babeeta.butterfly.application.management.entity;

import java.util.Date;


import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/****
 * 后台管理登录用户
 * @author zeyong.xia
 * @date 2011-10-25
 */
@Entity(noClassnameStored=true,value="users")
public class User {

	@Id
	private String id;	
	
	private String userName;//用户名称
	
	@Indexed
	private String userEmail;//用户邮箱
	
	public String companyName;//公司名称
	
	@Indexed
	private String userPass;//密码
	
	@Indexed
	private String contactMan;//联系人
	
	private String contactPhone;//联系电话
	
	private String address;//联系地址
	
	private Date createAt;//创建时间
	
	private String roleName;//角色名称 user,manager

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail.replaceAll(" ", "");
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass.replaceAll(" ", "");
	}

	public String getContactMan() {
		return contactMan;
	}

	public void setContactMan(String contactMan) {
		this.contactMan = contactMan;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
}
