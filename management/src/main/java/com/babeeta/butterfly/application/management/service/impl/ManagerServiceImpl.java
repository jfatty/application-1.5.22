package com.babeeta.butterfly.application.management.service.impl;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.dao.BadWordDao;
import com.babeeta.butterfly.application.management.dao.MessageDao;
import com.babeeta.butterfly.application.management.dao.UserDao;
import com.babeeta.butterfly.application.management.entity.BadWords;
import com.babeeta.butterfly.application.management.entity.GroupMessage;
import com.babeeta.butterfly.application.management.entity.User;
import com.babeeta.butterfly.application.management.service.ManagerService;
import com.babeeta.butterfly.application.management.util.WordsUtil;


/***
 * 后台管理逻辑实现
 * @author zeyong.xia
 * @date 2011-12-22
 */
public class ManagerServiceImpl implements ManagerService {


	private MessageDao<GroupMessage,String> messageDaoImpl;
	
	private BadWordDao<BadWords, String> badWordsDaoImpl;
	
	private UserDao<User,String> userDaoImpl;
	
	private static List<BadWords> words=null;
	/***
	 * 查询消息
	 * @param id
	 * @return
	 */
	public GroupMessage queryMessageById(String id)
	{
		return this.messageDaoImpl.findById(id);
	}
	
	/***
	 * 更新消息
	 * @param message
	 * @return
	 */
	public GroupMessage updateMessage(GroupMessage message)
	{
		this.messageDaoImpl.updateMessage(message);
		return message;
	}
	
	/****
	 * 查询消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryMessageList(int offset,int limit)
	{
		return this.messageDaoImpl.queryCheckMessageList(offset, limit);
	}
	
	/***
	 * 新增群发消息
	 * @param message
	 * @return
	 */
	public GroupMessage addMessage(GroupMessage message)
	{
		this.messageDaoImpl.save(message);
		return message;
	}
	
	/****
	 * 查询消息列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<GroupMessage> queryMyMessageList(int offset,int limit,String email)
	{
		return this.messageDaoImpl.queryMyMessageList(offset, limit, email);
	}
	
	/***
	 * 查询未审核的message总数
	 * @return
	 */
	public int queryNotCheckMessageCount()
	{
		return this.messageDaoImpl.queryNotCheckMessageCount();
	}
	
	/***
	 * 新增敏感词
	 * @param word
	 * @return
	 */
	public boolean addWord(BadWords word) throws Exception 
	{
		this.badWordsDaoImpl.save(word);
		return true;
	}
	
	/***
	 * 删除敏感词
	 * @param word
	 */
	public void deleteWord(String word)
	{
		this.badWordsDaoImpl.deleteByEntityId(word);
	}
	
	/***
	 * 判断敏感词是否存在
	 * @param word
	 * @return
	 */
	public boolean existsWord(String word)
	{
		return this.badWordsDaoImpl.existsWord(word);
	}
	
	/***
	 * 分页查询敏感词列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<BadWords> queryWordList(int offset,int limit)
	{
		return this.badWordsDaoImpl.queryList(offset, limit);
	}
	
	/***
	 * 查询敏感词总数
	 * @param offset
	 * @param limit
	 * @return
	 */
	public int queryWordCount()
	{
		return this.badWordsDaoImpl.queryCount();
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
		return this.userDaoImpl.queryList(offset, limit, map);
	}
	
	/***
	 * 查询用户总数
	 * @param offset
	 * @param limit
	 * @param map
	 * @return
	 */
	public int queryUserCount(Map<String,Object> map)
	{
		return userDaoImpl.queryCount(map);
	}
	
	/***
	 * 过滤敏感词
	 * @param word
	 * @return
	 */
	public boolean filterWord(String word)
	{

	    if(words==null||words.size()<=0)
	    {
	    	words=this.badWordsDaoImpl.queryAll(null);
	    }
	    WordsUtil util=new WordsUtil(words);
	    
		return util.filterWord(word);
	
	}
	
	/***
	 * 查询用户信息
	 * @param userId
	 * @return
	 */
	public User queryUserById(String userId)
	{
		return this.userDaoImpl.findById(userId);
	}
	
	/***
	 * 查询用户信息
	 * @param userId
	 * @return
	 */
	public User queryUserByEmail(String email)
	{
		User u= this.userDaoImpl.queryUserByEmail(email);
		return u;
	}
	
	/***
	 * 
	 * @param id
	 * @return
	 */
	public BadWords queryWordsById(String id)
	{
		return this.badWordsDaoImpl.findById(id);
	}
	
	/***
	 * 更新用户
	 * @param user
	 * @return
	 */
	public void updateUser(User user,Map<String,Object> map)
	{
		this.userDaoImpl.update(user,user.getId(),map);		
	}
	
	public void updateWord(BadWords word,Map<String,Object> map)
	{
		this.badWordsDaoImpl.update(word,word.getId(),map);
	}
	
	public void setMessageDaoImpl(MessageDao<GroupMessage,String> messageDaoImpl) {
		this.messageDaoImpl = messageDaoImpl;
	}

	public void setBadWordsDaoImpl(BadWordDao<BadWords, String> badWordsDaoImpl) {
		this.badWordsDaoImpl = badWordsDaoImpl;
	}

	public void setUserDaoImpl(UserDao<User, String> userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}
	

}
