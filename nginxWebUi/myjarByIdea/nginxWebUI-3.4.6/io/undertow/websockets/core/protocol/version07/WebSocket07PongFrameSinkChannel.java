package io.undertow.websockets.core.protocol.version07;

import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07PongFrameSinkChannel extends WebSocket07FrameSinkChannel {
   WebSocket07PongFrameSinkChannel(WebSocket07Channel wsChannel) {
      super(wsChannel, WebSocketFrameType.PONG);
   }
}
