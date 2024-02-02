package io.undertow.websockets.core;

public interface WebSocketCallback<T> {
  void complete(WebSocketChannel paramWebSocketChannel, T paramT);
  
  void onError(WebSocketChannel paramWebSocketChannel, T paramT, Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */