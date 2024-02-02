package io.undertow.websockets.core;

public interface WebSocketCallback<T> {
   void complete(WebSocketChannel var1, T var2);

   void onError(WebSocketChannel var1, T var2, Throwable var3);
}
