package ch.qos.logback.core.util;

public class PropertySetterException extends Exception {
   private static final long serialVersionUID = -2771077768281663949L;

   public PropertySetterException(String msg) {
      super(msg);
   }

   public PropertySetterException(Throwable rootCause) {
      super(rootCause);
   }

   public PropertySetterException(String message, Throwable cause) {
      super(message, cause);
   }
}
