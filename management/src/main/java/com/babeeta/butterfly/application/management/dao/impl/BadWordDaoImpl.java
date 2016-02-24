package com.babeeta.butterfly.application.management.dao.impl;

import com.babeeta.butterfly.application.management.dao.BadWordDao;
import com.babeeta.butterfly.application.management.dao.MorphiaDataStore;
import com.babeeta.butterfly.application.management.entity.BadWords;
import com.google.code.morphia.query.Query;

public class BadWordDaoImpl extends BaseDao<BadWords,String> implements BadWordDao<BadWords,String> {

	public BadWordDaoImpl(MorphiaDataStore ds) {
		super(BadWords.class, ds);
		// TODO Auto-generated constructor stub
	}

	/***
	 * 判断敏感词是否存在
	 * @param word
	 * @return
	 */
	public boolean existsWord(String word)
	{
		Query<BadWords> query=this.createQuery().filter("word", word);
		if(query.countAll()>0)
		{
			return true;
		}
		return false;
	}
	
	

}
