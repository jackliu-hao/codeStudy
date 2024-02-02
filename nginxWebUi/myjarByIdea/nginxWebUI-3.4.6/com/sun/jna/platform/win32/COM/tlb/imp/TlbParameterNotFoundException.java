package com.sun.jna.platform.win32.COM.tlb.imp;

public class TlbParameterNotFoundException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public TlbParameterNotFoundException() {
   }

   public TlbParameterNotFoundException(String msg) {
      super(msg);
   }

   public TlbParameterNotFoundException(Throwable cause) {
      super(cause);
   }

   public TlbParameterNotFoundException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
