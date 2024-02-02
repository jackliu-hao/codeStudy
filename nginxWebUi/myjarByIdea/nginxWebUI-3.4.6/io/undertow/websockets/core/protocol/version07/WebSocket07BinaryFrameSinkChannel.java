package io.undertow.websockets.core.protocol.version07;

import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07BinaryFrameSinkChannel extends WebSocket07FrameSinkChannel {
   WebSocket07BinaryFrameSinkChannel(WebSocket07Channel wsChannel) {
      super(wsChannel, WebSocketFrameType.BINARY);
   }

   public boolean isFragmentationSupported() {
      return true;
   }

   public boolean areExtensionsSupported() {
      return true;
   }
}
