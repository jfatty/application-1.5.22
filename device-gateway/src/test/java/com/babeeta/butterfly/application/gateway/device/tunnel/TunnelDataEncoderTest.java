package com.babeeta.butterfly.application.gateway.device.tunnel;

import static org.junit.Assert.*;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.junit.Before;
import org.junit.Test;

import com.babeeta.butterfly.MessageRouting.Message;
import com.google.protobuf.ByteString;

public class TunnelDataEncoderTest {

	private TunnelDataEncoder encoder = null;

	@Before
	public void setUp() throws Exception {
		encoder = new TunnelDataEncoder();
	}

	@Test
	public void testEncodeChannelHandlerContextChannelObject() throws Exception {
		Message msg = Message.newBuilder()
				.setTo("TO")
				.setDate(System.currentTimeMillis())
				.setFrom("FROM")
				.setUid("UID")
				.setContent(ByteString.copyFromUtf8("Hello"))
				.build();
		TunnelData<Message> td = new TunnelData<Message>(7, 129, msg);
		Object obj = encoder.encode(null, null, td);

		assertNotNull(obj);
		assertTrue(obj instanceof ChannelBuffer);
		ChannelBuffer buf = (ChannelBuffer) obj;

		assertEquals(7, buf.readInt());
		assertEquals(129, buf.readInt());
		assertEquals(msg.getSerializedSize(), buf.readInt());

		Message e = Message.parseFrom(new ChannelBufferInputStream(buf));

		assertNotNull(e);
		assertEquals(msg.getDate(), e.getDate());
	}

}
