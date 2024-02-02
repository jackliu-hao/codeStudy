package io.undertow.websockets.core.protocol.version07;

import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07PingFrameSinkChannel extends WebSocket07FrameSinkChannel {
   WebSocket07PingFrameSinkChannel(WebSocket07Channel wsChannel) {
      super(wsChannel, WebSocketFrameType.PING);
   }
}
