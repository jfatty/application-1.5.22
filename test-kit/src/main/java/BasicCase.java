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
import com.babeeta.butterfly.application.TestEnviroment;
import com.babeeta.butterfly.application.third.Message.PushSingleMessageTestClient;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

public class BasicCase {
	public static boolean isUnbind = true;
	public static boolean isPusher = true;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("[usage]java TunnelClient  isUnbind isPusher");
			return;
		}
		isUnbind = args[0].equals("true");
		isPusher = args[1].equals("true");

		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(TestEnviroment.DEV_HOST, 5757));

		final DataInputStream in = new DataInputStream(socket.getInputStream());
		final DataOutputStream out = new DataOutputStream(
				socket.getOutputStream());

		HeartbeatInit hbi = HeartbeatInit.newBuilder().setCause("none.")
				.setLastException(HeartbeatException.none).setLastTimeout(480)
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
				.setImei("IMEI").build();
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

	private void ack(final String uid) throws IOException {
		Acknowledgement ac = Acknowledgement.newBuilder().setIsAppAck(false)
				.setUid(uid).setCid(serviceBind.getClientId()).build();

		synchronized (out) {
			out.writeInt(counter.getAndIncrement());
			out.writeInt(130);
			out.writeInt(ac.getSerializedSize());
			ac.writeTo(out);
			out.flush();
		}

		try {
			Thread.currentThread().sleep(1000 * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		appAck(uid);

	}

	private void appAck(String uid) throws IOException {

		Acknowledgement ac = Acknowledgement.newBuilder().setIsAppAck(true)
				.setUid(uid).build();

		synchronized (out) {
			out.writeInt(counter.getAndIncrement());
			out.writeInt(130);
			out.writeInt(ac.getSerializedSize());
			ac.writeTo(out);
			out.flush();
		}

		if (BasicCase.isUnbind) {
			unbind();
		}
	}

	private String encode(String string) throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64(string.getBytes("utf-8")));
	}

	private String getTimeStamp() {
		return SimpleDateFormat.getDateTimeInstance().format(new Date());
	}

	// 设备注册的回复
	private void onCredential(byte[] buf) throws IOException {
		credential = Credential.parseFrom(buf);
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
		HeartbeatResponse response = HeartbeatResponse.parseFrom(buf);
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
		System.out.println(message.getContent().toStringUtf8());
		System.out.println(count.getAndIncrement());
		System.out.println("=====================================");

		ack(message.getUid());
	}

	private void onResponse(int tag, byte[] buf) throws Exception {
		ResponseHandler handler = handlerMap.remove(tag);
		if (handler == null) {
			System.out.println("Unmatched response.Tag:" + tag);
		} else {
			handler.onResponse(tag, Response.parseFrom(buf));
		}
	}

	private void onServiceBind(byte[] buf) throws Exception {
		serviceBind = ServiceBind.parseFrom(buf);
		if (serviceBind.getClientId() == null
				|| serviceBind.getClientId().trim().equals("")) {
			System.out.println("Unbind success.");
		} else {
			System.out.println("Service bind success:"
					+ serviceBind.getApplicationId() + " == "
					+ serviceBind.getClientId());
			if (BasicCase.isPusher) {
				push(serviceBind.getClientId());
			}
		}
	}

	private void push(String clientId) throws Exception {
		PushSingleMessageTestClient pushSingleMessageTestClient = new PushSingleMessageTestClient(
				TestEnviroment.APP_ID, TestEnviroment.APP_KEY);
		String messageId = pushSingleMessageTestClient.push(clientId, "text");
		System.out.println("push success ,messageId>>>" + messageId);
	}

	private void read() throws Exception {
		int tag = in.readInt();
		int type = in.readInt();
		int len = in.readInt();
		System.out.println("tag:" + tag + ", type:" + type + ", len:" + len);
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
				.setApplicationId(TestEnviroment.APP_ID).build();
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
				.setClientId(this.serviceBind.getClientId()).build();

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