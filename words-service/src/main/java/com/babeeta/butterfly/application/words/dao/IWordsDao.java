package com.babeeta.butterfly.application.words.dao;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.babeeta.butterfly.application.words.entity.Words;

/***
 * 
 * @author zeyong.xia
 * @date 2011-8-31
 * @time 上午10:28:21
 * @group babeeta
 */
public interface IWordsDao {

	/***
	 * 添加敏感词
	 * @param word
	 */
	public Words addWord(String word);
	
	/***
	 * 移除敏感词
	 * @param word
	 */
	public void removeWord(String word);
	
	public void remove();
	
	/***
	 * 是否存在该敏感词
	 * @param word
	 * @return
	 */
	public boolean existsWord(String word);
	
	/***
	 * 得到所有敏感词
	 * @return
	 */
	public ConcurrentHashMap<String, String> getAllWord();
	
	/***
	 * 查询所有敏感词
	 * @return
	 */
	public List<Words> queryAllWords();
}
