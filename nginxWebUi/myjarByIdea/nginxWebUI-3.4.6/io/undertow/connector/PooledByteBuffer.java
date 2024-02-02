package io.undertow.connector;

import java.io.Closeable;
import java.nio.ByteBuffer;

public interface PooledByteBuffer extends AutoCloseable, Closeable {
   ByteBuffer getBuffer();

   void close();

   boolean isOpen();
}
