package com.babeeta.butterfly.application.management.dao.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.dao.MorphiaDataStore;
import com.babeeta.butterfly.application.management.dao.UserDao;
import com.babeeta.butterfly.application.management.entity.User;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

/****
 * 用户信息
 * @author zeyong.xia
 * @date 2011-12-8
 */
public class UserDaoImpl extends BaseDao<User, String> implements
		UserDao<User, String> {

	
	public UserDaoImpl( MorphiaDataStore ds) {
		super(User.class, ds);
		// TODO Auto-generated constructor stub
	}


	@Override
	public User login(String userEmail, String userPass) {
		// TODO Auto-generated method stub
		Query<User> query=this.ds.createQuery(User.class).filter("userEmail", userEmail).filter("userPass", userPass);
		List<User> u=query.asList();
		if(u!=null&&u.size()>0)
		{
			return u.get(0);
		}
		return null;
	}

	@Override
	/***
	 * 查询用户
	 * @param user
	 * @return
	 */
	public User  queryUserByEmail(String email)
	{
		Query<User> query=this.ds.createQuery(User.class).filter("userEmail", email);
		List<User> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return list.get(0);
		}
		 return null;
	}
	
	/****
	 * 
	 * @param user
	 * @return
	 */
	public User updateUser(User user)
	{
		Query<User> query=this.createQuery().filter("_id", user.getId());
		UpdateOperations<User> uos=createUpdateOperations();
		uos.set("companyName", user.getCompanyName());
		uos.set("contactMan", user.getContactMan());
		uos.set("contactPhone", user.getContactPhone());
		uos.set("userPass", user.getUserPass());
		uos.set("address", user.getAddress());
		this.ds.update(query, uos);
		return user;
		
	}
	
	/***
	 * 查询用户列表
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public List<User> queryUserList(int offset,int limit,Map<String,Object> map)
	{
		Query<User> query=this.createQuery().offset(offset).limit(limit).order("createAt");
		if(map!=null&&map.size()>0)
    	{
    		Iterator<String> it=map.keySet().iterator();
    		while(it.hasNext())
    		{
    			String key=it.next();
    			Object obj=map.get(key);
    			if(key=="roleName"||key.equals("roleName"))
    			{
    				query.field("roleName").notEqual(obj);
    			}
    			else{
    				query.filter(key, obj);
    			}
    		}
    	}
		return query.asList();
	}

}
