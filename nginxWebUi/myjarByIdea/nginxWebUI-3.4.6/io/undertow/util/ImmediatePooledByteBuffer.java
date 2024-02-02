package io.undertow.util;

import io.undertow.connector.PooledByteBuffer;
import java.nio.ByteBuffer;

public class ImmediatePooledByteBuffer implements PooledByteBuffer {
   private ByteBuffer buffer;

   public ImmediatePooledByteBuffer(ByteBuffer buffer) {
      this.buffer = buffer;
   }

   public ByteBuffer getBuffer() {
      return this.buffer;
   }

   public void close() {
   }

   public boolean isOpen() {
      return true;
   }
}
