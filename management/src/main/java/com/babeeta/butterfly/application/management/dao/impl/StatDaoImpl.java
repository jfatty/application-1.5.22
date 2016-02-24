package com.babeeta.butterfly.application.management.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.babeeta.butterfly.application.management.dao.StatDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class StatDaoImpl implements StatDao {
	private DBCollection statCollection;
	private static final SimpleDateFormat SDF=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	public StatDaoImpl(Mongo mongo) {
		statCollection = mongo.getDB("stat").getCollection("stat");
	}

	public void ensureIndex() {
		BasicDBObject indexOptions = new BasicDBObject().append("background",
				true).append("unique", true);
		statCollection.ensureIndex(new BasicDBObject().append("rate", 1)
				.append("createTime", -1), indexOptions);
	}

	@Override
	public List<Map> getStats(String rate, int limit) {

		DBCursor cursor=statCollection.find(new BasicDBObject().append("rate", rate)).sort(
				new BasicDBObject().append("createTime", -1)).limit(limit);
		
		List<Map> list=new ArrayList<Map>();
		
		while(cursor.hasNext()){
			DBObject obj=cursor.next();
			Iterator<String> keyIt=obj.keySet().iterator();
			
			Map map=new LinkedHashMap();
			while(keyIt.hasNext()){
				String key=keyIt.next();
				if(key.equals("createTime")){
					map.put(key, SDF.format(obj.get(key)));
				}else if(key.equals("_id")){
					continue;
				}else{
					map.put(key, obj.get(key));
				}
			}
			list.add(map);
		}
		
		return list;
	}
}
