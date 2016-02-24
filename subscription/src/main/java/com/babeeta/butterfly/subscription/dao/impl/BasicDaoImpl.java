package com.babeeta.butterfly.subscription.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public class BasicDaoImpl {

	private final static Logger logger = LoggerFactory
			.getLogger(BasicDaoImpl.class);

	protected Mongo mongo;

	public BasicDaoImpl() {
		try {
			MongoOptions mongoOptions = new MongoOptions();
			mongoOptions.threadsAllowedToBlockForConnectionMultiplier = 5;
			mongoOptions.connectionsPerHost = 50;
			mongoOptions.autoConnectRetry = true;

			mongo = new Mongo("mongodb", mongoOptions);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
