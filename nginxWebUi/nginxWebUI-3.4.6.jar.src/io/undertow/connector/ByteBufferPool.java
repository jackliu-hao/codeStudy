package io.undertow.connector;

import java.io.Closeable;

public interface ByteBufferPool extends Closeable {
  PooledByteBuffer allocate();
  
  ByteBufferPool getArrayBackedPool();
  
  void close();
  
  int getBufferSize();
  
  boolean isDirect();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\connector\ByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */