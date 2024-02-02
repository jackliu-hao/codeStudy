package io.undertow.protocols.http2;

import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.nio.ByteBuffer;

class Http2GoAwayStreamSinkChannel extends Http2NoDataStreamSinkChannel {
   public static final int HEADER_FIRST_LINE = 2055;
   private final int status;
   private final int lastGoodStreamId;

   protected Http2GoAwayStreamSinkChannel(Http2Channel channel, int status, int lastGoodStreamId) {
      super(channel);
      this.status = status;
      this.lastGoodStreamId = lastGoodStreamId;
   }

   protected SendFrameHeader createFrameHeader() {
      ByteBuffer buf = ByteBuffer.allocate(17);
      Http2ProtocolUtils.putInt(buf, 2055);
      buf.put((byte)0);
      Http2ProtocolUtils.putInt(buf, 0);
      Http2ProtocolUtils.putInt(buf, this.lastGoodStreamId);
      Http2ProtocolUtils.putInt(buf, this.status);
      buf.flip();
      return new SendFrameHeader(new ImmediatePooledByteBuffer(buf));
   }

   protected boolean isLastFrame() {
      return true;
   }
}
