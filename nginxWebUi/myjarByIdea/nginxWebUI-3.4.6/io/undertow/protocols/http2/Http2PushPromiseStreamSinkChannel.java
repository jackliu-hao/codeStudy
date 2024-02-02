package io.undertow.protocols.http2;

import io.undertow.util.HeaderMap;
import java.nio.ByteBuffer;

public class Http2PushPromiseStreamSinkChannel extends Http2DataStreamSinkChannel {
   private final int pushedStreamId;

   Http2PushPromiseStreamSinkChannel(Http2Channel channel, HeaderMap requestHeaders, int associatedStreamId, int pushedStreamId) {
      super(channel, associatedStreamId, requestHeaders, 5);
      this.pushedStreamId = pushedStreamId;
   }

   protected void writeBeforeHeaderBlock(ByteBuffer buffer) {
      buffer.put((byte)(this.pushedStreamId >> 24 & 255));
      buffer.put((byte)(this.pushedStreamId >> 16 & 255));
      buffer.put((byte)(this.pushedStreamId >> 8 & 255));
      buffer.put((byte)(this.pushedStreamId & 255));
   }

   protected int grabFlowControlBytes(int bytes) {
      return bytes;
   }

   public int getPushedStreamId() {
      return this.pushedStreamId;
   }
}
