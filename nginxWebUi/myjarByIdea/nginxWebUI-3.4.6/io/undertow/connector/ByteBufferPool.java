package io.undertow.connector;

import java.io.Closeable;

public interface ByteBufferPool extends Closeable {
   PooledByteBuffer allocate();

   ByteBufferPool getArrayBackedPool();

   void close();

   int getBufferSize();

   boolean isDirect();
}
