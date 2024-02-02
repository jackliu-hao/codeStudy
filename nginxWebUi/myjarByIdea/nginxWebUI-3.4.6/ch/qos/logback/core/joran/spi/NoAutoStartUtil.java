package ch.qos.logback.core.joran.spi;

public class NoAutoStartUtil {
   public static boolean notMarkedWithNoAutoStart(Object o) {
      if (o == null) {
         return false;
      } else {
         Class<?> clazz = o.getClass();
         NoAutoStart a = (NoAutoStart)clazz.getAnnotation(NoAutoStart.class);
         return a == null;
      }
   }
}
