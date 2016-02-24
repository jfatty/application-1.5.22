package com.babeeta.butterfly.application.management.dao;

import java.io.Serializable;

import com.babeeta.butterfly.application.management.entity.Role;

/***
 * 
 * @author zeyong.xia
 * @date 2011-10-25
 */
public interface RoleDao<T, K extends Serializable> extends IDao<T, K> {
	
	public Role queryByUserAccount(String userAccount);
}
