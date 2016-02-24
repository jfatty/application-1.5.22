package com.babeeta.butterfly.application.gateway.device.tunnel;

import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.babeeta.butterfly.MessageRouting.Heartbeat;
import com.babeeta.butterfly.MessageRouting.HeartbeatInit;
import com.babeeta.butterfly.MessageRouting.HeartbeatInit.HeartbeatException;
import com.google.protobuf.MessageLite;

public class TunnelDataDecoderTest {

	TunnelDataDecoder decoder = null;
	IMocksControl mocksControl = null;

	@Before
	public void setUp() throws Exception {
		decoder = new TunnelDataDecoder();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDecode使用Heartbeat() throws Exception {
		ChannelBuffer buf = buidData(Heartbeat.getDefaultInstance(), 1);

		Object result = decoder.decode(null, null,
				buf);

		assertNotNull(result);
		assertTrue(result instanceof TunnelData);
		assertEquals(7, ((TunnelData<?>) result).tag);
		assertEquals(1, ((TunnelData<?>) result).cmd);
		assertNotNull(((TunnelData<?>) result).obj);
		assertTrue(((TunnelData<?>) result).obj instanceof Heartbeat);
	}

	@Test
	public void testDecode使用HeartbeatInit() throws Exception {
		HeartbeatInit heartbeatInit = HeartbeatInit.newBuilder()
				.setCause("CAUES")
				.setLastTimeout(120)
				.setLastException(HeartbeatException.choke)
				.build();
		ChannelBuffer buf = buidData(heartbeatInit, 0);

		Object result = decoder.decode(null, null, buf);

		assertNotNull(result);
		assertTrue(result instanceof TunnelData);
		assertEquals(7, ((TunnelData<?>) result).tag);
		assertEquals(0, ((TunnelData<?>) result).cmd);
		HeartbeatInit hbi = (HeartbeatInit) ((TunnelData<?>) result).obj;

		assertEquals("CAUES", hbi.getCause());
		assertEquals(120, hbi.getLastTimeout());
		assertEquals(HeartbeatException.choke, hbi.getLastException());
	}

	private ChannelBuffer buidData(MessageLite messageLite, int typeCode) {
		byte[] data = messageLite.toByteArray();
		ChannelBuffer buf = ChannelBuffers.buffer(4 * 3 + data.length);

		buf.writeInt(7);
		buf.writeInt(typeCode);
		buf.writeInt(data.length);
		buf.writeBytes(data);
		return buf;
	}
}
