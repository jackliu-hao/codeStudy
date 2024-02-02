package ch.qos.logback.core.util;

public class ContentTypeUtil {
   public static boolean isTextual(String contextType) {
      return contextType == null ? false : contextType.startsWith("text");
   }

   public static String getSubType(String contextType) {
      if (contextType == null) {
         return null;
      } else {
         int index = contextType.indexOf(47);
         if (index == -1) {
            return null;
         } else {
            int subTypeStartIndex = index + 1;
            return subTypeStartIndex < contextType.length() ? contextType.substring(subTypeStartIndex) : null;
         }
      }
   }
}
