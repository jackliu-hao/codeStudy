package io.undertow.websockets;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public interface WebSocketConnectionCallback {
  void onConnect(WebSocketHttpExchange paramWebSocketHttpExchange, WebSocketChannel paramWebSocketChannel);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\WebSocketConnectionCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */