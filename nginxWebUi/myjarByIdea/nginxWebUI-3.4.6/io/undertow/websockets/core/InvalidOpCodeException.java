package io.undertow.websockets.core;

public class InvalidOpCodeException extends WebSocketException {
   public InvalidOpCodeException() {
   }

   public InvalidOpCodeException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public InvalidOpCodeException(String msg) {
      super(msg);
   }

   public InvalidOpCodeException(Throwable cause) {
      super(cause);
   }
}
