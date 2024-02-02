package io.undertow.util;

public interface ObjectPool<T> {
   PooledObject<T> allocate();
}
