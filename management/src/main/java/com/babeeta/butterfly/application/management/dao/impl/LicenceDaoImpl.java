package com.babeeta.butterfly.application.management.dao.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.dao.LicenceDao;
import com.babeeta.butterfly.application.management.dao.MorphiaDataStore;
import com.babeeta.butterfly.application.management.entity.Licence;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

public class LicenceDaoImpl extends BaseDao<Licence,String> implements LicenceDao<Licence,String>  {

	public LicenceDaoImpl( MorphiaDataStore ds) {
		super(Licence.class, ds);
		// TODO Auto-generated constructor stub
	}

	/***
	 * 得到licence信息
	 * @return
	 */
	public Licence getLicence()
	{
		Query<Licence> query=this.createQuery();
		List<Licence> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return list.get(0);
		}
		return null;
	
	}
	
	/**
	 * 更新licence
	 * @param licence
	 * @return
	 */
	public boolean updateLicence(String id,Map<String,Object> map)
	{
		if(map==null||map.size()==0)
		{
			return false;
		}
		Iterator<String> it=map.keySet().iterator();
		Query<Licence>query=this.createQuery().filter("_id", id);
		UpdateOperations<Licence> ups=this.createUpdateOperations();
		while(it.hasNext())
		{
			String key=it.next();
			Object val=map.get(key);
			ups.set(key, val);
		}
		this.ds.update(query, ups);
		return true;
	}
}
