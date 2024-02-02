package ch.qos.logback.classic.spi;

public class EventArgUtil {
   public static final Throwable extractThrowable(Object[] argArray) {
      if (argArray != null && argArray.length != 0) {
         Object lastEntry = argArray[argArray.length - 1];
         return lastEntry instanceof Throwable ? (Throwable)lastEntry : null;
      } else {
         return null;
      }
   }

   public static Object[] trimmedCopy(Object[] argArray) {
      if (argArray != null && argArray.length != 0) {
         int trimemdLen = argArray.length - 1;
         Object[] trimmed = new Object[trimemdLen];
         System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
         return trimmed;
      } else {
         throw new IllegalStateException("non-sensical empty or null argument array");
      }
   }

   public static Object[] arrangeArguments(Object[] argArray) {
      return argArray;
   }

   public static boolean successfulExtraction(Throwable throwable) {
      return throwable != null;
   }
}
