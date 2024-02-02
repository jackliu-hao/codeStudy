package ch.qos.logback.classic.spi;

import ch.qos.logback.core.CoreConstants;
import java.util.Iterator;
import java.util.List;

public class CallerData {
   public static final String NA = "?";
   private static final String LOG4J_CATEGORY = "org.apache.log4j.Category";
   private static final String SLF4J_BOUNDARY = "org.slf4j.Logger";
   public static final int LINE_NA = -1;
   public static final String CALLER_DATA_NA;
   public static final StackTraceElement[] EMPTY_CALLER_DATA_ARRAY;

   public static StackTraceElement[] extract(Throwable t, String fqnOfInvokingClass, int maxDepth, List<String> frameworkPackageList) {
      if (t == null) {
         return null;
      } else {
         StackTraceElement[] steArray = t.getStackTrace();
         int found = -1;

         int availableDepth;
         for(availableDepth = 0; availableDepth < steArray.length; ++availableDepth) {
            if (isInFrameworkSpace(steArray[availableDepth].getClassName(), fqnOfInvokingClass, frameworkPackageList)) {
               found = availableDepth + 1;
            } else if (found != -1) {
               break;
            }
         }

         if (found == -1) {
            return EMPTY_CALLER_DATA_ARRAY;
         } else {
            availableDepth = steArray.length - found;
            int desiredDepth = maxDepth < availableDepth ? maxDepth : availableDepth;
            StackTraceElement[] callerDataArray = new StackTraceElement[desiredDepth];

            for(int i = 0; i < desiredDepth; ++i) {
               callerDataArray[i] = steArray[found + i];
            }

            return callerDataArray;
         }
      }
   }

   static boolean isInFrameworkSpace(String currentClass, String fqnOfInvokingClass, List<String> frameworkPackageList) {
      return currentClass.equals(fqnOfInvokingClass) || currentClass.equals("org.apache.log4j.Category") || currentClass.startsWith("org.slf4j.Logger") || isInFrameworkSpaceList(currentClass, frameworkPackageList);
   }

   private static boolean isInFrameworkSpaceList(String currentClass, List<String> frameworkPackageList) {
      if (frameworkPackageList == null) {
         return false;
      } else {
         Iterator var2 = frameworkPackageList.iterator();

         String s;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            s = (String)var2.next();
         } while(!currentClass.startsWith(s));

         return true;
      }
   }

   public static StackTraceElement naInstance() {
      return new StackTraceElement("?", "?", "?", -1);
   }

   static {
      CALLER_DATA_NA = "?#?:?" + CoreConstants.LINE_SEPARATOR;
      EMPTY_CALLER_DATA_ARRAY = new StackTraceElement[0];
   }
}
