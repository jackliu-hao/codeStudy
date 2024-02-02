package io.undertow.server;

import java.io.IOException;

public class TruncatedResponseException extends IOException {
   public TruncatedResponseException() {
   }

   public TruncatedResponseException(String msg) {
      super(msg);
   }

   public TruncatedResponseException(Throwable cause) {
      super(cause);
   }

   public TruncatedResponseException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
