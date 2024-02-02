package io.undertow.protocols.ajp;

import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.nio.ByteBuffer;

public class AjpClientCPingStreamSinkChannel extends AbstractAjpClientStreamSinkChannel {
   private static final byte[] CPING = new byte[]{18, 52, 0, 1, 10};

   protected AjpClientCPingStreamSinkChannel(AjpClientChannel channel) {
      super(channel);
   }

   protected final SendFrameHeader createFrameHeader() {
      return new SendFrameHeader(new ImmediatePooledByteBuffer(ByteBuffer.wrap(CPING)));
   }
}
