package org.apache.http.pool;

import java.util.concurrent.Future;
import org.apache.http.concurrent.FutureCallback;

public interface ConnPool<T, E> {
   Future<E> lease(T var1, Object var2, FutureCallback<E> var3);

   void release(E var1, boolean var2);
}
