package freemarker.log;

import org.apache.log4j.MDC;

public class _Log4jOverSLF4JTester {
   private static final String MDC_KEY = _Log4jOverSLF4JTester.class.getName();

   public static final boolean test() {
      MDC.put(MDC_KEY, "");

      boolean var0;
      try {
         var0 = org.slf4j.MDC.get(MDC_KEY) != null;
      } finally {
         MDC.remove(MDC_KEY);
      }

      return var0;
   }
}
