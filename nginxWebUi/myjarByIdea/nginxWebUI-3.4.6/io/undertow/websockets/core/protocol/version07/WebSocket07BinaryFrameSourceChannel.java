package io.undertow.websockets.core.protocol.version07;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07BinaryFrameSourceChannel extends StreamSourceFrameChannel {
   WebSocket07BinaryFrameSourceChannel(WebSocketChannel wsChannel, int rsv, boolean finalFragment, Masker masker, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.BINARY, rsv, finalFragment, pooled, frameLength, masker);
   }

   WebSocket07BinaryFrameSourceChannel(WebSocketChannel wsChannel, int rsv, boolean finalFragment, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.BINARY, rsv, finalFragment, pooled, frameLength, (Masker)null);
   }
}
