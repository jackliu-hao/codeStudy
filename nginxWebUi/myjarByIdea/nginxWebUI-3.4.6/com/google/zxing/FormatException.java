package com.google.zxing;

public final class FormatException extends ReaderException {
   private static final FormatException INSTANCE;

   private FormatException() {
   }

   private FormatException(Throwable cause) {
      super(cause);
   }

   public static FormatException getFormatInstance() {
      return isStackTrace ? new FormatException() : INSTANCE;
   }

   public static FormatException getFormatInstance(Throwable cause) {
      return isStackTrace ? new FormatException(cause) : INSTANCE;
   }

   static {
      (INSTANCE = new FormatException()).setStackTrace(NO_TRACE);
   }
}
