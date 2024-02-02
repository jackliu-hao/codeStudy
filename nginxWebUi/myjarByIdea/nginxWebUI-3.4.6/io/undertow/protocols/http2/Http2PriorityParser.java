package io.undertow.protocols.http2;

import java.nio.ByteBuffer;
import org.xnio.Bits;

class Http2PriorityParser extends Http2PushBackParser {
   private int streamDependency;
   private int weight;
   private boolean exclusive;

   Http2PriorityParser(int frameLength) {
      super(frameLength);
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser frameHeaderParser) {
      if (resource.remaining() >= 5) {
         int read = Http2ProtocolUtils.readInt(resource);
         if (Bits.anyAreSet(read, Integer.MIN_VALUE)) {
            this.exclusive = true;
            this.streamDependency = read & Integer.MAX_VALUE;
         } else {
            this.exclusive = false;
            this.streamDependency = read;
         }

         this.weight = resource.get();
      }
   }

   public int getWeight() {
      return this.weight;
   }

   public int getStreamDependency() {
      return this.streamDependency;
   }

   public boolean isExclusive() {
      return this.exclusive;
   }
}
