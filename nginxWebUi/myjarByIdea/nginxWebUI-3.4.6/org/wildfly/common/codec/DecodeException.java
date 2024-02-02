package org.wildfly.common.codec;

public class DecodeException extends IllegalArgumentException {
   private static final long serialVersionUID = 5823281980783313991L;

   public DecodeException() {
   }

   public DecodeException(String msg) {
      super(msg);
   }

   public DecodeException(Throwable cause) {
      super(cause);
   }

   public DecodeException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
