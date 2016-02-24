package com.babeeta.butterfly.monitor.gateway.device.pipeline;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.babeeta.butterfly.MessageRouting.Acknowledgement;
import com.babeeta.butterfly.MessageRouting.Credential;
import com.babeeta.butterfly.MessageRouting.HeartbeatResponse;
import com.babeeta.butterfly.MessageRouting.Message;
import com.babeeta.butterfly.MessageRouting.Response;
import com.babeeta.butterfly.MessageRouting.ServiceBind;
import com.babeeta.butterfly.monitor.gateway.device.TunnelData;
import com.google.protobuf.MessageLite;

public class TunnelDataDecoder extends OneToOneDecoder {

	private static final int HEADER_SIZE = 4 * 3;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof ChannelBuffer)) {
			return msg;
		}

		ChannelBuffer buf = (ChannelBuffer) msg;
		int tag = buf.readInt();
		int cmd = buf.readInt();
		int len = buf.readInt();

		if (len < 0) {
			throw new CorruptedFrameException("Negative length: " + len);
		}

		MessageLite prototype = getProtoType(cmd, len, buf);

		if (len > 0) {
			return new TunnelData<MessageLite>(tag, cmd, decode(prototype, len,
					buf));
		} else {
			return new TunnelData<MessageLite>(tag, cmd, prototype);
		}
	}

	private MessageLite decode(MessageLite prototype, int len, ChannelBuffer buf)
			throws IOException {
		// Copy from org.jboss.netty.handler.codec.protobuf.ProtobufDecoder
		if (buf.hasArray()) {
			return prototype
					.newBuilderForType()
					.mergeFrom(
							buf.array(), buf.arrayOffset() + HEADER_SIZE,
							buf.readableBytes())
					.build();
		} else {
			return prototype.newBuilderForType().mergeFrom(
					new ChannelBufferInputStream(buf)).build();
		}
	}

	private MessageLite getProtoType(int clazz, int len, ChannelBuffer buf) {
		switch (clazz) {
		case 2:
			// Hearbeat
			return HeartbeatResponse.getDefaultInstance();
		case 135:
			// response
			return Response.getDefaultInstance();
		case 129:
			// Message
			return Message.getDefaultInstance();
		case 130:
			// Acknowlegement
			return Acknowledgement.getDefaultInstance();
		case 136:
			// Credential
			return Credential.getDefaultInstance();
		case 137:
			// Service Bind
			return ServiceBind.getDefaultInstance();
		default:
			return null;
		}
	}
}