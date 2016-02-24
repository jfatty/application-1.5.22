package com.oppo.push.service;

public interface PushService {

	public boolean pushToClient(String appId,String appKey,String cid,byte[] content);
}
