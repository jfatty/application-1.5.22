package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.babeeta.butterfly.MessageRouting;
import com.babeeta.butterfly.MessageRouting.Credential;
import com.babeeta.butterfly.MessageRouting.DeviceRegister;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.application.gateway.device.ServerContext;
import com.babeeta.butterfly.application.gateway.device.push.MessagePusher;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelLet;
import com.babeeta.butterfly.application.gateway.device.tunnel.let.monitor.ThreadPool;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Descriptors.FieldDescriptor;

public class DeviceRegisterTunnelLet implements TunnelLet<DeviceRegister> {

	private static final Logger logger = LoggerFactory
			.getLogger(DeviceRegisterTunnelLet.class);

	private final ServerContext serverContext;
	
	private final int MAX_CONNECTION = 128;

	public static AtomicLong DEVICE_REGISTER_COUNT = new AtomicLong(0);

	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(32, MAX_CONNECTION,
			1, TimeUnit.HOURS,
			new LinkedBlockingQueue<Runnable>(1000),
			new NamedThreadFactory(
					"DeviceRegister-HC-"));

	private final ThreadLocal<HttpClient> localHttpClient = new ThreadLocal<HttpClient>();

	public DeviceRegisterTunnelLet(ServerContext serverContext) {
		super();
		this.serverContext = serverContext;
		this.serverContext.registerMBean(
				DeviceRegisterTunnelLet.class.getSimpleName()
						+ ":name=DeviceRegisterExecutor", new ThreadPool(
						executor));
	}

	@Override
	public void messageReceived(
			final TunnelContext tunnelContext,
			final TunnelData<DeviceRegister> data) {
		DEVICE_REGISTER_COUNT.getAndIncrement();
		try {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					onDeviceRegister(tunnelContext, data);
				}
			});
		} catch (RejectedExecutionException e) {
			logger.error("[{}]Device register failed due to overload.",
					tunnelContext.getChannel().getId());

			tunnelContext.getChannel().write(
					new TunnelData<MessageLite>(data.tag,
							MessageRouting.MessageCMD.RESPONSE.getNumber(),
							MessageRouting.Response
									.newBuilder()
									.setStatus("SERVICE_UNAVAILABLE")
									.build()));
		}
	}

	private String convertToJson(DeviceRegister deviceRegister) {
		Map<FieldDescriptor, Object> map = deviceRegister.getAllFields();
		Map<String, Object> stringMap = new HashMap<String, Object>();
		Iterator<FieldDescriptor> it = map.keySet().iterator();
		while (it.hasNext()) {
			FieldDescriptor key = it.next();
			String name = key.getFullName().substring(
					key.getFullName().lastIndexOf('.') + 1);
			stringMap.put(name, map.get(key));
		}
		JSONObject jsonObject = JSONObject.fromObject(stringMap);
		return "{\"extra\":" + jsonObject.toString() + "}";
	}

	private void doOk(final TunnelContext tunnelContext,
			final TunnelData<DeviceRegister> data, HttpResponse httpResponse)
			throws IOException {
		JSONObject obj = JSONObject.fromObject(EntityUtils
				.toString(httpResponse.getEntity()));
		Credential credential = Credential.newBuilder()
				.setId(obj.getString("id"))
				.setSecureKey(obj.getString("secureKey"))
				.build();
		tunnelContext.setDeviceId(credential.getId());
		logger.debug("doOk create did [{}]",credential.getId());
		MessagePusher.getDefaultInstance().register(credential.getId(),
				tunnelContext.getChannel());
		reportToGateway(credential.getId());
		TunnelData<Credential> result = new TunnelData<MessageRouting.Credential>(
				data.tag, 136, credential);
		tunnelContext.getChannel().write(result);
		logger.info("[{}] Sent new device to client. [{}]",
				tunnelContext.getChannel()
						.getId(), obj.toString());
	}

	private HttpClient getHttpClient() {
		HttpClient client = localHttpClient.get();
		if (client == null) {

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(
						new Scheme("http", 80, PlainSocketFactory
								.getSocketFactory()));

			ThreadSafeClientConnManager httpClientConnectionManager = new ThreadSafeClientConnManager(
					schemeRegistry);

			httpClientConnectionManager.setMaxTotal(MAX_CONNECTION);
			httpClientConnectionManager.setDefaultMaxPerRoute(MAX_CONNECTION);
		
			
			client = new DefaultHttpClient(httpClientConnectionManager);
			localHttpClient.set(client);
		}
		return client;
	}

	private void onDeviceRegister(final TunnelContext tunnelContext,
			final TunnelData<DeviceRegister> data) {
		DeviceRegister deviceRegister = data.obj;
		String requestBody = convertToJson(deviceRegister);
		logger.debug("onDeviceRegister requestBody {}",requestBody);
		HttpPost httpPost = new HttpPost(
				"http://accounts.dev/api/register");
		httpPost.setHeader(new BasicHeader("Content-type",
				"application/json"));
		httpPost.setEntity(new ByteArrayEntity(requestBody.getBytes()));
		try {
			HttpResponse httpResponse = getHttpClient().execute(
					httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == status) {
				doOk(tunnelContext, data, httpResponse);
			} else {
				logger.error(
						"Unexpectant response status :{} while registering on {}.",
						status, "accounts.dev");
			}
		} catch (IOException e) {
			logger.error("Error occured while request [{}],{}",
					"accounts.dev",
					e.getMessage());
		}
	}

	private void reportToGateway(String id) {
		logger.debug("[{}]Report to gateway.dev.", id);
		Message msg = Message
				.newBuilder()
				.setContent(ByteString.copyFromUtf8(id))
				.setDate(System.currentTimeMillis())
				.setFrom(
						"report@" + serverContext.messageServiceAddress)
				.setTo("update@gateway.dev")
				.setUid(UUID.randomUUID().toString()
						.replaceAll("\\-", ""))
				.build();
		serverContext.messageSender.send(msg);
	}
}