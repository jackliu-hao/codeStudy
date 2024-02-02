package io.undertow.protocols.http2;

import java.nio.ByteBuffer;

public class Http2GoAwayParser extends Http2PushBackParser {
   private int statusCode;
   private int lastGoodStreamId;

   public Http2GoAwayParser(int frameLength) {
      super(frameLength);
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
      if (resource.remaining() >= 8) {
         this.lastGoodStreamId = Http2ProtocolUtils.readInt(resource);
         this.statusCode = Http2ProtocolUtils.readInt(resource);
      }
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public int getLastGoodStreamId() {
      return this.lastGoodStreamId;
   }
}
