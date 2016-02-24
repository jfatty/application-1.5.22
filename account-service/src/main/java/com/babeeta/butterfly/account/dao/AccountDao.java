package com.babeeta.butterfly.account.dao;

import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.account.entity.Account;

public interface AccountDao {
	public void deleteById(String id);

	public Account insertAccount(Account account);

	public Account selectById(String id);

	public List<Account> selectByQuery(Map<String, Object> map);

	public void update(String id, Map<String, Object> map);

	/**
	 * 查询有expiredMsgFeedBackUrl字段的account
	 * 
	 * @return
	 */
	public List<Account> queryHasExpiredMsgFeedbackUrlFieldAccount();
		public int count();
}
