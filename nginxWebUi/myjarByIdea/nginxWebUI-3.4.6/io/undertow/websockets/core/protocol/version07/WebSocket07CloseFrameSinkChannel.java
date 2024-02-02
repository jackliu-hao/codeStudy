package io.undertow.websockets.core.protocol.version07;

import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07CloseFrameSinkChannel extends WebSocket07FrameSinkChannel {
   WebSocket07CloseFrameSinkChannel(WebSocket07Channel wsChannel) {
      super(wsChannel, WebSocketFrameType.CLOSE);
   }
}
