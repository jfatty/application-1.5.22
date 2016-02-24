package com.babeeta.butterfly.application.gateway.device.tunnel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.babeeta.butterfly.MessageRouting.Acknowledgement;
import com.babeeta.butterfly.MessageRouting.Credential;
import com.babeeta.butterfly.MessageRouting.DeviceRegister;
import com.babeeta.butterfly.MessageRouting.Heartbeat;
import com.babeeta.butterfly.MessageRouting.HeartbeatInit;
import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;
import com.babeeta.butterfly.MessageRouting.HeartbeatResponse;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.MessageRouting.ServiceBind;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

public class TunnelClient {
	public static boolean isUnbind = true;
	public static boolean isPusher = true;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out
					.println("[usage]java TunnelClient deviceGatewayHost isUnbind isPusher");
			return;
		}
		isUnbind = args[1].equals("true");
		isPusher = args[2].equals("true");

		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(args[0], 5757));

		final DataInputStream in = new DataInputStream(socket.getInputStream());
		final DataOutputStream out = new DataOutputStream(
				socket.getOutputStream());

		HeartbeatInit hbi = HeartbeatInit.newBuilder()
				.setCause("none.")
				.setLastException(HeartbeatException.none)
				.setLastTimeout(480)
				.build();

		final AtomicInteger counter = new AtomicInteger();

		// 发送心跳协商
		out.writeInt(counter.getAndIncrement());
		out.writeInt(0);
		out.writeInt(hbi.getSerializedSize());
		hbi.writeTo(out);
		out.flush();

		// 启动Datahandler， 处理服务器下发数据
		DataHandler handler = new DataHandler(socket, in, out, counter);
		Thread handlerThread = new Thread(handler, "Handler");
		handlerThread.start();

		// 发送设备注册
		DeviceRegister deviceRegister = DeviceRegister.newBuilder()
				.setImei("IMEI")
				.build();
		int tag = counter.getAndIncrement();

		handler.handlerMap.put(tag, new ResponseHandler() {

			@Override
			public void onResponse(int tag, MessageLite message)
					throws Exception {
				Credential credential = (Credential) message;
				System.out.println("Device id:" + credential.getId()
						+ ", Secure Key:" + credential.getSecureKey());
			}
		});

		synchronized (out) {
			out.writeInt(tag);
			out.writeInt(131);
			out.writeInt(deviceRegister.getSerializedSize());
			deviceRegister.writeTo(out);
			out.flush();
		}
	}
}

class DataHandler implements Runnable {
	final AtomicInteger count = new AtomicInteger(0);
	private static final String APPLICATION_ID = "8968c4fe5538405ba3bfa4b61484f0a0";
	private static final String SECURE_KEY = "a2d825d2a41a49819405bc4a2d85ebfc";

	// private static final String APPLICATION_ID =
	// "e28ccddf8b2048a0a06199e197e61efc";
	// private static final String SECURE_KEY =
	// "b13f2abd78d3443cb3ba3867549659ae";

	private final ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor();

	final ConcurrentMap<Integer, ResponseHandler> handlerMap = new ConcurrentHashMap<Integer, ResponseHandler>();

	private final DataInputStream in;
	private final DataOutputStream out;
	private boolean stop;
	private final AtomicInteger counter;
	private final Socket socket;
	Credential credential = null;
	ServiceBind serviceBind = null;

