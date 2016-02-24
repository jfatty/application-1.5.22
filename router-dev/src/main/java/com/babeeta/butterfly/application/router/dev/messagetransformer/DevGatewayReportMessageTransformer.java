package com.babeeta.butterfly.application.router.dev.messagetransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.router.dev.DevRouter;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class DevGatewayReportMessageTransformer implements MessageTransformer {

	static final String FIELD__ID = "_id";

	static final String DB_DEV_GATEWAY = "dev_gateway";

	private final Mongo mongo;

	private static final Logger logger = LoggerFactory
			.getLogger(DevRouter.class);

	public DevGatewayReportMessageTransformer(Mongo mongo) {
		this.mongo = mongo;
	}

	@Override
	public Message transform(Message message) {
		saveDevGateway(message.getContent().toStringUtf8());
		return null;
	}

	private DBCollection getDevGatewayCollection() {
		DB db = mongo.getDB(DB_DEV_GATEWAY);
		DBCollection dbCollection = db.getCollection(DB_DEV_GATEWAY);
		return dbCollection;
	}

	private void saveDevGateway(String devGateway) {
		getDevGatewayCollection().save(
				new BasicDBObject().append(FIELD__ID, devGateway));
	}

}
