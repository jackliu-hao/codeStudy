package io.undertow.websockets.core.protocol.version07;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07PongFrameSourceChannel extends StreamSourceFrameChannel {
   WebSocket07PongFrameSourceChannel(WebSocketChannel wsChannel, int rsv, Masker masker, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.PONG, rsv, true, pooled, frameLength, masker);
   }

   WebSocket07PongFrameSourceChannel(WebSocketChannel wsChannel, int rsv, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.PONG, rsv, true, pooled, frameLength, (Masker)null);
   }
}
