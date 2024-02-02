package org.apache.http.concurrent;

public interface FutureCallback<T> {
   void completed(T var1);

   void failed(Exception var1);

   void cancelled();
}
