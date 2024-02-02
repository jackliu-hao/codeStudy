package io.undertow.websockets.core;

import java.io.IOException;

public class WebSocketException extends IOException {
   private static final long serialVersionUID = -6784834646314672530L;

   public WebSocketException() {
   }

   public WebSocketException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public WebSocketException(String msg) {
      super(msg);
   }

   public WebSocketException(Throwable cause) {
      super(cause);
   }
}
