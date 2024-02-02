package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.nio.ByteBuffer;

class Http2PingStreamSinkChannel extends Http2NoDataStreamSinkChannel {
   public static final int HEADER = 2054;
   private final byte[] data;
   private final boolean ack;

   protected Http2PingStreamSinkChannel(Http2Channel channel, byte[] data, boolean ack) {
      super(channel);
      if (data.length != 8) {
         throw new IllegalArgumentException(UndertowMessages.MESSAGES.httpPingDataMustBeLength8());
      } else {
         this.data = data;
         this.ack = ack;
      }
   }

   protected SendFrameHeader createFrameHeader() {
      ByteBuffer buf = ByteBuffer.allocate(17);
      Http2ProtocolUtils.putInt(buf, 2054);
      buf.put((byte)(this.ack ? 1 : 0));
      Http2ProtocolUtils.putInt(buf, 0);

      for(int i = 0; i < 8; ++i) {
         buf.put(this.data[i]);
      }

      buf.flip();
      return new SendFrameHeader(new ImmediatePooledByteBuffer(buf));
   }
}
