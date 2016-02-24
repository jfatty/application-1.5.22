package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
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
import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.MessageRouting.ServiceBind;
import com.babeeta.butterfly.application.gateway.device.ServerContext;
import com.babeeta.butterfly.application.gateway.device.push.BroadcastService;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelLet;
import com.google.protobuf.MessageLite;

public class ServiceBindTunnelLet implements TunnelLet<ServiceBind> {

	private static final Logger logger = LoggerFactory
			.getLogger(ServiceBindTunnelLet.class);

	@SuppressWarnings("unused")
	private final ServerContext serverContext;

	public static AtomicLong SERVICE_BIND_COUNT = new AtomicLong(0);

	private ThreadSafeClientConnManager httpClientConnectionManager;

	private final int MAX_CONNECTION = 256;

	public final ThreadPoolExecutor executor = new ThreadPoolExecutor(64,
			MAX_CONNECTION, 10,
			TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000));
	
	private final ThreadLocal<HttpClient> localHttpClient = new ThreadLocal<HttpClient>();
	
	private final static String ALL_BROADCAST_TAG="all";

	private final UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("Uncaught exception: ", e);

		}
	};

	public ServiceBindTunnelLet(ServerContext serverContext) {
		super();
		this.serverContext = serverContext;
		setup();
	}

	@Override
	public void messageReceived(final TunnelContext tunnelContext,
								final TunnelData<ServiceBind> data) {
		SERVICE_BIND_COUNT.getAndIncrement();
		try {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setName("Bind");
					Thread.currentThread().setUncaughtExceptionHandler(
							uncaughtExceptionHandler);
					if (tunnelContext.getDeviceId() == null) {
						tunnelContext.getChannel().write(
								new TunnelData<MessageLite>(data.tag, 135,
										Response
												.newBuilder()
												.setStatus("ERROR").build()));
						logger.debug(
								"[{}] TunnelContext getDeviceId is null. [{}]",
								tunnelContext.getChannel().getId(),
								data.obj.getApplicationId()
										+ ":"
										+ (data.obj.getClientId() == null ? "null"
												: data.obj.getClientId())
								);
						return;
					}
					bind(tunnelContext, data);
				}

			});
		} catch (RejectedExecutionException e) {
			logger.error("[{}]Service bind failed due to over load.",
					tunnelContext
							.getChannel().getId());
			tunnelContext.getChannel().write(
					new TunnelData<MessageLite>(data.tag,
							MessageRouting.MessageCMD.RESPONSE.getNumber(),
							MessageRouting.Response
									.newBuilder()
									.setStatus("SERVICE_UNAVAILABLE")
									.build()));
		}

	}

	private void bind(final TunnelContext tunnelContext,
						final TunnelData<ServiceBind> data) {
		logger.debug("service bind aid={} cid={},did={}",new Object[]{data.obj.getApplicationId(),data.obj.getClientId()==null?"":data.obj.getClientId(),tunnelContext.getDeviceId()});
		long timestamp = System.currentTimeMillis();

		HttpPost httpPost = new HttpPost("http://subscription.dev/api/bind");
		httpPost.setHeader(new BasicHeader("Content-type",
				"application/json"));
		String requestEntity = "{\"aid\":\"" + data.obj.getApplicationId()
				+ "\",\"cid\":\"" + data.obj.getClientId() + "\",\"did\":\""
				+ tunnelContext.getDeviceId() + "\"}";
		httpPost.setEntity(new ByteArrayEntity(requestEntity.getBytes()));
		HttpEntity entity = null;
		try {
			logger.debug(
					"Sent service bind to [bind@subscription.dev]. [{}]",
					data.obj.getApplicationId()
							+ ":"
							+ (data.obj.getClientId() == null ? "" : data.obj
									.getClientId()) + ":"
							+ tunnelContext.getDeviceId());
			HttpResponse httpResponse = getHttpClient().execute(
					httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();
			logger.debug("[{}]Bound response: {}, time:{}ms",
					new Object[] { tunnelContext.getChannel().getId(), status,
							(System.currentTimeMillis() - timestamp) });
			entity = httpResponse.getEntity();
			if (HttpStatus.SC_OK == status) {
				doOk(tunnelContext, data, entity);
			} else {
				logger.error(
						"Unexpectant response status :{} while binding on {}.",
						status, "subscription.dev");
				sendError(tunnelContext, data, new Exception(
						"Unexpectant response status :" + status
								+ " while binding on subscription.dev"));
			}
		} catch (IOException e) {
			logger.error("Error occured while request [{}],{}",
					"subscription.dev",
					e.getMessage());
			sendError(tunnelContext, data, e);
		} finally {
			if (entity != null) {
				try {
					EntityUtils.consume(entity);
				} catch (IOException ignore) {
				}
			}
		}
	}

	private void doOk(final TunnelContext tunnelContext,
			final TunnelData<ServiceBind> data,
			HttpEntity entity) throws IOException {
		JSONObject obj = JSONObject.fromObject(EntityUtils
				.toString(entity));
		ServiceBind serviceBind = ServiceBind.newBuilder()
				.setApplicationId(obj.getString("aid"))
				.setClientId(obj.getString("cid"))
				.build();
		BroadcastService.getDefaultInstance().register(data.obj.getApplicationId(), ALL_BROADCAST_TAG,tunnelContext.getDeviceId() , serviceBind.getClientId());
		tunnelContext.getChannel()
				.write(
						new TunnelData<MessageLite>(data.tag, 137,
								serviceBind));
		logger.debug(
				"[{}] Service bind success,sent to client. [{}:{}:{}]",
				new Object[] { tunnelContext.getChannel().getId(),
						serviceBind.getApplicationId(),
						serviceBind.getClientId(), tunnelContext.getDeviceId() }
				);
	}

	private void sendError(final TunnelContext tunnelContext,
			final TunnelData<ServiceBind> data, Throwable t) {
		logger.error(
				"[{}] Service bind exception, sent error to client. {}",
				tunnelContext.getChannel().getId(),
				t.toString());
		tunnelContext.getChannel()
				.write(new TunnelData<MessageLite>(data.tag, 135,
						Response
								.newBuilder().setStatus("ERROR")
								.build()));
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

	private void setup() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
					new Scheme("http", 80, PlainSocketFactory
							.getSocketFactory()));
	}
}