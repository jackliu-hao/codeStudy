package org.apache.http.pool;

import java.util.concurrent.Future;
import org.apache.http.concurrent.FutureCallback;

public interface ConnPool<T, E> {
  Future<E> lease(T paramT, Object paramObject, FutureCallback<E> paramFutureCallback);
  
  void release(E paramE, boolean paramBoolean);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\pool\ConnPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */