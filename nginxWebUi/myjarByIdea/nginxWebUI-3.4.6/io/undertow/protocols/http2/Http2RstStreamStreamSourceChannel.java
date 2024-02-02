package io.undertow.protocols.http2;

import io.undertow.connector.PooledByteBuffer;

public class Http2RstStreamStreamSourceChannel extends AbstractHttp2StreamSourceChannel {
   private final int errorCode;
   private final int streamId;

   Http2RstStreamStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, int errorCode, int streamId) {
      super(framedChannel, data, 0L);
      this.errorCode = errorCode;
      this.streamId = streamId;
      this.lastFrame();
   }

   public int getErrorCode() {
      return this.errorCode;
   }

   public int getStreamId() {
      return this.streamId;
   }
}
