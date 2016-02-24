package com.babeeta.butterfly.application.third.service.tag;

/***
 * oppo定制
 * 
 * @author zeyong.xia
 * @date 2011-9-23
 */
public interface OppoTagService {

	/***
	 * 打tag
	 * 
	 * @param clientId
	 * @param aid
	 * @param groupTag
	 * @return
	 */
	public TagResult setGroupTag(String clientId, String aid, String groupTag);

	/***
	 * 移除tag
	 * 
	 * @param clientId
	 * @param aid
	 * @param groupTag
	 * @return
	 */
	public TagResult removeGroupTag(String clientId, String aid, String groupTag);

	/***
	 * 查询tag信息
	 * 
	 * @param clientId
	 * @param aid
	 * @return
	 */
	public TagResult listGroupTag(String clientId, String aid);

	/***
	 * 查询tag信息
	 * 
	 * @param groupTag
	 * @return
	 */
	public TagResult listDevice(String groupTag, String aid);

	public TagResult cleanTag(String aid, String tName);
}
