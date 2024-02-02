package io.undertow.util;

import io.undertow.connector.PooledByteBuffer;
import java.nio.ByteBuffer;
import org.xnio.Pooled;

public class PooledAdaptor implements Pooled<ByteBuffer> {
   private final PooledByteBuffer buffer;

   public PooledAdaptor(PooledByteBuffer buffer) {
      this.buffer = buffer;
   }

   public void discard() {
      this.buffer.close();
   }

   public void free() {
      this.buffer.close();
   }

   public ByteBuffer getResource() throws IllegalStateException {
      return this.buffer.getBuffer();
   }

   public void close() {
      this.buffer.close();
   }

   public String toString() {
      return "PooledAdaptor(" + this.buffer + ")";
   }
}
