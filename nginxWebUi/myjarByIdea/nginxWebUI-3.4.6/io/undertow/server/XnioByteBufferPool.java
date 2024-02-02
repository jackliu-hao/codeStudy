package io.undertow.server;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import java.nio.ByteBuffer;
import org.xnio.Pool;
import org.xnio.Pooled;

public class XnioByteBufferPool implements ByteBufferPool {
   private final Pool<ByteBuffer> pool;
   private final ByteBufferPool arrayBackedPool;
   private final int bufferSize;
   private final boolean direct;

   public XnioByteBufferPool(Pool<ByteBuffer> pool) {
      this.pool = pool;
      Pooled<ByteBuffer> buf = pool.allocate();
      this.bufferSize = ((ByteBuffer)buf.getResource()).remaining();
      this.direct = !((ByteBuffer)buf.getResource()).hasArray();
      buf.free();
      if (this.direct) {
         this.arrayBackedPool = new DefaultByteBufferPool(false, this.bufferSize);
      } else {
         this.arrayBackedPool = this;
      }

   }

   public PooledByteBuffer allocate() {
      final Pooled<ByteBuffer> buf = this.pool.allocate();
      return new PooledByteBuffer() {
         private boolean open = true;

         public ByteBuffer getBuffer() {
            return (ByteBuffer)buf.getResource();
         }

         public void close() {
            this.open = false;
            buf.free();
         }

         public boolean isOpen() {
            return this.open;
         }
      };
   }

   public ByteBufferPool getArrayBackedPool() {
      return this.arrayBackedPool;
   }

   public void close() {
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public boolean isDirect() {
      return this.direct;
   }
}
