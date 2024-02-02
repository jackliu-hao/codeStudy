package io.undertow.util;

public interface ObjectPool<T> {
  PooledObject<T> allocate();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ObjectPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */