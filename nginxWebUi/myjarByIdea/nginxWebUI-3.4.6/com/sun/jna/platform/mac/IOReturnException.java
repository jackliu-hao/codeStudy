package com.sun.jna.platform.mac;

public class IOReturnException extends RuntimeException {
   private static final long serialVersionUID = 1L;
   private int ioReturn;

   public IOReturnException(int kr) {
      this(kr, formatMessage(kr));
   }

   protected IOReturnException(int kr, String msg) {
      super(msg);
      this.ioReturn = kr;
   }

   public int getIOReturnCode() {
      return this.ioReturn;
   }

   public static int getSystem(int kr) {
      return kr >> 26 & 63;
   }

   public static int getSubSystem(int kr) {
      return kr >> 14 & 4095;
   }

   public static int getCode(int kr) {
      return kr & 16383;
   }

   private static String formatMessage(int kr) {
      return "IOReturn error code: " + kr + " (system=" + getSystem(kr) + ", subSystem=" + getSubSystem(kr) + ", code=" + getCode(kr) + ")";
   }
}
