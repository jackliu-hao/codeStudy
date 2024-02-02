package io.undertow.protocols.http2;

import java.nio.ByteBuffer;
import org.xnio.Bits;

class Http2PushPromiseParser extends Http2HeaderBlockParser {
   private int paddingLength = 0;
   private int promisedStreamId;
   private static final int STREAM_MASK = -129;

   Http2PushPromiseParser(int frameLength, HpackDecoder hpackDecoder, boolean client, int maxHeaders, int streamId, int maxHeaderListSize) {
      super(frameLength, hpackDecoder, client, maxHeaders, streamId, maxHeaderListSize);
   }

   protected boolean handleBeforeHeader(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
      boolean hasPadding = Bits.anyAreSet(headerParser.flags, 8);
      int reqLength = (hasPadding ? 1 : 0) + 4;
      if (resource.remaining() < reqLength) {
         return false;
      } else {
         if (hasPadding) {
            this.paddingLength = resource.get() & 255;
         }

         this.promisedStreamId = (resource.get() & -129) << 24;
         this.promisedStreamId += (resource.get() & 255) << 16;
         this.promisedStreamId += (resource.get() & 255) << 8;
         this.promisedStreamId += resource.get() & 255;
         return true;
      }
   }

   protected int getPaddingLength() {
      return this.paddingLength;
   }

   public int getPromisedStreamId() {
      return this.promisedStreamId;
   }
}
