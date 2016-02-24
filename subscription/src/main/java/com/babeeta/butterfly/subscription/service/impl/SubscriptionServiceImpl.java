package com.babeeta.butterfly.subscription.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.subscription.dao.SubscriptionDao;
import com.babeeta.butterfly.subscription.entity.Binding;
import com.babeeta.butterfly.subscription.service.SubscriptionService;

public class SubscriptionServiceImpl implements SubscriptionService {
	private final static Logger logger = LoggerFactory
			.getLogger(SubscriptionServiceImpl.class);

	private SubscriptionDao subscriptionDao;

	@Override
	public Binding bind(Binding binding) throws Exception {
		if (binding.getCid() == null
				|| !subscriptionDao.exists(binding.getAid(), binding.getCid())) {
			// Generate an cid while param's cid is null or does not exists.
			binding.setCid(UUID.randomUUID().toString().replaceAll("-", ""));
		}
		subscriptionDao.save(binding.getAid(), binding.getCid(),
				binding.getDid());
		logger.debug("[{}:{}:{}] has bound.", new Object[] { binding.getAid(),
				binding.getCid(),
				binding.getDid() });
		return binding;
	}

	public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
		this.subscriptionDao = subscriptionDao;
	}
	
	@Override
	public String unbind(Binding binding) throws Exception {
		subscriptionDao.remove(binding.getAid(), binding.getCid());
		logger.debug("[{}:{}] has unbound.", new Object[] { binding.getAid(),
				binding.getCid() });
		return "{\"status\":\"OK\"}";
	}

	@Override
	public List<Map> list(String did) {
		return subscriptionDao.list(did);
	}

}
