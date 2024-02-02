package io.undertow.protocols.http2;

import java.nio.ByteBuffer;

class Http2DiscardParser extends Http2PushBackParser {
   int remaining;

   Http2DiscardParser(int frameLength) {
      super(frameLength);
      this.remaining = frameLength;
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser frameHeaderParser) {
      int toUse = Math.min(resource.remaining(), this.remaining);
      this.remaining -= toUse;
      resource.position(resource.position() + toUse);
   }
}
