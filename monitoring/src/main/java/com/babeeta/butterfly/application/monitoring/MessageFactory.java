package com.babeeta.butterfly.application.monitoring;

import com.babeeta.butterfly.MessageRouting;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: XYuser
 * Date: 11-1-11
 * Time: 上午10:14
 * To change this template use File | Settings | File Templates.
 * <br>获取各个服务的消息对象
 */
public class MessageFactory {
    private final static Logger logger = LoggerFactory.getLogger(MessageFactory.class);

    private static final int APP_GATEWAY_MESSAGE = 1;
    private static final int ROUTER_DEV_MESSAGE = 2;
    private static final int ROUTER_GATEWAY_MESSAGE = 3;
    private static final int DEV_GATEWAY_MESSAGE = 4;
    private static final int ACCOUNTS_MESSAGE = 5;
    private static final int SUBSCRIPTION_MESSAGE = 6;

    private static Map<String, Integer> MESSAGE_MAPPING = new HashMap<String, Integer>();

    static {
        MESSAGE_MAPPING.put("0.gateway.app", APP_GATEWAY_MESSAGE);
        MESSAGE_MAPPING.put("1.gateway.app", APP_GATEWAY_MESSAGE);
        MESSAGE_MAPPING.put("2.gateway.app", APP_GATEWAY_MESSAGE);
        MESSAGE_MAPPING.put("3.gateway.app", APP_GATEWAY_MESSAGE);

        MESSAGE_MAPPING.put("0.gateway.dev", DEV_GATEWAY_MESSAGE);
        MESSAGE_MAPPING.put("1.gateway.dev", DEV_GATEWAY_MESSAGE);
        MESSAGE_MAPPING.put("2.gateway.dev", DEV_GATEWAY_MESSAGE);
        MESSAGE_MAPPING.put("3.gateway.dev", DEV_GATEWAY_MESSAGE);

        MESSAGE_MAPPING.put("dev", ROUTER_DEV_MESSAGE);
        MESSAGE_MAPPING.put("gateway.dev", ROUTER_GATEWAY_MESSAGE);
        MESSAGE_MAPPING.put("accounts.app", ACCOUNTS_MESSAGE);
        MESSAGE_MAPPING.put("accounts.dev", ACCOUNTS_MESSAGE);
        MESSAGE_MAPPING.put("subscription.dev", SUBSCRIPTION_MESSAGE);
    }

    public static MessageLite getMessage(String domain, String target) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        MessageRouting.Message message = MessageRouting.Message.newBuilder()
                .setFrom("monitoring@" + domain)
                .setContent(ByteString.copyFrom("monitoring".getBytes()))
                .setDate(System.currentTimeMillis())
                .setUid(id)
                .setTo("@" + target)
                .build();

        switch (MESSAGE_MAPPING.get(target)) {
            case APP_GATEWAY_MESSAGE:
                message = message.toBuilder()
                        .setFrom("auth@accounts.app")
                        .setTo("@" + target) //{node}.gateway.app
                        .build();
                break;
            case ROUTER_DEV_MESSAGE:
                message = message.toBuilder()
                        .setTo("4c1716c72604451181e3b5c45200471e.8968c4fe5538405ba3bfa4b61484f0a0@" + target) //cid.aid@dev
                        .build();
                break;
            case ROUTER_GATEWAY_MESSAGE:
                message = message.toBuilder()
                        .setTo("4c1716c72604451181e3b5c45200471e.8968c4fe5538405ba3bfa4b61484f0a0@" + target) //cid.aid@gateway.dev
                        .build();
                break;
            case DEV_GATEWAY_MESSAGE:
                message = message.toBuilder()
                        .setTo("4c1716c72604451181e3b5c45200471e.8968c4fe5538405ba3bfa4b61484f0a0@" + target) //cid.aid@{node}.gateway.dev
                        .build();
                break;
            case ACCOUNTS_MESSAGE:
                message = message.toBuilder()
                        .setTo("auth@" + target)
                        .setContent(ByteString.copyFromUtf8("8968c4fe5538405ba3bfa4b61484f0a0:a2d825d2a41a49819405bc4a2d85ebfc")) //aid:key
                        .setReplyFor("monitoring@" + domain)
                        .build();
                break;
            case SUBSCRIPTION_MESSAGE:
                message = message.toBuilder()
                        .setTo("bind@" + target)
                        .setContent(ByteString.copyFromUtf8("8968c4fe5538405ba3bfa4b61484f0a0:4c1716c72604451181e3b5c45200471e:e94901f9e41746f3b0ae3a518991c18e")) //aid:cid:did
                        .setReplyFor("monitoring@" + domain)
                        .build();
                break;
            default:
                message = null;
                logger.error("[Not find target service] {}", target);
                break;
        }
        return message;
    }
}