	public DataHandler(Socket socket, DataInputStream in, DataOutputStream out,
			AtomicInteger counter) {
		super();
		this.in = in;
		this.out = out;
		this.counter = counter;
		this.socket = socket;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				read();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void ack(String uid) throws IOException {
		Acknowledgement ac = Acknowledgement.newBuilder()
				.setUid(uid)
				.build();

		synchronized (out) {
			out.writeInt(counter.getAndIncrement());
			out.writeInt(130);
			out.writeInt(ac.getSerializedSize());
			ac.writeTo(out);
			out.flush();
		}

		if (TunnelClient.isUnbind) {
			unbind();
		}
	}

	private String encode(String string) throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64(string
				.getBytes("utf-8")));
	}

	private String getTimeStamp() {
		return SimpleDateFormat.getDateTimeInstance().format(new Date());
	}

	// 设备注册的回复
	private void onCredential(byte[] buf) throws IOException {
		this.credential = Credential.parseFrom(buf);
		System.out.println("New credential:" + credential.getId() + ":"
				+ credential.getSecureKey());
		int tag = counter.getAndIncrement();

		handlerMap.put(tag, new ResponseHandler() {

			@Override
			public void onResponse(int tag, MessageLite message)
					throws Exception {
				if (message instanceof Response) {
					Response response = (Response) message;
					System.out.println("Auth response:" + response.getStatus());
					// 发送服务绑定
					serviceBind();
				} else {
					System.out.println("Auth failed: Unkown response");
				}
			}

		});

		synchronized (out) {
			out.writeInt(tag);
			out.writeInt(132);
			out.writeInt(credential.getSerializedSize());
			credential.writeTo(out);
			out.flush();
		}
	}

	// 解析心跳回复并设定下一次心跳的时间
	private void onHeartbeatResponse(byte[] buf)
			throws InvalidProtocolBufferException {
		HeartbeatResponse response = HeartbeatResponse
				.parseFrom(buf);
		System.out.println("[" + getTimeStamp() + "]T:" + response.getDelay());

		scheduledExecutorService.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					synchronized (out) {
						Heartbeat heartbeat = Heartbeat.newBuilder()
								.setLastDelay(120).build();
						out.writeInt(counter.getAndIncrement());
						out.writeInt(1);
						out.writeInt(heartbeat.getSerializedSize());
						heartbeat.writeTo(out);
						out.flush();
					}
				} catch (IOException e) {
					System.out.println("Heartbeat error:" + e.toString());
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
					try {
						socket.connect(socket.getRemoteSocketAddress());
					} catch (IOException e1) {
						System.out
								.println("Error while reconnecting to remote server."
										+ e1.toString());
					}
				}
			}

		}, response.getDelay(), TimeUnit.SECONDS);

	}

	private void onMessage(byte[] buf) throws IOException {
		Message message = Message.parseFrom(buf);
		System.out.println("=====================================");
		System.out.println(message.toString());
		System.out.println(count.getAndIncrement());
		System.out.println("=====================================");

		ack(message.getUid());
	}

	private void onResponse(int tag, byte[] buf)
			throws Exception {
		ResponseHandler handler = handlerMap.remove(tag);
		if (handler == null) {
			System.out.println("Unmatched response.Tag:" + tag);
		} else {
			handler.onResponse(tag, Response.parseFrom(buf));
		}
	}

	private void onServiceBind(byte[] buf)
			throws Exception {
		this.serviceBind = ServiceBind.parseFrom(buf);
		if (this.serviceBind.getClientId() == null
				|| this.serviceBind.getClientId().trim().equals("")) {
			System.out.println("Unbind success.");
		} else {
			System.out.println("Service bind success:"
					+ serviceBind.getApplicationId() + " == "
					+ serviceBind.getClientId());
			if (TunnelClient.isPusher) {
				push();
			}
		}
	}

	private void push() throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://push-api-0.hudee.com/service/push/server/"
						+ this.serviceBind.getClientId());
		post.setHeader("Authorization", "BASIC " + encode(APPLICATION_ID + ":"
				+ SECURE_KEY));
		post.setEntity(new ByteArrayEntity("Hello, there!".getBytes("utf-8")));

		HttpResponse response = httpClient.execute(post);

		System.out.println("Push api response:" + response.getStatusLine());
		System.out.println("Content:"
				+ EntityUtils.toString(response.getEntity()));
	}

	private void read() throws Exception {
		int tag = in.readInt();
		int type = in.readInt();
		int len = in.readInt();
		System.out
				.println("tag:" + tag + ", type:" + type + ", len:" + len);
		byte[] buf = new byte[len];
		in.readFully(buf);
		switch (type) {
		case 2:
			onHeartbeatResponse(buf);
			break;
		case 129:
			onMessage(buf);
			break;
		case 136:
			onCredential(buf);
			break;
		case 137:
			onServiceBind(buf);
			break;
		case 135:
			onResponse(tag, buf);
			break;
		default:
			System.out.println("Unkown server feed.");

		}
	}

	// 发送服务绑定
	private void serviceBind() throws IOException {
		ServiceBind serviceBind = ServiceBind.newBuilder()
				.setApplicationId(APPLICATION_ID)
				.build();
		int tag = counter.getAndIncrement();
		synchronized (out) {
			out.writeInt(tag);
			out.writeInt(133);
			out.writeInt(serviceBind.getSerializedSize());
			serviceBind.writeTo(out);
			out.flush();
		}
	}

	private void unbind() throws IOException {
		ServiceBind serviceBind = ServiceBind.newBuilder()
				.setApplicationId(this.serviceBind.getApplicationId())
				.setClientId(this.serviceBind.getClientId())
				.build();

		synchronized (out) {
			out.writeInt(counter.getAndIncrement());
			out.writeInt(134);
			out.writeInt(serviceBind.getSerializedSize());
			serviceBind.writeTo(out);
			out.flush();
		}
	}
}

interface ResponseHandler {
	void onResponse(int tag, MessageLite message) throws Exception;
}