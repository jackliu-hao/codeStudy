package io.undertow.protocols.http2;

import java.nio.ByteBuffer;

class Http2RstStreamParser extends Http2PushBackParser {
   private int errorCode;

   Http2RstStreamParser(int frameLength) {
      super(frameLength);
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
      if (resource.remaining() >= 4) {
         this.errorCode = Http2ProtocolUtils.readInt(resource);
      }
   }

   public int getErrorCode() {
      return this.errorCode;
   }
}
