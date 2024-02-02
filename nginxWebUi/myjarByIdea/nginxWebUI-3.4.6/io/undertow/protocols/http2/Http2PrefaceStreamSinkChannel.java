package io.undertow.protocols.http2;

import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.nio.ByteBuffer;

class Http2PrefaceStreamSinkChannel extends Http2StreamSinkChannel {
   Http2PrefaceStreamSinkChannel(Http2Channel channel) {
      super(channel, 0);
   }

   protected SendFrameHeader createFrameHeaderImpl() {
      return new SendFrameHeader(new ImmediatePooledByteBuffer(ByteBuffer.wrap(Http2Channel.PREFACE_BYTES)));
   }
}
