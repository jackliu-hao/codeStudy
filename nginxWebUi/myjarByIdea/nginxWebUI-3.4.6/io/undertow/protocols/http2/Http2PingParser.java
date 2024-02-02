package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import java.io.IOException;
import java.nio.ByteBuffer;

class Http2PingParser extends Http2PushBackParser {
   final byte[] data = new byte[8];

   Http2PingParser(int frameLength) {
      super(frameLength);
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser parser) throws IOException {
      if (parser.length != 8) {
         throw new IOException(UndertowMessages.MESSAGES.httpPingDataMustBeLength8());
      } else if (parser.streamId != 0) {
         throw new IOException(UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(6));
      } else if (resource.remaining() >= 8) {
         resource.get(this.data);
      }
   }

   byte[] getData() {
      return this.data;
   }
}
