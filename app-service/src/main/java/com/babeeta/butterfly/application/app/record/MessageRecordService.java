package com.babeeta.butterfly.application.app.record;

import com.babeeta.butterfly.application.app.record.dao.MessageRecordDao;
import com.babeeta.butterfly.application.app.record.dao.impl.MessageRecordDaoImpl;
import com.babeeta.butterfly.application.app.reliablepush.dao.ReliablePushDao;
import com.babeeta.butterfly.application.app.reliablepush.dao.impl.ReliablePushDaoImpl;

public class MessageRecordService {
	private static final MessageRecordDao messageRecordDao = new MessageRecordDaoImpl();

	private static final MessageRecordService defaultInstance = new MessageRecordService();
	
	private static final ReliablePushDao reliablePushDao =new ReliablePushDaoImpl();
	
	public static ReliablePushDao getReliablepushdao() {
		return reliablePushDao;
	}

	private MessageRecordService() {

	}

	public static MessageRecordService getDefaultInstance() {
		return defaultInstance;
	}

	public static MessageRecordDao getDao() {
		return messageRecordDao;
	}
}
