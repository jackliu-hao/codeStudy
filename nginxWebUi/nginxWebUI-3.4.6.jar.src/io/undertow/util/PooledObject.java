package io.undertow.util;

import java.io.Closeable;

public interface PooledObject<T> extends Closeable, AutoCloseable {
  T getObject();
  
  void close();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PooledObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */