package com.babeeta.butterfly.application.words.service.impl;

import java.util.List;

import com.babeeta.butterfly.application.words.dao.IWordsDao;
import com.babeeta.butterfly.application.words.entity.Words;
import com.babeeta.butterfly.application.words.service.IWordsService;
import com.babeeta.butterfly.application.words.util.WordsUtil;

public class WordsServiceImpl implements IWordsService{

	
	private IWordsDao wordsDaoImpl;
	
	public static List<Words> words=null;
	
	
	/***
	 * 过滤敏感词
	 * @param word
	 * @return
	 */
	public boolean filterWord(String word)
	{
	    if(words==null||words.size()<=0)
	    {
	    	words=this.wordsDaoImpl.queryAllWords();
	    }
	    WordsUtil util=new WordsUtil(words);
	    
		return util.filterWord(word);
	}
	
	
	/***
	 * 删除敏感词
	 * @param word
	 * @return
	 */
	public void removeWord(String word)
	{
		this.wordsDaoImpl.removeWord(word);
	}
	
	/***
	 * 新增多个敏感词，词之间用|隔开
	 * @param word
	 * @return
	 */
	public void addWords(String words)
	{
		if(words==null||words.equals(""))
		{
			return;
		}
		//分割字符
		String[] str=words.split("\\|");
		if(str==null||str.length<=0)
		{
			return;
		}
		//迭代
		for(String word:str)
		{
			if(word==null||word.equals(""))
			{
				continue;
			}
			this.wordsDaoImpl.addWord(word);
		}
	}
	
	/***
	 * 删除多个敏感词，词之间用|隔开
	 * @param word
	 * @return
	 */
	public void removeWords(String words)
	{
		if(words==null||words.equals(""))
		{
			return;
		}
		//分割字符
		String[] str=words.split("\\|");
		if(str==null||str.length<=0)
		{
			return;
		}
		//迭代
		for(String word:str)
		{
			if(word==null||word.equals(""))
			{
				continue;
			}
			this.wordsDaoImpl.removeWord(word);
		}
	}
		
	public void remove()
	{
		this.wordsDaoImpl.remove();
	}


	
	public void setWordsDaoImpl(IWordsDao wordsDaoImpl) {
		this.wordsDaoImpl = wordsDaoImpl;
	}
	
}
