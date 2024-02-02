package io.undertow.websockets.core.protocol.version07;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07TextFrameSourceChannel extends StreamSourceFrameChannel {
   WebSocket07TextFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, boolean finalFragment, Masker masker, UTF8Checker checker, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.TEXT, rsv, finalFragment, pooled, frameLength, masker, checker);
   }

   WebSocket07TextFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, boolean finalFragment, UTF8Checker checker, PooledByteBuffer pooled, long frameLength) {
      super(wsChannel, WebSocketFrameType.TEXT, rsv, finalFragment, pooled, frameLength, (Masker)null, checker);
   }
}
