package com.babeeta.butterfly.subscription.dao;

import java.util.List;
import java.util.Map;

public interface SubscriptionDao {

	public void save(String aid, String cid, String did);

	public void remove(String aid, String cid);

	public boolean exists(String aid, String cid);
	
	public List<Map> list(String did);
}
