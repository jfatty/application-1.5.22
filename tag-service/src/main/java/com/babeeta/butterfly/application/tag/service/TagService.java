package com.babeeta.butterfly.application.tag.service;

import java.util.List;

/***
 * 
 * @update zeyong.xia
 * @date 2011-9-21
 */
public interface TagService {

	/****
	 * 新增tag
	 */
	public void registerTag(String clientId, String tagName, String aid);

	/***
	 * 删除tag
	 * 
	 * @param clientId
	 * @param tagName
	 * @param aid
	 */
	public void unregisterTag(String clientId, String tagName, String aid);

	/***
	 * 查询tag信息
	 * 
	 * @paramtagName tag名称
	 * 
	 * @aid applicationId
	 */
	public List<String> queryClient(String tagName, String aid);

	/***
	 * 查询tag信息
	 * 
	 * @clientId cid
	 * 
	 * @aid applicationId
	 */
	public List<String> queryTag(String clientId, String aid);

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName);

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName, String cid);

	/***
	 * 判断是否存在
	 * 
	 * @param aid
	 * @param cid
	 * @return
	 */
	public boolean existsCid(String aid, String cid);

	/**
	 * @param tagName
	 * @param aid
	 * @return
	 */
	public int count(String tagName, String aid);

	public void cleanTag(String aid, String tName);

}
