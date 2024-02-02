package io.undertow.protocols.http2;

public class HpackException extends Exception {
   private final int closeCode;

   public HpackException() {
      this((String)null, 9);
   }

   public HpackException(String message, int closeCode) {
      super(message);
      this.closeCode = closeCode;
   }

   public HpackException(int closeCode) {
      this.closeCode = closeCode;
   }

   public int getCloseCode() {
      return this.closeCode;
   }
}
