package com.babeeta.butterfly.application.words.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public class BasicDaoImpl {
	private final static Logger logger = LoggerFactory
			.getLogger(BasicDaoImpl.class);
	protected final Morphia morphia;
	protected Mongo mongo;
	protected Datastore datastore;

	public BasicDaoImpl() {
		morphia = new Morphia();
		try {
			MongoOptions mongoOptions = new MongoOptions();
			mongoOptions.threadsAllowedToBlockForConnectionMultiplier = 512;
			mongoOptions.connectionsPerHost = 512;
			mongoOptions.autoConnectRetry = true;

			mongo = new Mongo("mongodb.", mongoOptions);
			morphia.mapPackage("com.babeeta.butterfly.application.words.entity");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
