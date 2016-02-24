package com.babeeta.butterfly.application.tag.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.application.tag.dao.TagDao;
import com.babeeta.butterfly.application.tag.entity.TagInfo;
import com.babeeta.butterfly.application.tag.service.TagService;
import com.babeeta.butterfly.application.tag.subscription.SubscriptionDao;
import com.babeeta.butterfly.router.network.MessageSenderImpl;
import com.google.protobuf.ByteString;

/****
 * 
 * @update zeyong.xia
 * @date 2011-9-21
 */
public class TagServiceImpl implements TagService {
	private final static Logger logger = LoggerFactory
	        .getLogger(TagServiceImpl.class);
	private TagDao tagDao;

	private SubscriptionDao subscriptionDaoImpl;
	private static final MessageSender MESSAGE_SENDER = new MessageSenderImpl();

	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	public void setSubscriptionDaoImpl(SubscriptionDao subscriptionDaoImpl) {
		this.subscriptionDaoImpl = subscriptionDaoImpl;
	}

	/****
	 * 新增tag
	 */
	public void registerTag(String clientId, String tagName, String aid) {

		logger.debug("[tag service]registerTag set tag[{}] to [{}].", tagName,
		        clientId);
		boolean flag = this.tagDao.existsTag(aid, tagName, clientId);
		if (flag) {
			return;
		}
		TagInfo tag = new TagInfo();
		tag.setClientId(clientId);
		tag.setTagName(tagName);
		tag.setAid(aid);
		tagDao.addTag(tag);
		sendAddGroupMsg(clientId, aid, tagName);
	}

	/***
	 * 删除tag
	 * 
	 * @param clientId
	 * @param tagName
	 * @param aid
	 */
	public void unregisterTag(String clientId, String tagName, String aid) {

		logger.debug("[tag service]unregisterTag remove tag[{}] from [{}].",
		        tagName, clientId);
		TagInfo tag = new TagInfo();
		tag.setClientId(clientId);
		tag.setTagName(tagName);
		tag.setAid(aid);
		tagDao.removeTag(tag);

		sendRemoveGroupMsg(clientId, aid, tagName);
	}

	/***
	 * 查询tag信息
	 * 
	 * @paramtagName tag名称
	 * 
	 * @aid applicationId
	 */
	public List<String> queryClient(String tagName, String aid) {
		logger.debug("[tag service]queryClient .tagName is  [{}].", tagName);
		List<TagInfo> clientList = tagDao.queryClient(tagName, aid);

		if (clientList != null) {
			logger.debug(
			        "[tag service]queryClient, clientList which has tag[{}].",
			        clientList.size(), tagName);
			List<String> result = new ArrayList<String>();
			for (TagInfo record : clientList) {
				if (!result.contains(record.getClientId())
				        && record.getClientId() != null) {
					result.add(record.getClientId());
				}
			}
			return result;
		} else {
			logger.debug("[tag service]queryClient,no client from {}", tagName);
			return null;
		}
	}

	/***
	 * 查询tag信息
	 * 
	 * @clientId cid
	 * 
	 * @aid applicationId
	 */
	public List<String> queryTag(String client, String aid) {
		logger.debug("[tag service]queryTag , on client [{}].", client);
		List<TagInfo> tagList = tagDao.queryTag(client, aid);

		if (tagList != null) {
			logger.debug("Found [{}] tag on [{}].", tagList.size(), client);
			List<String> result = new ArrayList<String>();
			for (TagInfo record : tagList) {
				if (!result.contains(record.getTagName())) {
					result.add(record.getTagName());
				}
			}
			if (result.size() > 0) {
				return result;
			} else {
				logger.debug("No other tag except userId.");
				return null;
			}
		} else {
			logger.error("Invalid client [{}].", client);
			return null;
		}
	}

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName) {
		return this.tagDao.existsTag(aid, tagName);
	}

	/***
	 * 判断tag存不存在
	 * 
	 * @param aid
	 * @param tagName
	 * @return
	 */
	public boolean existsTag(String aid, String tagName, String cid) {
		return this.tagDao.existsTag(aid, tagName, cid);
	}

	/***
	 * 判断是否存在
	 * 
	 * @param aid
	 * @param cid
	 * @return
	 */
	public boolean existsCid(String aid, String cid) {
		return this.subscriptionDaoImpl.exists(aid, cid);
	}

	/**
	 * @param tagName
	 * @param aid
	 * @return
	 */
	public int count(String tagName, String aid) {
		return tagDao.count(tagName, aid);
	}

	private void sendAddGroupMsg(String cid, String aid, String tag) {
		MessageRouting.Message.Builder builder = MessageRouting.Message
		        .newBuilder()
		        .setDate(System.currentTimeMillis())
		        .setFrom(
		                "addGroup@tag.app")
		        .setUid(
		                UUID.randomUUID().toString().replaceAll("-", ""))
		        .setBroadcast(false)
		        .setContent(
		                ByteString.copyFrom((cid + "." + aid + "." + tag)
		                        .getBytes())).setTo(
		                "addGroup@dev");

		MESSAGE_SENDER.send(builder.build());
	}

	private void sendRemoveGroupMsg(String cid, String aid, String tag) {
		MessageRouting.Message.Builder builder = MessageRouting.Message
		        .newBuilder()
		        .setDate(System.currentTimeMillis())
		        .setFrom(
		                "removeGroup@tag.app")
		        .setUid(
		                UUID.randomUUID().toString().replaceAll("-", ""))
		        .setBroadcast(false)
		        .setContent(
		                ByteString.copyFrom((cid + "." + aid + "." + tag)
		                        .getBytes())).setTo(
		                "removeGroup@dev");

		MESSAGE_SENDER.send(builder.build());
	}

	@Override
	public void cleanTag(String aid, String tName) {
		this.tagDao.cleanTag(aid, tName);

		MessageRouting.Message.Builder builder = MessageRouting.Message
		        .newBuilder()
		        .setDate(System.currentTimeMillis())
		        .setFrom("cleanGroup@tag.app")
		        .setUid(UUID.randomUUID().toString().replaceAll("-", ""))
		        .setBroadcast(false)
		        .setContent(
		                ByteString.copyFrom((aid + "." + tName)
		                        .getBytes())).setTo("cleanGroup@dev");

		MESSAGE_SENDER.send(builder.build());
	}
}
