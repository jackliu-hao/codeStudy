package io.undertow.protocols.http2;

import java.nio.ByteBuffer;
import org.xnio.Bits;

class Http2HeadersParser extends Http2HeaderBlockParser {
   private static final int DEPENDENCY_MASK = -129;
   private int paddingLength = 0;
   private int dependentStreamId = 0;
   private int weight = 16;
   private boolean headersEndStream = false;
   private boolean exclusive;

   Http2HeadersParser(int frameLength, HpackDecoder hpackDecoder, boolean client, int maxHeaders, int streamId, int maxHeaderListSize) {
      super(frameLength, hpackDecoder, client, maxHeaders, streamId, maxHeaderListSize);
   }

   protected boolean handleBeforeHeader(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
      boolean hasPadding = Bits.anyAreSet(headerParser.flags, 8);
      boolean hasPriority = Bits.anyAreSet(headerParser.flags, 32);
      this.headersEndStream = Bits.allAreSet(headerParser.flags, 1);
      int reqLength = (hasPadding ? 1 : 0) + (hasPriority ? 5 : 0);
      if (reqLength == 0) {
         return true;
      } else if (resource.remaining() < reqLength) {
         return false;
      } else {
         if (hasPadding) {
            this.paddingLength = resource.get() & 255;
         }

         if (hasPriority) {
            if (resource.remaining() < 4) {
               return false;
            }

            byte b = resource.get();
            this.exclusive = (b & 128) != 0;
            this.dependentStreamId = (b & -129 & 255) << 24;
            this.dependentStreamId += (resource.get() & 255) << 16;
            this.dependentStreamId += (resource.get() & 255) << 8;
            this.dependentStreamId += resource.get() & 255;
            this.weight = resource.get() & 255;
         }

         return true;
      }
   }

   protected int getPaddingLength() {
      return this.paddingLength;
   }

   int getDependentStreamId() {
      return this.dependentStreamId;
   }

   int getWeight() {
      return this.weight;
   }

   boolean isHeadersEndStream() {
      return this.headersEndStream;
   }

   public boolean isExclusive() {
      return this.exclusive;
   }
}
