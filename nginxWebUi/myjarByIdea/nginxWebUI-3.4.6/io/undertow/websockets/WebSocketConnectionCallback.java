package io.undertow.websockets;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public interface WebSocketConnectionCallback {
   void onConnect(WebSocketHttpExchange var1, WebSocketChannel var2);
}
