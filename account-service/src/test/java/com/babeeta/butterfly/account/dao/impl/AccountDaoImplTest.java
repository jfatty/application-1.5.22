package com.babeeta.butterfly.account.dao.impl;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.babeeta.butterfly.account.entity.Account;
import com.google.code.morphia.Datastore;

public class AccountDaoImplTest {
	private AccountDaoImpl dao;
	private Datastore dataStore;

	@Before
	public void init() {

		dao = new AccountDaoImpl("account_"+getTestDBNameSufix());
		dataStore = dao.datastore;

		dataStore.delete(dataStore.createQuery(Account.class));

		assertAccountCount(0);
	}

	@Test
	public void queryHasExpiredMsgFeedbackUrlFieldAccountTest() {
		Account targetAccountOne = getTestAccount();
		targetAccountOne
				.setExpiredMsgFeedbackUrl("expiredMsgFeedbackUrlTestOne");
		dataStore.save(targetAccountOne);

		Account targetAccountTwo = getTestAccount();
		targetAccountTwo
				.setExpiredMsgFeedbackUrl("expiredMsgFeedbackUrlTestTwo");
		dataStore.save(targetAccountTwo);

		Account controlGroupAccount = getTestAccount();
		controlGroupAccount.setExpiredMsgFeedbackUrl(null);
		dataStore.save(controlGroupAccount);

		assertAccountCount(3);

		List<Account> accountList = dao
				.queryHasExpiredMsgFeedbackUrlFieldAccount();
		
		assertEquals(2, accountList.size());
		
		Assert.assertFalse(accountList.get(0).getId().equals(controlGroupAccount.getId()));
		
		Assert.assertFalse(accountList.get(1).getId().equals(controlGroupAccount.getId()));
	}

	private void assertAccountCount(int count) {
		assertEquals(count, dataStore.getCount(dataStore
				.createQuery(Account.class)));
	}

	private Account getTestAccount() {
		return getTestAccount(generateUUID());
	}

	private String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	private Account getTestAccount(String id) {
		Account account = new Account();
		account.setId(id);
		account.setStatus("testStatus");
		return account;
	}

	protected String getTestDBNameSufix() {
		String hostName = null;
		String hostAddress = null;

		try {
			hostName = InetAddress.getLocalHost().getHostName()
					.replaceAll("\\W", "_");
			hostAddress = InetAddress.getLocalHost().getHostAddress()
					.replaceAll("\\W", "_");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostName + "_" + hostAddress;
	}
}
