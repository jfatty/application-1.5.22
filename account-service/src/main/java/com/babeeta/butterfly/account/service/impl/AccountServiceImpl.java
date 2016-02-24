package com.babeeta.butterfly.account.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.account.dao.AccountDao;
import com.babeeta.butterfly.account.entity.Account;
import com.babeeta.butterfly.account.service.AccountService;

public class AccountServiceImpl implements AccountService {

	private final static Logger logger = LoggerFactory
			.getLogger(AccountServiceImpl.class);

	private AccountDao accountDao;
	private Map<String,Account> appAccountMap=new HashMap<String, Account>();
	private boolean inApp = true;
	private static final String DEFAULT_LIFE_UNIT = "day";

	public AccountServiceImpl(String domain) {
		// if domain contains dev,than we to consider it in DEV
		// domain,otherwise in APP domain
		if (domain.indexOf("dev") > -1) {
			inApp = false;
		}
	}
	
	@Override
	public String auth(Account paramAccount) {
		if(inApp){
			return authAppAccount(paramAccount);
		}else{
			return authDeviceAccount(paramAccount);
		}
	}

	@Override
	public Account getAccount(Account account) {
		return accountDao.selectById(account.getId());
	}

	@Override
	public List<Account> ListAccount(Account account) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (account.getStatus() != null) {
			map.put("status", account.getStatus());
		}
		Map<String, Object> extraMap = account.getExtra();
		Iterator<String> it = extraMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			map.put("extra." + key, extraMap.get(key));
		}
		return accountDao.selectByQuery(map);
	}

	@Override
	public Account register(Account account) {
		if (inApp) {
			account.setStatus("NORMAL");
		}
		account.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		account.setSecureKey(UUID.randomUUID().toString()
				.replaceAll("-", ""));
		account.setCreateDate(new Date());
		account = accountDao.insertAccount(account);
		logger.debug("Register result:  id:{},key:{}", account.getId(),
				account.getSecureKey());
		return account;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	@Override
	public void updateAccount(Account account) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (account.getStatus() != null) {
			map.put("status", account.getStatus());
		}
		if (account.getExtra() != null) {
			map.put("extra", account.getExtra());
		}
		logger.debug("Update result:  id:{}", account.getId());
		accountDao.update(account.getId(), map);
	}
	

	/***
	 * 判断该应用是否拥有自定义life权限
	 * @param aid
	 * @return
	 */
	public String hasField(String aid,String field){
		logger.debug("[AccountServiceImpl] hasLife aid is {},field is {} ", aid,field);
		Account account=getAppAccount(aid);
		
		if (account == null) {
			logger.error("not found account {}", aid);
			throw new IllegalArgumentException("not found account " + aid);
		}

		if (account.getExtra() == null || account.getExtra().isEmpty()) {
			logger.debug("account {} don't have the property 'extra'", aid);
			return DEFAULT_LIFE_UNIT;
		}
		
		if (!account.getExtra().containsKey(field)) {
			logger.debug("account {} extra not found field '{}'", aid, field);
			return DEFAULT_LIFE_UNIT;
		}

		logger.debug("account {} hasLife,life  is {}", aid,	(String) account.getExtra().get(field));

		return (String) account.getExtra().get(field);
	}

	@Override
	public List<Account> getExpiredMsgFeedbackServiceEnableAccount() {
		return accountDao.queryHasExpiredMsgFeedbackUrlFieldAccount();
	}
	
	private String authDeviceAccount(Account paramAccount){

		Account account = accountDao.selectById(paramAccount.getId());
		
		if (account != null && (account.getSecureKey() != null
				&& account.getSecureKey().equals(paramAccount.getSecureKey()))) {
			
			logger.debug("Auth success, id:{},key:{}", account.getId(),
					account.getSecureKey());
			return "{\"status\":\"OK\"}";
		
		}
		logger.debug("Auth failed, Id:{},key:{}", paramAccount.getId(),
				paramAccount.getSecureKey());
		return "{\"status\":\"FAIL\"}";
	}
	
	private Account getAppAccount(String aid){
		Account account=appAccountMap.get(aid);
		
		if(account==null || StringUtils.isBlank(account.getId())){
			
			account = accountDao.selectById(aid);
			
			if(account!=null && StringUtils.isNotBlank(account.getId())){
				appAccountMap.put(account.getId(), account);
				logger.debug("[{}] put account info to memory.",account.getId());
			}
		}
		return account;
	}
	
	private String authAppAccount(Account paramAccount){
		Account account=getAppAccount(paramAccount.getId());
		
		if (account != null && (account.getSecureKey() != null
				&& account.getSecureKey().equals(paramAccount.getSecureKey()))) {
			if ("NORMAL".equals(account.getStatus())) {
				logger.debug("Auth success, id:{},key:{}", account.getId(),
						account.getSecureKey());
				return "{\"status\":\"OK\"}";
			} else {
				logger.debug(
						"Auth failed,reason for 'FREEZED' status, Id:{},key:{}",
						account.getId(),
						account.getSecureKey());
				return "{\"status\":\"FREEZED\"}";
			}
		}
		logger.debug("Auth failed, Id:{},key:{}", paramAccount.getId(),
				paramAccount.getSecureKey());
		return "{\"status\":\"FAIL\"}";
	}

	@Override
	public int count() {
		return accountDao.count();
	}
}
