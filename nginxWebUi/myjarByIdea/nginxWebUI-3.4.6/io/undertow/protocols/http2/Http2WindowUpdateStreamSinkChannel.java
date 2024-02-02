package io.undertow.protocols.http2;

import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.nio.ByteBuffer;

class Http2WindowUpdateStreamSinkChannel extends Http2NoDataStreamSinkChannel {
   public static final int HEADER_FIRST_LINE = 1032;
   private final int streamId;
   private final int deltaWindowSize;

   protected Http2WindowUpdateStreamSinkChannel(Http2Channel channel, int streamId, int deltaWindowSize) {
      super(channel);
      this.streamId = streamId;
      this.deltaWindowSize = deltaWindowSize;
   }

   protected SendFrameHeader createFrameHeader() {
      ByteBuffer buf = ByteBuffer.allocate(13);
      Http2ProtocolUtils.putInt(buf, 1032);
      buf.put((byte)0);
      Http2ProtocolUtils.putInt(buf, this.streamId);
      Http2ProtocolUtils.putInt(buf, this.deltaWindowSize);
      buf.flip();
      return new SendFrameHeader(new ImmediatePooledByteBuffer(buf));
   }
}
