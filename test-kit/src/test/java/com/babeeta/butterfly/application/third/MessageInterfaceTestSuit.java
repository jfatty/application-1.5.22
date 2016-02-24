package com.babeeta.butterfly.application.third;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.babeeta.butterfly.application.TestEnviroment;
import com.babeeta.butterfly.application.third.Message.DeleteMessageTestClient;
import com.babeeta.butterfly.application.third.Message.ModifyMessageContentTestClient;
import com.babeeta.butterfly.application.third.Message.PushBroadcastMessageTestClient;
import com.babeeta.butterfly.application.third.Message.PushSingleMessageTestClient;
import com.babeeta.butterfly.application.third.Message.QueryMessageStatusTestClient;
import com.babeeta.butterfly.application.third.tag.SetTagClient;

import static org.junit.Assert.assertEquals;

public class MessageInterfaceTestSuit {
	private static String appId = TestEnviroment.APP_ID;
	private static String appKey = TestEnviroment.APP_KEY;
	private QueryMessageStatusTestClient queryMessageStatusTestClient;
	private String clientId = null;

	@Before
	public void init() {
		queryMessageStatusTestClient = new QueryMessageStatusTestClient(appId,
				appKey);
		queryMessageStatusTestClient.setHOST(TestEnviroment.APP_HOST);

		DaoInit daoInit = new DaoInit();
		String did = daoInit.initDevAccount();
		clientId = daoInit.initSubscription(appId, did);
	}

	@Test
	public void testQueryMessageNotFound() {
		String response = queryMessageStatusTestClient.queryStatus("notExists");
		assertEquals(404, queryMessageStatusTestClient.getResponseStatus());
	}

	@Test
	public void testQueryDelayingStatusMessage() {
		String messageId = pushDelayingMessage();

		QueryMessageStatusTestClient queryMessageStatusTestClient = new QueryMessageStatusTestClient(
				appId, appKey);

		Assert.assertEquals("DELAYING",
				queryMessageStatusTestClient.queryStatus(messageId));
	}

	@Test
	public void testUpdateDelayingStatusMessage() {
		String messageId = pushDelayingMessage();
		ModifyMessageContentTestClient modifyMessageContentTestClient = new ModifyMessageContentTestClient(
				appId, appKey);

		modifyMessageContentTestClient.modify(messageId, "text", "newContent");

		Assert.assertEquals(200, modifyMessageContentTestClient
				.getResponseStatus());
	}

	@Test
	public void testUpdateDelivingStatusMessage() {
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				appId, appKey);
		pushSingleMessageTestClient.setDelay(0);
		pushSingleMessageTestClient.setLife(2);

		String messageId = pushSingleMessageTestClient.push(clientId, "text");

		ModifyMessageContentTestClient modifyMessageContentTestClient = new ModifyMessageContentTestClient(
				appId, appKey);

		modifyMessageContentTestClient.modify(messageId, "text", "newContent");

