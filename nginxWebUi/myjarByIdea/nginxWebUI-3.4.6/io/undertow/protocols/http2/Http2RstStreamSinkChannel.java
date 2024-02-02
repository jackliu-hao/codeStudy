package io.undertow.protocols.http2;

import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.nio.ByteBuffer;

class Http2RstStreamSinkChannel extends Http2NoDataStreamSinkChannel {
   public static final int HEADER_FIRST_LINE = 1027;
   private final int streamId;
   private final int errorCode;

   protected Http2RstStreamSinkChannel(Http2Channel channel, int streamId, int errorCode) {
      super(channel);
      this.errorCode = errorCode;
      this.streamId = streamId;
   }

   protected SendFrameHeader createFrameHeader() {
      ByteBuffer buf = ByteBuffer.allocate(13);
      Http2ProtocolUtils.putInt(buf, 1027);
      buf.put((byte)0);
      Http2ProtocolUtils.putInt(buf, this.streamId);
      Http2ProtocolUtils.putInt(buf, this.errorCode);
      buf.flip();
      return new SendFrameHeader(new ImmediatePooledByteBuffer(buf));
   }
}
