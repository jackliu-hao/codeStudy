package io.undertow.server;

import java.io.IOException;

public class RequestTooBigException extends IOException {
   public RequestTooBigException() {
   }

   public RequestTooBigException(String message) {
      super(message);
   }

   public RequestTooBigException(String message, Throwable cause) {
      super(message, cause);
   }

   public RequestTooBigException(Throwable cause) {
      super(cause);
   }
}
