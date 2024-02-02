package io.undertow.protocols.http2;

import java.io.IOException;

public class ConnectionErrorException extends IOException {
   private final int code;

   public ConnectionErrorException(int code) {
      this.code = code;
   }

   public ConnectionErrorException(int code, String message) {
      super(message);
      this.code = code;
   }

   public ConnectionErrorException(int code, Throwable cause) {
      super(cause);
      this.code = code;
   }

   public int getCode() {
      return this.code;
   }
}
