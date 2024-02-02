package io.undertow.util;

import java.io.Closeable;

public interface PooledObject<T> extends Closeable, AutoCloseable {
   T getObject();

   void close();
}
