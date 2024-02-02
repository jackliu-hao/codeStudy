package io.undertow.websockets.core;

public class WebSocketHandshakeException extends WebSocketException {
   private static final long serialVersionUID = 1L;

   public WebSocketHandshakeException() {
   }

   public WebSocketHandshakeException(String s) {
      super(s);
   }

   public WebSocketHandshakeException(String s, Throwable throwable) {
      super(s, throwable);
   }

   public WebSocketHandshakeException(Throwable cause) {
      super(cause);
   }
}
