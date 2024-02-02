package org.noear.solon.validation.util;

public class StringUtils {
   public static boolean isDigits(String str) {
      if (str != null && str.length() != 0) {
         int l = str.length();

         for(int i = 0; i < l; ++i) {
            if (!Character.isDigit(str.codePointAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isInteger(String str) {
      return isNumberDo(str, false);
   }

   public static boolean isNumber(String str) {
      return isNumberDo(str, true);
   }

   private static boolean isNumberDo(String str, boolean incDot) {
      if (str != null && str.length() != 0) {
         char[] chars = str.toCharArray();
         int l = chars.length;
         int start = chars[0] != '-' && chars[0] != '+' ? 0 : 1;
         boolean hasDot = false;

         for(int i = start; i < l; ++i) {
            int ch = chars[i];
            if (incDot && ch == '.') {
               if (hasDot) {
                  return false;
               }

               hasDot = true;
            } else if (!Character.isDigit(ch)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
