package io.undertow.websockets.core;

public interface WebSocketFrame extends WebSocketChannel.PartialFrame {
   boolean isFinalFragment();
}
