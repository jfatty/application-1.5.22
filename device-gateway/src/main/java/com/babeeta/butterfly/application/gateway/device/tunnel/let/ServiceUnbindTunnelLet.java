package com.babeeta.butterfly.application.gateway.device.tunnel.let;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.LinkedBlockingQueue;
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

import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.MessageRouting.ServiceBind;
import com.babeeta.butterfly.application.gateway.device.ServerContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelContext;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelData;
import com.babeeta.butterfly.application.gateway.device.tunnel.TunnelLet;
import com.google.protobuf.MessageLite;

public class ServiceUnbindTunnelLet implements TunnelLet<ServiceBind> {
	private static final Logger logger = LoggerFactory
			.getLogger(ServiceUnbindTunnelLet.class);

	@SuppressWarnings("unused")
	private final ServerContext serverContext;

	public static AtomicLong SERVICE_UNBIND_COUNT = new AtomicLong(0);

	private HttpClient httpClient;

	private ThreadSafeClientConnManager httpClientConnectionManager;

	private final int MAX_CONNECTION = 256;

	public final ThreadPoolExecutor executor = new ThreadPoolExecutor(64,
			MAX_CONNECTION, 10,
			TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000));

	private final UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("Uncaught exception: ", e);

		}
	};

	public ServiceUnbindTunnelLet(ServerContext serverContext) {
		super();
		this.serverContext = serverContext;
		setup();
	}

	@Override
	public void messageReceived(final TunnelContext tunnelContext,
			final TunnelData<ServiceBind> data) {
		SERVICE_UNBIND_COUNT.getAndIncrement();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("unbind");
				Thread.currentThread().setUncaughtExceptionHandler(
						uncaughtExceptionHandler);
				unbind(tunnelContext, data);
			}

		});
	}

	private void doOk(final TunnelContext tunnelContext,
			final TunnelData<ServiceBind> data,
			HttpEntity entity) throws IOException {
		JSONObject obj = JSONObject.fromObject(EntityUtils
				.toString(entity));
		logger.debug("[{}] [{}]", tunnelContext.getChannel().getId(),
				obj.toString());
		if ("OK".equalsIgnoreCase(obj.getString("status"))) {
			ServiceBind serviceBind = ServiceBind.newBuilder()
					.setApplicationId(data.obj.getApplicationId())
					.build();
			tunnelContext.getChannel().write(
					new TunnelData<MessageLite>(data.tag, 137,
							serviceBind));
			logger.debug(
					"[{}] Service unbind success,sent to client. [{}:{}:{}]",
					new Object[] { tunnelContext.getChannel().getId(),
							serviceBind.getApplicationId(),
							serviceBind.getClientId(),
							tunnelContext.getDeviceId() }
					);
		} else {
			tunnelContext.getChannel()
					.write(new TunnelData<MessageLite>(data.tag, 135,
							Response
									.newBuilder().setStatus("ERROR")
									.build()));
		}
	}

	private void sendError(final TunnelContext tunnelContext,
			final TunnelData<ServiceBind> data, Throwable t) {
		logger.error(
				"[{}] Service unbind exception, sent error to client. {}",
				tunnelContext.getChannel().getId(),
				t.toString());
		tunnelContext.getChannel()
				.write(new TunnelData<MessageLite>(data.tag, 135,
						Response
								.newBuilder().setStatus("ERROR")
								.build()));
	}

	private void setup() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
					new Scheme("http", 80, PlainSocketFactory
							.getSocketFactory()));

		httpClientConnectionManager = new ThreadSafeClientConnManager(
				schemeRegistry);

		httpClientConnectionManager.setMaxTotal(MAX_CONNECTION);
		httpClientConnectionManager.setDefaultMaxPerRoute(MAX_CONNECTION);
		httpClient = new DefaultHttpClient(httpClientConnectionManager);
	}

	private void unbind(final TunnelContext tunnelContext,
			final TunnelData<ServiceBind> data) {
		long timestamp = System.currentTimeMillis();
		logger.debug("service unbind aid={} cid={},did={}",new Object[]{data.obj.getApplicationId()==null?"":data.obj.getApplicationId(),data.obj.getClientId()==null?"":data.obj.getClientId(),tunnelContext.getDeviceId()==null?"":tunnelContext.getDeviceId()});
		HttpPost httpPost = new HttpPost("http://subscription.dev/api/unbind");
		httpPost.setHeader(new BasicHeader("Content-type",
				"application/json"));
		String requestEntity = "{\"aid\":\"" + data.obj.getApplicationId()
				+ "\",\"cid\":\"" + data.obj.getClientId() + "\"}";
		httpPost.setEntity(new ByteArrayEntity(requestEntity.getBytes()));
		HttpEntity entity = null;
		try {
			logger.debug(
					"[{}] Sent service unbind to [unbind@subscription.dev]. [{}] =/= [{}]",
					new Object[] {
							tunnelContext.getChannel().getId(),
							data.obj.getApplicationId(),
							data.obj.getClientId() });
			HttpResponse httpResponse = httpClient.execute(
					httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();
			logger.debug("[{}]Unbound response: {}, time:{}ms",
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
}
