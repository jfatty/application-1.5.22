package com.babeeta.butterfly.application.words.dao.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.words.dao.IWordsDao;
import com.babeeta.butterfly.application.words.entity.Words;
import com.google.code.morphia.query.Query;

public class WordsDaoImpl extends BasicDaoImpl implements IWordsDao {

	private final static Logger logger = LoggerFactory
	.getLogger(WordsDaoImpl.class);


	public WordsDaoImpl() {
		super();
		datastore = morphia.createDatastore(mongo, "words");
		datastore.ensureIndexes();
	}
	/***
	 * 添加敏感词
	 * @param word
	 */
	public Words addWord(String word)
	{
		Query<Words> query=this.datastore.createQuery(Words.class).filter("word", word);
		if(query.get()==null)
		{
			Words w=new Words();
			w.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			w.setWord(word);
			this.datastore.save(w);
			return w;
		}
		return null;
		
	}
	
	/***
	 * 移除敏感词
	 * @param word
	 */
	public void removeWord(String word)
	{
		Query<Words> query=this.datastore.createQuery(Words.class).filter("word", word);
		if(query.get()!=null)
		{
			this.datastore.delete(query);
		}
	}
	
	
	/***
	 * 得到所有敏感词=
	 * @return
	 */
	public ConcurrentHashMap<String, String> getAllWord()
	{
		ConcurrentHashMap<String, String> map=new ConcurrentHashMap<String, String>();
		Query<Words> query=this.datastore.createQuery(Words.class);
		List<Words> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			StringBuffer sb=new StringBuffer();
			for(Words word:list)
			{
				map.put(word.getId(), word.getWord());
			}
		}
		return map;
	}
	
	/***
	 * 是否存在该敏感词
	 * @param word
	 * @return
	 */
	public boolean existsWord(String word)
	{
		Query<Words> query=this.datastore.createQuery(Words.class).filter("word", word);
		if(query.get()!=null)
		{
			return true;
		}
		return false;
	}
	
	public void remove()
	{
		Query<Words> query=this.datastore.createQuery(Words.class);
		this.datastore.delete(query);
	}
	
	/***
	 * 查询所有敏感词
	 */
	public List<Words> queryAllWords()
	{
		Query<Words> query=this.datastore.createQuery(Words.class);
		return query.asList();
	}
}
