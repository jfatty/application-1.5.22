package com.babeeta.butterfly.application.management.dao;

import java.io.Serializable;

/***
 * 
 * @author zeyong.xia
 * @date 2011-12-21
 * @param <T>
 * @param <K>
 */
public interface BadWordDao<T, K extends Serializable> extends IDao<T, K>  {

	/***
	 * 判断敏感词是否存在
	 * @param word
	 * @return
	 */
	public boolean existsWord(String word);
}
