package com.babeeta.butterfly.application.router.gateway.balance;

/**
 * 均衡的资源池
 * 
 * @author leon
 * 
 * @param <T>
 */
public interface ServiceLocator {
	/**
	 * 通过K获取对应的池
	 * 
	 * @param key
	 * @return Server的域名
	 */
	String getDomain(String key);
}
