package io.undertow.websockets.core;

public class WebSocketFrameCorruptedException extends WebSocketException {
   private static final long serialVersionUID = -6784834646314476130L;

   public WebSocketFrameCorruptedException() {
   }

   public WebSocketFrameCorruptedException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public WebSocketFrameCorruptedException(String msg) {
      super(msg);
   }

   public WebSocketFrameCorruptedException(Throwable cause) {
      super(cause);
   }
}
