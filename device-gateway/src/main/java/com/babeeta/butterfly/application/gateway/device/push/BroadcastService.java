package com.babeeta.butterfly.application.gateway.device.push;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageHandler;
import com.babeeta.butterfly.MessageRouting.Message;

public class BroadcastService implements MessageHandler {
	private static Logger logger = LoggerFactory
	        .getLogger(BroadcastService.class);
	private static final Pattern GROUP_SYCN_REGX = Pattern
	        .compile("([0-9a-z]+)\\.([0-9a-z]+)\\.([0-9a-z]+)\\.(.+)");
	private static final Pattern GROUP_SYCN_REGX2 = Pattern
	        .compile("([0-9a-z]+)\\.(.+)");
	/**
	 * Map<aid.tag,HashSet<did.cid>>
	 */
	private final Map<String, Set<String>> listener = new ConcurrentHashMap<String, Set<String>>();
	private static final BroadcastService DEFAULT_INSTANCE = new BroadcastService();
	private static final MessagePusher MESSAGE_PUSHER = MessagePusher
	        .getDefaultInstance();

	private BroadcastService() {
		Timer timer = new Timer("cleanOfflineClientFromBroadcastListenerTimer",
		        true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				cleanOfflineClient();
			}
		}, 30 * 60 * 1000, 30 * 60 * 1000);
	}

	public static synchronized BroadcastService getDefaultInstance() {
		return DEFAULT_INSTANCE;
	}

	private void cleanOfflineClient() {

		Set<String> tagSet = listener.keySet();

		if (tagSet.isEmpty()) {
			logger.debug("tag listener is emtpy");
			return;
		}
		logger.debug("tag listener size {}", listener.size());

		for (String tag : listener.keySet()) {

			Set<String> clientSet = listener.get(tag);

			if (clientSet == null || clientSet.isEmpty()) {
				logger.debug("[{}] listen on this tag clientSet is emtpy", tag);
				continue;
			}

			logger.debug("[{}] listen on this tag clientSet size is {}", tag,
			        clientSet.size());

			Iterator<String> clientIt = clientSet.iterator();

			while (clientIt.hasNext()) {
				try {
					String deviceId = clientIt.next().split("\\.")[0];

					if (!MESSAGE_PUSHER.isOnline(deviceId)) {
						clientIt.remove();
						logger.debug(
						        "[{}] device offline,remove from {} broadcast listener.",
						        deviceId, tag);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					break;
				}
			}
		}
	}

	@Override
	public void onMessage(Message message) {
		if (message.getTo().startsWith("cleanGroup@")) {
			Matcher matcher = GROUP_SYCN_REGX2.matcher(message.getContent()
			        .toStringUtf8());
			if (!matcher.matches()) {
				logger.error("[{}] invalid group sync messsage content {}",
				        message.getUid(), message.getContent().toStringUtf8());
			}
			String aid = matcher.group(1);
			String tag = matcher.group(2);

			clean(aid, tag);

		} else if (message.getTo().startsWith("broadcast@")) {
			broadcast(message);
		} else if (message.getTo().startsWith("addGroup@")
		        || message.getTo().startsWith("removeGroup@")) {
			Matcher matcher = GROUP_SYCN_REGX.matcher(message.getContent()
			        .toStringUtf8());
			if (!matcher.matches()) {
				logger.error("[{}] invalid group sync messsage content {}",
				        message.getUid(), message.getContent().toStringUtf8());
			}
			String did = matcher.group(1);
			String cid = matcher.group(2);
			String aid = matcher.group(3);
			String tag = matcher.group(4);

			if (message.getTo().startsWith("addGroup@")) {
				register(aid, tag, did, cid);
			} else {
				remove(aid, tag, did, cid);
			}
		} else {
			logger.error(
			        "BroadcastService can not handle this kind of message,message to is {}.",
			        message.getTo());
		}
	}

	public void register(String aid, String tag, String did, String cid) {
		String key = aid + "." + tag;
		if (MessagePusher.getDefaultInstance().resolve(did) == null) {
			logger.debug("[{}] device offline,ignore {}.{}", did, key);
			return;
		}

		Set<String> clientSet = listener.get(key);

		if (clientSet == null) {
			clientSet = (Set<String>) Collections
			        .newSetFromMap(new ConcurrentHashMap<String, Boolean>());// means
			                                                                 // ConcurrentHashSet
			listener.put(key, clientSet);
		}

		String client = did + "." + cid;

		if (clientSet.contains(client)) {
			logger.debug("[{}] client already exists in tag {} listener",
			        client, key);
		} else {
			clientSet.add(client);
			logger.debug("register {}.{} to {} listener finished",
			        new Object[] { did, cid, key });
		}
	}

	public void remove(String aid, String tag, String did, String cid) {
		String key = aid + "." + tag;
		Set clientSet = listener.get(key);

		if (clientSet == null || clientSet.size() == 0) {
			logger.info("no client on {}.{}", aid, tag);
			return;
		}
		clientSet.remove(did + "." + cid);

		logger.debug("remove {}.{} from {}.{} list finished.", new Object[] {
		        did, cid, aid, tag });
	}

	private void broadcast(Message message) {
		logger.debug("recieve broadcast msg from {}", message.getFrom());

		if (!message.getBroadcast()) {
			logger.error("[{}] is not a broadcast msg.", message.getUid());
			return;
		}

		String key = message.getFrom().substring(0,
		        message.getFrom().indexOf("@"));
		Set<String> clientSet = listener.get(key);

		logger.info("[{}] ready to broadcast msg", key);

		if (clientSet == null || clientSet.size() == 0) {
			logger.debug("no client listen on {}", key);
			return;
		}
		String aid = key.substring(0, key.indexOf("."));

		for (String client : clientSet) {
			MessagePusher.getDefaultInstance().push(
			        message.toBuilder().setTo(client + "." + aid + "@dev")
			                .build());
		}
	}

	public void clean(String aid, String tag) {
		String key = aid + "." + tag;
		if (listener.containsKey(key)) {
			listener.remove(key);
		}

		logger.debug("clean {}.{} list finished.", new Object[] { aid, tag });
	}
}
