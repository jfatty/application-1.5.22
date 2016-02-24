package com.babeeta.butterfly.application.third.service.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.application.reliable.MessageStatus;
import com.babeeta.butterfly.application.reliable.ReliablePush;
import com.babeeta.butterfly.application.reliable.dao.ReliablePushDao;
import com.babeeta.butterfly.application.reliable.dao.ReliablePushDaoImpl;
import com.babeeta.butterfly.application.third.service.AbstractHttpRPCService;
import com.babeeta.butterfly.application.third.service.NamedService;
import com.babeeta.butterfly.application.third.service.app.AppAccountService;

public class FeedbackServiceImpl implements FeedbackService {

	private static final Logger log = LoggerFactory
			.getLogger(FeedbackServiceImpl.class);
	private AppAccountService appAccountService;
	private NamedService namedService;
	private ReliablePushDao reliablePushDao;
	private DefaultHttpClient httpClient;
	private static final String CONTENT_TYPE_JSON = "application/json";
	private List<Map> feedbackAccountList = null;
	private int maxFeedbackNumPerTime = 500;

	public FeedbackServiceImpl() {
		reliablePushDao = ReliablePushDaoImpl.getDefaultInstance();

		ThreadSafeClientConnManager threadSafeClientConnManager = new ThreadSafeClientConnManager();
		threadSafeClientConnManager.setMaxTotal(100);
		threadSafeClientConnManager.setDefaultMaxPerRoute(100);
		httpClient = new DefaultHttpClient(threadSafeClientConnManager);
	}

	@Override
	public boolean initFeedbackAccount() {
		feedbackAccountList = appAccountService
				.getFeedbackServiceEnableAccount();
		if (feedbackAccountList == null || feedbackAccountList.size() == 0) {
			log.info("feedback service enable app account list is emtpy");
			return false;
		}
		return true;
	}

	@Override
	public void release() {
		reliablePushDao.unsetExpiredNotifyBy(namedService.getNodeName());
	}

	@Override
	public void expiredMessageFeedback() {
		if (feedbackAccountList == null || feedbackAccountList.size() == 0) {
			log.info("feedback service enable app account list is emtpy");
			return;
		}

		log.info("start to feedback expired Message.");

		for (Map account : feedbackAccountList) {
			if (account == null || account.isEmpty()) {
				log.error("empty app account");
				continue;
			}

			if (!account.containsKey("id")) {
				log.error("app account must have id property");
				continue;
			}

			String appId = (String) account.get("id");

			if (!account.containsKey("expiredMsgFeedbackUrl")) {
				log.error(
						"app account [{}] must have expiredMsgFeedbackUrl property",
						appId);
				continue;
			}

			String feedbackUrl = (String) account.get("expiredMsgFeedbackUrl");

			notifyByAid(appId, feedbackUrl);
		}

		log.info("expired Message Feedback finished.");
	}

	private void notifyByAid(String appId, String feedbackUrl) {
		List<String> msgList = new ArrayList<String>();

		while (true) {
			if (msgList.size() >= maxFeedbackNumPerTime) {
				log.debug("aid {} reach maxFeedbackNumPerTime {}", appId,
						maxFeedbackNumPerTime);
				notifyExpiredMsgToSubscriber(feedbackUrl, msgList);
				notifyByAid(appId, feedbackUrl);
				return;
			}
			Map reliablePush = reliablePushDao.findAndModifyExpriedMsg(appId,
					namedService.getNodeName());

			if (reliablePush == null) {
				log.debug("app {} has no already expired msg in db", appId);
				break;
			}

			msgList.add((String) reliablePush.get(ReliablePush.FIELD_ID));
		}

		while (true) {
			if (msgList.size() >= maxFeedbackNumPerTime) {
				log.debug("aid {} reach maxFeedbackNumPerTime {}", appId,
						maxFeedbackNumPerTime);
				notifyExpiredMsgToSubscriber(feedbackUrl, msgList);
				notifyByAid(appId, feedbackUrl);
				return;
			}

			Map reliablePush = reliablePushDao.findAndModifyDeliveringMsg(
					appId, new Date(), namedService.getNodeName());

			if (reliablePush == null) {
				log.debug("app {} has no msg to be expired msg in db", appId);
				break;
			}

			msgList.add((String) reliablePush.get(ReliablePush.FIELD_ID));
		}
		if (msgList.size() == 0) {
			log.info("app {} expired msg list is emtpy", appId);
			return;
		}
		notifyExpiredMsgToSubscriber(feedbackUrl, msgList);

	}

	private boolean notifyExpiredMsgToSubscriber(final String feedbackUrl,
			List<String> expiredMessageIdList) {
		HttpPut httpPut = new HttpPut(feedbackUrl);
		httpPut.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON);
		String feedBackJsonContent = JSONArray.fromObject(expiredMessageIdList)
				.toString();

		HttpResponse httpResponse = null;

		try {
			StringEntity stringEntity = new StringEntity(feedBackJsonContent);

			httpPut.setEntity(stringEntity);

			log.debug(
					"notify exprired msg started,feedbackUrl {},http put content is {}",
					feedbackUrl, feedBackJsonContent);

			httpResponse = httpClient.execute(httpPut);

		} catch (Exception e) {
			log.error("notify expired msg to subscriber failed,feedback url "
					+ feedbackUrl, e);
			return false;
		}

		int responseCode = httpResponse.getStatusLine().getStatusCode();
		String responseContent = null;
		try {
			responseContent = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		log.info(
				"notify exprired msg to {} {},request content is {},http response code is {},http response content is {}",
				new Object[] { feedbackUrl,
						responseCode == 200 ? "success" : "failed",
						feedBackJsonContent, responseCode, responseContent });

		return responseCode == 200;
	}

	public void setAppAccountService(AppAccountService appAccountService) {
		this.appAccountService = appAccountService;
	}

	public void setNamedService(NamedService namedService) {
		this.namedService = namedService;
	}

	public FeedbackServiceImpl(ReliablePushDao reliablePushDao) {
		this.reliablePushDao = reliablePushDao;
	}

}
