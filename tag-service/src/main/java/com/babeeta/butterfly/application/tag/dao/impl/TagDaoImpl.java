package com.babeeta.butterfly.application.tag.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.tag.dao.TagDao;
import com.babeeta.butterfly.application.tag.entity.TagInfo;
import com.google.code.morphia.query.Query;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class TagDaoImpl extends BasicDaoImpl implements TagDao {
	private final static Logger logger = LoggerFactory
	        .getLogger(TagDaoImpl.class);

	public TagDaoImpl() {
		datastore = morphia.createDatastore(mongo, "tag");

		DBCollection dbCollection = datastore.getCollection(TagInfo.class);

		logger.info("ensure index started");
		BasicDBObject indexOptions = new BasicDBObject().append("background",
		        true);
		dbCollection.ensureIndex(new BasicDBObject().append("clientId", 1)
		        .append("tagName", 1), indexOptions);
		dbCollection.ensureIndex(
		        new BasicDBObject().append("aid", 1).append("tagName", 1),
		        indexOptions);
		dbCollection.ensureIndex(
		        new BasicDBObject().append("aid", 1).append("clientId", 1),
		        indexOptions);
		dbCollection.ensureIndex(
		        new BasicDBObject().append("aid", 1).append("clientId", 1)
		                .append("tagName", 1), indexOptions);

		logger.info("ensure index finished");
	}

	@Override
	public void addTag(TagInfo tag) {
		Query<TagInfo> query =
		        datastore.createQuery(TagInfo.class)
		                .filter("clientId", tag.getClientId())
		                .filter("tagName", tag.getTagName());
		if (query.get() == null) {
			datastore.save(tag);
		} else {
			logger.debug("[tag dao]addTag | tag[{}] on client[{}] exist.",
			        tag.getTagName(), tag.getClientId());
		}
	}

	@Override
	public void removeTag(TagInfo tag) {
		Query<TagInfo> query =
		        datastore.createQuery(TagInfo.class)
		                .filter("clientId", tag.getClientId())
		                .filter("tagName", tag.getTagName());
		if (query.get() != null) {
			datastore.delete(query);
		} else {
			logger.debug(
			        "[tag dao]removeTag | not found tag[{}] on client[{}].",
			        tag.getTagName(), tag.getClientId());
		}
	}

	/***
	 * 查询tag信息
	 * 
	 * @paramtagName tag名称
	 * 
	 * @aid applicationId
	 */
	@Override
	public List<TagInfo> queryClient(String tagName, String aid)
	{
		Query<TagInfo> query =
		        datastore.createQuery(TagInfo.class)
		                .filter("tagName", tagName).filter("aid", aid);

		return query.asList();

	}

	/***
	 * 查询tag信息
	 * 
	 * @clientId cid
	 * 
	 * @aid applicationId
	 */
	@Override
	public List<TagInfo> queryTag(String clientId, String aid)
	{
		Query<TagInfo> query =
		        datastore.createQuery(TagInfo.class)
		                .filter("clientId", clientId).filter("aid", aid);

		return query.asList();
	}

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName, String cid)
	{
		Query<TagInfo> query = this.datastore.createQuery(TagInfo.class)
		        .filter("aid", aid)
		        .filter("clientId", cid)
		        .filter("tagName", tagName);
		if (query.get() != null)
		{
			return true;
		}
		return false;
	}

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName)
	{
		Query<TagInfo> query = this.datastore.createQuery(TagInfo.class)
		        .filter("aid", aid)
		        .filter("tagName", tagName);
		if (query.get() != null)
		{
			return true;
		}
		return false;
	}

	@Override
	public int count(String tagName, String aid) {
		Query<TagInfo> query =
		        datastore.createQuery(TagInfo.class)
		                .filter("tagName", tagName).filter("aid", aid);
		return Integer.valueOf(String.valueOf(query.countAll()));
	}

	@Override
	public void cleanTag(String aid, String tName) {
		Query<TagInfo> query = this.datastore.createQuery(TagInfo.class)
		        .filter("aid", aid)
		        .filter("tagName", tName);
		this.datastore.delete(query);
	}
}
