package io.undertow.websockets.core;

public class WebSocketInvalidCloseCodeException extends WebSocketException {
   private static final long serialVersionUID = -6784834646314476130L;

   public WebSocketInvalidCloseCodeException() {
   }

   public WebSocketInvalidCloseCodeException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public WebSocketInvalidCloseCodeException(String msg) {
      super(msg);
   }

   public WebSocketInvalidCloseCodeException(Throwable cause) {
      super(cause);
   }
}
