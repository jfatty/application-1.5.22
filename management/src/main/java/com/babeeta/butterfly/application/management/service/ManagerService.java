package com.babeeta.butterfly.application.management.service;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.entity.BadWords;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.entity.User;

/***
 * 后台管理逻辑
 * @author zeyong.xia
 * @date 2011-12-22
 */
public interface ManagerService {

	/***
	 * 查询消息
	 * @param id
	 * @return
	 */
	public GroupMessage queryMessageById(String id);
	
	/***
	 * 更新消息
	 * @param message
	 * @return
	 */
	public GroupMessage updateMessage(GroupMessage message); 
	
	/****
	 * 查询消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryMessageList(int offset,int limit);
	
	/****
	 * 查询消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryMyMessageList(int offset,int limit,String email);
	
	/***
	 * 新增群发消息
	 * @param message
	 * @return
	 */
	public GroupMessage addMessage(GroupMessage message);
	
	/***
	 * 查询未审核的message总数
	 * @return
	 */
	public int queryNotCheckMessageCount();
	
	/***
	 * 新增敏感词
	 * @param word
	 * @return
	 */
	public boolean addWord(BadWords word)throws Exception ;
	
	/***
	 * 删除敏感词
	 * @param word
	 */
	public void deleteWord(String word);
	
	/***
	 * 分页查询敏感词列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<BadWords> queryWordList(int offset,int limit);
	
	/***
	 * 查询敏感词总数
	 * @param offset
	 * @param limit
	 * @return
	 */
	public int queryWordCount();
	
	
	/***
	 * 判断敏感词是否存在
	 * @param word
	 * @return
	 */
	public boolean existsWord(String word);
	
	/***
	 * 查询用户列表
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public List<User> queryUserList(int offset,int limit,Map<String,Object> map);
	
	/***
	 * 查询用户总数
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public int queryUserCount(Map<String,Object> map);
	
	/***
	 * 过滤敏感词
	 * @param word
	 * @return
	 */
	public boolean filterWord(String word);
	
	/***
	 * 查询用户信息
	 * @param userId
	 * @return
	 */
	public User queryUserById(String userId);
	
	/***
	 * 查询用户信息
	 * @param userId
	 * @return
	 */
	public User queryUserByEmail(String email);
	
	/***
	 * 更新用户
	 * @param user
	 * @return
	 */
	public void updateUser(User user,Map<String,Object> map);
	
	/***
	 * 
	 * @param id
	 * @return
	 */
	public BadWords queryWordsById(String id);
	
	public void updateWord(BadWords word,Map<String,Object> map);
}
