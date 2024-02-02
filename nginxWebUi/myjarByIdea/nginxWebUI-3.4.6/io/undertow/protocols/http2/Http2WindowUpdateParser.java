package io.undertow.protocols.http2;

import java.nio.ByteBuffer;

class Http2WindowUpdateParser extends Http2PushBackParser {
   private int deltaWindowSize;

   Http2WindowUpdateParser(int frameLength) {
      super(frameLength);
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser frameHeaderParser) {
      if (resource.remaining() >= 4) {
         this.deltaWindowSize = Http2ProtocolUtils.readInt(resource);
      }
   }

   public int getDeltaWindowSize() {
      return this.deltaWindowSize;
   }
}
