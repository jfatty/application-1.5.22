package com.babeeta.butterfly.application.tag.dao;

import java.util.List;

import com.babeeta.butterfly.application.tag.entity.TagInfo;

public interface TagDao {
	public void addTag(TagInfo tag);

	public void removeTag(TagInfo tag);

	// 以下油zeyong.xia添加

	/***
	 * 查询tag信息
	 * 
	 * @paramtagName tag名称
	 * 
	 * @aid applicationId
	 */
	public List<TagInfo> queryClient(String tagName, String aid);

	/**
	 * @param tagName
	 * @param aid
	 * @return
	 */
	public int count(String tagName, String aid);

	/***
	 * 查询tag信息
	 * 
	 * @clientId cid
	 * 
	 * @aid applicationId
	 */
	public List<TagInfo> queryTag(String clientId, String aid);

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName, String cid);

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName);

	public void cleanTag(String aid, String tName);

}
