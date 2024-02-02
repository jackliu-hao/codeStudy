package org.xnio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public interface BufferAllocator<B extends Buffer> {
   BufferAllocator<ByteBuffer> BYTE_BUFFER_ALLOCATOR = new BufferAllocator<ByteBuffer>() {
      public ByteBuffer allocate(int size) {
         return ByteBuffer.allocate(size);
      }
   };
   BufferAllocator<ByteBuffer> DIRECT_BYTE_BUFFER_ALLOCATOR = new BufferAllocator<ByteBuffer>() {
      public ByteBuffer allocate(int size) {
         return ByteBuffer.allocateDirect(size);
      }
   };

   B allocate(int var1) throws IllegalArgumentException;
}
