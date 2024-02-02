package io.undertow.protocols.http2;

import java.io.IOException;

public class StreamErrorException extends IOException {
   private final int errorId;

   public StreamErrorException(int errorId) {
      this.errorId = errorId;
   }

   public int getErrorId() {
      return this.errorId;
   }
}
