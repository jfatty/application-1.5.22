package com.babeeta.butterfly.application.words.service;

/***
 * 
 * @author zeyong.xia
 * @date 2011-8-31
 * @time 上午10:40:01
 * @group babeeta
 */
public interface IWordsService {

	
	
	/***
	 * 过滤敏感词
	 * @param word
	 * @return
	 */
	public boolean filterWord(String word);
	
	/***
	 * 删除敏感词
	 * @param word
	 * @return
	 */
	public void removeWord(String word);
	

	/***
	 * 新增多个敏感词，词之间用|隔开
	 * @param word
	 * @return
	 */
	public void addWords(String words);
	/***
	 * 移除所有敏感词
	 */
	public void remove();

	
}
