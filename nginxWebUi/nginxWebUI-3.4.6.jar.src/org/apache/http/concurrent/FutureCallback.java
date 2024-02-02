package org.apache.http.concurrent;

public interface FutureCallback<T> {
  void completed(T paramT);
  
  void failed(Exception paramException);
  
  void cancelled();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\concurrent\FutureCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */