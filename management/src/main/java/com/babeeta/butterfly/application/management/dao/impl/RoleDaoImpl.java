package com.babeeta.butterfly.application.management.dao.impl;

import java.util.List;

import com.babeeta.butterfly.application.management.dao.MorphiaDataStore;
import com.babeeta.butterfly.application.management.dao.RoleDao;
import com.babeeta.butterfly.application.management.entity.Role;
import com.google.code.morphia.query.Query;

public class RoleDaoImpl extends BaseDao<Role, String> implements RoleDao<Role, String> {

	public RoleDaoImpl(MorphiaDataStore ds) {
		super(Role.class, ds);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Role queryByUserAccount(String userAccount) {
		// TODO Auto-generated method stub
		Query<Role> query=this.ds.createQuery(Role.class).filter("userEmail", userAccount);
		List<Role> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return list.get(0);
		}
		return null;
	}

}
