package com.babeeta.butterfly.application.third.service.words;

import com.babeeta.butterfly.application.third.service.app.AppServiceResult;

/***
 * 敏感词过滤
 * @author zeyong.xia
 * @date 2011-9-23
 */
public interface FilterWordsService {

	/****
	 * 过滤文本消息
	 * xiazeyong add
	 * @param message
	 * @return
	 */
	public AppServiceResult filterMessage(String message);
}
