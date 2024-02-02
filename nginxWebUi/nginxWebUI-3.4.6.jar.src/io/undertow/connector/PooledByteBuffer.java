package io.undertow.connector;

import java.io.Closeable;
import java.nio.ByteBuffer;

public interface PooledByteBuffer extends AutoCloseable, Closeable {
  ByteBuffer getBuffer();
  
  void close();
  
  boolean isOpen();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\connector\PooledByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */