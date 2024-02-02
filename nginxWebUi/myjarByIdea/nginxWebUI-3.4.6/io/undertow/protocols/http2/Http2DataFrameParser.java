package io.undertow.protocols.http2;

import java.nio.ByteBuffer;
import org.xnio.Bits;

class Http2DataFrameParser extends Http2PushBackParser {
   private int padding = 0;

   Http2DataFrameParser(int frameLength) {
      super(frameLength);
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser headerParser) throws ConnectionErrorException {
      if (Bits.anyAreClear(headerParser.flags, 8)) {
         this.finish();
      } else if (headerParser.length == 0) {
         throw new ConnectionErrorException(1);
      } else {
         if (resource.remaining() > 0) {
            this.padding = resource.get() & 255;
            --headerParser.length;
            if (this.padding > headerParser.length) {
               throw new ConnectionErrorException(1);
            }

            this.finish();
         }

      }
   }

   int getPadding() {
      return this.padding;
   }
}
