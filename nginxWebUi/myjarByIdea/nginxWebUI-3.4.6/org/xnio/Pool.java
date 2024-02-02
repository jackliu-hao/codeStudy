package org.xnio;

import java.nio.ByteBuffer;

/** @deprecated */
public interface Pool<T> {
   Pool<ByteBuffer> HEAP = new Pool<ByteBuffer>() {
      public Pooled<ByteBuffer> allocate() {
         return Buffers.globalPooledWrapper(ByteBufferPool.MEDIUM_HEAP.allocate());
      }
   };
   Pool<ByteBuffer> DIRECT = new Pool<ByteBuffer>() {
      public Pooled<ByteBuffer> allocate() {
         return Buffers.globalPooledWrapper(ByteBufferPool.MEDIUM_DIRECT.allocate());
      }
   };

   Pooled<T> allocate();
}
