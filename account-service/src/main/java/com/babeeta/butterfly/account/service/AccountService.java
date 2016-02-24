package com.babeeta.butterfly.account.service;

import java.util.List;

import com.babeeta.butterfly.account.entity.Account;

public interface AccountService {
	/**
	 * 
	 * @return "OK"：有效， "FAIL"： 无效
	 */
	public String auth(Account account);

	/**
	 * 账户信息account
	 * 
	 * @return "OK"：有效， "FAIL"： 无效
	 */
	public Account getAccount(Account account);

	/**
	 * 列举用户
	 * 
	 * @param id
	 * @return
	 */
	public List<Account> ListAccount(Account account);

	/**
	 * 注册账户
	 * 
	 * @param account
	 * @return
	 */
	public Account register(Account account);

	/**
	 * 修改
	 * 
	 * @param account
	 */
	public void updateAccount(Account account);
	
	/***
	 * 判断该应用是否拥有自定义life权限
	 * @param aid
	 * @return
	 */
	public String hasField(String aid,String field);
	
	/**
	 * 查询有expiredMsgFeedBackUrl字段的account
	 * 
	 * @return
	 */
	public List<Account> getExpiredMsgFeedbackServiceEnableAccount();
	
	public int count();
	
}
