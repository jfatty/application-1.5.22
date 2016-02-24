package com.babeeta.butterfly.application.management.dao;

import java.util.List;
import java.util.Map;

public interface StatDao {
	public List<Map> getStats(String rate,int limit);
	
	public void ensureIndex();
}
