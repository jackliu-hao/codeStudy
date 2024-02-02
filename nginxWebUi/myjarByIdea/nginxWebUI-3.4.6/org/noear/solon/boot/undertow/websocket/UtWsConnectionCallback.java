package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class UtWsConnectionCallback implements WebSocketConnectionCallback {
   UtWsChannelListener listener = new UtWsChannelListener();

   public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
      this.listener.onOpen(exchange, channel);
      channel.getReceiveSetter().set(this.listener);
      channel.resumeReceives();
   }
}
