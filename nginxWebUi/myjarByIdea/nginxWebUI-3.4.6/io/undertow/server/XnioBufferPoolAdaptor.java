package io.undertow.server;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import java.nio.ByteBuffer;
import org.xnio.Pool;
import org.xnio.Pooled;

public class XnioBufferPoolAdaptor implements Pool<ByteBuffer> {
   private final ByteBufferPool byteBufferPool;

   public XnioBufferPoolAdaptor(ByteBufferPool byteBufferPool) {
      this.byteBufferPool = byteBufferPool;
   }

   public Pooled<ByteBuffer> allocate() {
      final PooledByteBuffer buf = this.byteBufferPool.allocate();
      return new Pooled<ByteBuffer>() {
         public void discard() {
            buf.close();
         }

         public void free() {
            buf.close();
         }

         public ByteBuffer getResource() throws IllegalStateException {
            return buf.getBuffer();
         }

         public void close() {
            buf.close();
         }
      };
   }
}