		assertEquals(200, modifyMessageContentTestClient.getResponseStatus());
	}

	@Test
	public void testUpdateDeletedStatusMessage() {
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				appId, appKey);
		pushSingleMessageTestClient.setDelay(0);

		String messageId = pushSingleMessageTestClient.push(clientId, "text");

		DeleteMessageTestClient deleteMessageTestClient = new DeleteMessageTestClient(
				appId, appKey);
		deleteMessageTestClient.delete(messageId);

		assertEquals(200, deleteMessageTestClient.getResponseStatus());

		ModifyMessageContentTestClient modifyMessageContentTestClient = new ModifyMessageContentTestClient(
				appId, appKey);

		modifyMessageContentTestClient.modify(messageId, "text", "newContent");

		assertEquals(409, modifyMessageContentTestClient.getResponseStatus());
	}

	@Test
	public void testDeleteBroadCastMessage() {

		SetTagClient setTagClient = new SetTagClient(appId, appKey);

		String tag = "" + System.currentTimeMillis();
		setTagClient.set(clientId, tag);

		PushBroadcastMessageTestClient pushBroadcastMessageTestClient = new PushBroadcastMessageTestClient(
				appId, appKey);

		String messageId = pushBroadcastMessageTestClient.broadcast(tag);

		assertEquals(200, pushBroadcastMessageTestClient.getResponseStatus());

		DeleteMessageTestClient deleteMessageTestClient = new DeleteMessageTestClient(
				appId, appKey);

		deleteMessageTestClient.delete(messageId);

		assertEquals(409, deleteMessageTestClient.getResponseStatus());
	}

	@Test
	public void testDeleteDelivingMessage() {
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				appId, appKey);

		String messageId = pushSingleMessageTestClient.push(clientId, "text");

		QueryMessageStatusTestClient queryMessageStatusTestClient = new QueryMessageStatusTestClient(
				appId, appKey);

		assertEquals("DELIVERING", queryMessageStatusTestClient
				.queryStatus(messageId));

		DeleteMessageTestClient deleteMessageTestClient = new DeleteMessageTestClient(
				appId, appKey);

		deleteMessageTestClient.delete(messageId);

		assertEquals(200, deleteMessageTestClient.getResponseStatus());
	}

	@Test
	public void testDeleteOthersMessage() {
		DaoInit daoInit = new DaoInit();
		String[] otherAppAccount = daoInit.initAppAccount();
		String otherAppId = otherAppAccount[0];
		String otherAppKey = otherAppAccount[1];
		String did = daoInit.initDevAccount();
		String otherCid = daoInit.initSubscription(otherAppId, did);

		// 用另一个app发消息
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				otherAppId, otherAppKey);
		String messageId = pushSingleMessageTestClient.push(otherCid, "text");
		assertEquals(200, pushSingleMessageTestClient.getResponseStatus());

		// 验证删除另外一个app的消息报404错误
		DeleteMessageTestClient deleteMessageTestClient = new DeleteMessageTestClient(
				appId, appKey);
		deleteMessageTestClient.delete(messageId);

		assertEquals(404, deleteMessageTestClient.getResponseStatus());

		// 验证另一个app自己可以删除

		DeleteMessageTestClient otherAppDeleteClient = new DeleteMessageTestClient(
				otherAppId, otherAppKey);

		otherAppDeleteClient.delete(messageId);

		assertEquals(200, otherAppDeleteClient.getResponseStatus());
	}

	@Test
	public void testModifyOthersMessge() {
		DaoInit daoInit = new DaoInit();
		String[] otherAppAccount = daoInit.initAppAccount();
		String otherAppId = otherAppAccount[0];
		String otherAppKey = otherAppAccount[1];
		String did = daoInit.initDevAccount();
		String otherCid = daoInit.initSubscription(otherAppId, did);

		// 用另一个app发消息
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				otherAppId, otherAppKey);
		String messageId = pushSingleMessageTestClient.push(otherCid, "text");

		assertEquals(200, pushSingleMessageTestClient.getResponseStatus());

		// 验证修改另外一个app的消息报404错误
		ModifyMessageContentTestClient modifyMessageContentTestClient = new ModifyMessageContentTestClient(
				appId, appKey);
		modifyMessageContentTestClient.modify(messageId, "text", "shit");

		assertEquals(404, modifyMessageContentTestClient.getResponseStatus());

		// 验证另一个app自己可以修改
		ModifyMessageContentTestClient otherModifyClient = new ModifyMessageContentTestClient(
				otherAppId, otherAppKey);
		
		otherModifyClient.modify(messageId, "text", "newContent");
		
		assertEquals(200, otherModifyClient.getResponseStatus());
	}
	@Test
	public void testQueryOtherMessage(){
		DaoInit daoInit = new DaoInit();
		String[] otherAppAccount = daoInit.initAppAccount();
		String otherAppId = otherAppAccount[0];
		String otherAppKey = otherAppAccount[1];
		String did = daoInit.initDevAccount();
		String otherCid = daoInit.initSubscription(otherAppId, did);

		// 用另一个app发消息
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				otherAppId, otherAppKey);
		String messageId = pushSingleMessageTestClient.push(otherCid, "text");

		assertEquals(200, pushSingleMessageTestClient.getResponseStatus());

		//验证查询另外一个app的消息报404错误
		QueryMessageStatusTestClient queryMessageStatusTestClient=new QueryMessageStatusTestClient(appId, appKey);
		queryMessageStatusTestClient.queryStatus(messageId);
		assertEquals(404, queryMessageStatusTestClient.getResponseStatus());
		
		//验证另外一个app自己可以删除
		QueryMessageStatusTestClient otherQueryClient=new QueryMessageStatusTestClient(otherAppId, otherAppKey);
		otherQueryClient.queryStatus(messageId);
		
		assertEquals(200, otherQueryClient.getResponseStatus());
	}
	
	private String pushDelayingMessage() {
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				appId, appKey);
		pushSingleMessageTestClient.setDelay(2);

		return pushSingleMessageTestClient.push(clientId, "text");
	}
	@Test
	public void testDelayMsg(){
		PushSingleMessageTestClient pushSingleMessageTestClient=new PushSingleMessageTestClient(appId, appKey);
	
		pushSingleMessageTestClient.setLife(4);
		
		pushSingleMessageTestClient.push(clientId,"text");
		
		
		for(int i=0;i<20;i++){
			pushSingleMessageTestClient.push(clientId, "text");
		}
	
	}
	@Test
	public void testPush(){
	}
	
	public static void main(String[] args) {
//		SetTagClient setTagClient=new SetTagClient(appId, appKey);
//		setTagClient.set("5d6d6e6636f741a19ca31208e9a7e254", "N1");
		
		PushBroadcastMessageTestClient pushBroadcastMessageTestClient=new PushBroadcastMessageTestClient(appId, appKey);
		pushBroadcastMessageTestClient.broadcast("N1");
	}
	
}