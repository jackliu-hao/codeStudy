package io.undertow.websockets.core.protocol.version07;

import io.undertow.websockets.core.WebSocketFrameType;

class WebSocket07TextFrameSinkChannel extends WebSocket07FrameSinkChannel {
   WebSocket07TextFrameSinkChannel(WebSocket07Channel wsChannel) {
      super(wsChannel, WebSocketFrameType.TEXT);
   }

   public boolean isFragmentationSupported() {
      return true;
   }

   public boolean areExtensionsSupported() {
      return true;
   }
}
