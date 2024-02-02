package org.apache.http.pool;

public interface PoolEntryCallback<T, C> {
  void process(PoolEntry<T, C> paramPoolEntry);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\pool\PoolEntryCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */