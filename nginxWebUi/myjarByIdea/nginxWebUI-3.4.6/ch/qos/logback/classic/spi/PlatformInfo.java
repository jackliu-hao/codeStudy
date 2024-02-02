package ch.qos.logback.classic.spi;

public class PlatformInfo {
   private static final int UNINITIALIZED = -1;
   private static int hasJMXObjectName = -1;

   public static boolean hasJMXObjectName() {
      if (hasJMXObjectName == -1) {
         try {
            Class.forName("javax.management.ObjectName");
            hasJMXObjectName = 1;
         } catch (Throwable var1) {
            hasJMXObjectName = 0;
         }
      }

      return hasJMXObjectName == 1;
   }
}
