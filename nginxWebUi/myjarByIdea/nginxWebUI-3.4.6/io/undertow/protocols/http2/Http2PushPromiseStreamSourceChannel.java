package io.undertow.protocols.http2;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.util.HeaderMap;

public class Http2PushPromiseStreamSourceChannel extends AbstractHttp2StreamSourceChannel {
   private final HeaderMap headers;
   private final int pushedStreamId;
   private final int associatedStreamId;

   Http2PushPromiseStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, HeaderMap headers, int pushedStreamId, int associatedStreamId) {
      super(framedChannel, data, frameDataRemaining);
      this.headers = headers;
      this.pushedStreamId = pushedStreamId;
      this.associatedStreamId = associatedStreamId;
      this.lastFrame();
   }

   public HeaderMap getHeaders() {
      return this.headers;
   }

   public int getPushedStreamId() {
      return this.pushedStreamId;
   }

   public int getAssociatedStreamId() {
      return this.associatedStreamId;
   }
}
