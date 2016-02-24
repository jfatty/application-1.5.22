package com.babeeta.butterfly.application.management.dao;

import com.google.code.morphia.Key;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/***
 * 
 * @author zeyong.xia
 * @date 2011-12-7
 */
public interface IDao<T,K extends Serializable> {
	
	/***
	 * 删除
	 * @param id
	 */
	public void deleteByEntityId(String id);

	/****
	 * 查询
	 * @param id
	 * @return
	 */
	public T findById(String id);

	/****
	 * 保存
     * @param entity
     */
	public Key<T> save(T entity);
	
	/****
	 * 更新
	 * @param entity
	 */
	public void update(T entity,String id,Map<String,Object> map);
	
	/****
	 * 查询总数
	 * @return
	 */
    public int queryCount();
    
    /***
     * 分页查询
     * @param offset
     * @param limit
     * @return
     */
    public List<T> queryList(int offset,int limit);
    
    /****
     * 按条件不分页查询
     * @param map
     * @return
     */
    public List<T> queryAll(Map<String,Object> map);
    
    /****
     * 按条件查询列表
     * @param offset
     * @param limit
     * @param map
     * @return
     */
    public List<T> queryList(int offset,int limit,Map<String,Object> map);
    
    /***
     * 查询种数
     * @param map
     * @return
     */
    public int queryCount(Map<String,Object> map);

}
