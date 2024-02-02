package io.undertow.server.protocol.framed;

import io.undertow.connector.PooledByteBuffer;
import java.nio.ByteBuffer;

public class SendFrameHeader {
   private final int reminingInBuffer;
   private final PooledByteBuffer byteBuffer;
   private final boolean anotherFrameRequired;
   private final ByteBuffer trailer;

   public SendFrameHeader(int reminingInBuffer, PooledByteBuffer byteBuffer, boolean anotherFrameRequired) {
      this(reminingInBuffer, byteBuffer, anotherFrameRequired, (ByteBuffer)null);
   }

   public SendFrameHeader(int reminingInBuffer, PooledByteBuffer byteBuffer, boolean anotherFrameRequired, ByteBuffer trailer) {
      this.byteBuffer = byteBuffer;
      this.reminingInBuffer = reminingInBuffer;
      this.anotherFrameRequired = anotherFrameRequired;
      this.trailer = trailer;
   }

   public SendFrameHeader(int reminingInBuffer, PooledByteBuffer byteBuffer) {
      this.byteBuffer = byteBuffer;
      this.reminingInBuffer = reminingInBuffer;
      this.anotherFrameRequired = false;
      this.trailer = null;
   }

   public SendFrameHeader(PooledByteBuffer byteBuffer) {
      this.byteBuffer = byteBuffer;
      this.reminingInBuffer = 0;
      this.anotherFrameRequired = false;
      this.trailer = null;
   }

   public PooledByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public ByteBuffer getTrailer() {
      return this.trailer;
   }

   public int getRemainingInBuffer() {
      return this.reminingInBuffer;
   }

   public boolean isAnotherFrameRequired() {
      return this.anotherFrameRequired;
   }
}
