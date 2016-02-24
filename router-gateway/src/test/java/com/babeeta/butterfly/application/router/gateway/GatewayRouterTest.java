package com.babeeta.butterfly.application.router.gateway;

import com.babeeta.butterfly.MessageFuture;
import com.babeeta.butterfly.MessageFutureListener;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageSender;
import com.babeeta.butterfly.application.router.gateway.balance.ServiceLocator;
import com.google.protobuf.ByteString;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class GatewayRouterTest {

	private GatewayRouter gatewayRouter = null;
	private IMocksControl mocksControl = null;
	private ServiceLocator serverLocator = null;
	private MessageSender messageSender = null;
	private MessageFuture messageFuture = null;

	@Before
	public void setUp() throws Exception {
		mocksControl = EasyMock.createControl();
		serverLocator = mocksControl.createMock(ServiceLocator.class);
		messageSender = mocksControl.createMock(MessageSender.class);
		messageFuture = mocksControl.createMock(MessageFuture.class);
		gatewayRouter = new GatewayRouter(messageSender, serverLocator);
	}

	@Test
	public void testTransform如果收件人为update则按Content的内容做Hash()
			throws UnsupportedEncodingException {
		Message msg = buildMessage("update@gateway.dev");

		Capture<Message> messageCapture = setupMessageSender();
		mocksControl.replay();

		gatewayRouter.onMessage(msg);
		Assert.assertTrue(messageCapture.hasCaptured());
		Assert.assertEquals("update@router-1.gateway.dev", messageCapture.getValue()
                .getTo());
	}

	@Test
	public void testTransform收件人为空会被抛弃() throws UnsupportedEncodingException {
		Message msg = buildMessage("");

		mocksControl.replay();

		gatewayRouter.onMessage(msg);

	}

	@Test
	public void testTransform收件人格式不正确会被抛弃() throws UnsupportedEncodingException {
		Message msg = buildMessage("6a8s74fas@asdfasd.com");

		mocksControl.replay();

		gatewayRouter.onMessage(msg);
	}

	@Test
	public void testTransform正常情况() throws UnsupportedEncodingException {
		Message msg = buildMessage("did.cid.aid@gateway.dev");

		Capture<Message> messageCapture = setupMessageSender();
		mocksControl.replay();

		gatewayRouter.onMessage(msg);

		Assert.assertTrue(messageCapture.hasCaptured());
		Assert.assertEquals("did.cid.aid@router-1.gateway.dev", messageCapture
                .getValue().getTo());
	}

	private Message buildMessage(String to) throws UnsupportedEncodingException {
		Message msg = Message.newBuilder()
				.setContent(ByteString.copyFrom("did", "utf-8"))
				.setDate(System.currentTimeMillis())
				.setFrom("FROMX")
				.setTo(to)
				.setUid("UID")
				.build();
		return msg;
	}

	private Capture<Message> setupMessageSender() {
		EasyMock.expect(serverLocator.getDomain("did"))
				.andReturn("router-1.gateway.dev")
				.once();

		Capture<Message> messageCapture = new Capture<Message>();
		EasyMock.expect(messageSender.send(EasyMock.capture(messageCapture)))
				.andReturn(messageFuture)
				.once();
		EasyMock.expect(messageFuture.addListener(EasyMock.isA(MessageFutureListener.class)))
				.andReturn(messageFuture)
				.once();
		return messageCapture;
	}
}
