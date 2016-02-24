package com.babeeta.butterfly.application.app.tag;

public interface TagService {
	
	/***
	 * 通过tagName查询clientId
	 * @param aid
	 * @param groupTag
	 * @return
	 */
	public TagResult listClient(String aid,String groupTag);
	
	/**
	 * @param aid
	 * @param groupTag
	 * @return
	 */
	public int tagCount(String aid,String groupTag);
	
	/***
	 * 判断tag是否存在
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid,String tagName);
	
	
}
