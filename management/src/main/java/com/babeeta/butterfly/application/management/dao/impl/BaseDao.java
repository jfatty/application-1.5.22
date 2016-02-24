package com.babeeta.butterfly.application.management.dao.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.dao.IDao;
import com.babeeta.butterfly.application.management.dao.MorphiaDataStore;
import com.google.code.morphia.DAO;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

public class BaseDao<T,K extends Serializable>extends DAO<T,K> implements IDao<T,K> {

	public BaseDao(Class<T> entityClass, MorphiaDataStore ds) {
		super(entityClass, ds.getDataStore());
	}

	@Override
	public void deleteByEntityId(String id) {
		deleteByQuery(createQuery().filter("_id", id));
	}

	@Override
	public T findById(String id) {
		return findOne("_id", id);
	}

	@Override
	public int queryCount() {
		return (int) createQuery().countAll();
	}

	@Override
	public List<T> queryList(int offset, int limit) {	
		return createQuery().offset(offset).limit(limit).asList();
	}

	@Override
	public Key<T> save(T entity) {
		return this.ds.save(entity);
	}

	@Override
	public void update(T entity,String id,Map<String,Object> map) {
		Query<T> query =createQuery().filter("_id",id);
		UpdateOperations<T> ops=createUpdateOperations();
		if(map!=null&&map.size()>0)
		{
			Iterator<String> it=map.keySet().iterator();
			while(it.hasNext())
			{
				String key=it.next();
				Object value=map.get(key);
				ops.set(key, value);
			}
		}
		this.ds.update(query, ops);
		
	}
	/****
     * 按条件不分页查询
     * @param map
     * @return
     */
    public List<T> queryAll(Map<String,Object> map)
    {
    	Query<T> query=this.createQuery().order("-createAt");
    	if(map!=null&&map.size()>0)
    	{
    		Iterator<String> it=map.keySet().iterator();
    		while(it.hasNext())
    		{
    			String key=it.next();
    			Object obj=map.get(key);
    			query.filter(key, obj);
    		}
    	}
    	return query.asList();
    }
	/****
     * 按条件查询列表
     * @param offset
     * @param limit
     * @param map
     * @return
     */
    public List<T> queryList(int offset,int limit,Map<String,Object> map)
    {
    	Query<T> query=this.createQuery().offset(offset).limit(limit).order("-createAt");
    	if(map!=null&&map.size()>0)
    	{
    		Iterator<String> it=map.keySet().iterator();
    		while(it.hasNext())
    		{
    			String key=it.next();
    			Object obj=map.get(key);
    			query.filter(key, obj);
    		}
    	}
    	return query.asList();
    }
    
    /***
     * 查询种数
     * @param map
     * @return
     */
    public int queryCount(Map<String,Object> map)
    {
    	Query<T> query=this.createQuery();
    	if(map!=null&&map.size()>0)
    	{
    		Iterator<String> it=map.keySet().iterator();
    		while(it.hasNext())
    		{
    			String key=it.next();
    			Object obj=map.get(key);
    			query.filter(key, obj);
    		}
    	}
    	return (int)query.countAll();
    }
}
