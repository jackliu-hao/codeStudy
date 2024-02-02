package io.undertow.protocols.http2;

import io.undertow.connector.PooledByteBuffer;

public class Http2GoAwayStreamSourceChannel extends AbstractHttp2StreamSourceChannel {
   private final int status;
   private final int lastGoodStreamId;

   Http2GoAwayStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, int status, int lastGoodStreamId) {
      super(framedChannel, data, frameDataRemaining);
      this.status = status;
      this.lastGoodStreamId = lastGoodStreamId;
      this.lastFrame();
   }

   public int getStatus() {
      return this.status;
   }

   public int getLastGoodStreamId() {
      return this.lastGoodStreamId;
   }
}
