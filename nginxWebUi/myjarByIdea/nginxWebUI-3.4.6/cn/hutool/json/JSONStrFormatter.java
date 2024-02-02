package cn.hutool.json;

import cn.hutool.core.util.StrUtil;

public class JSONStrFormatter {
   private static final String SPACE = "    ";
   private static final char NEW_LINE = '\n';

   public static String format(String json) {
      StringBuilder result = new StringBuilder();
      Character wrapChar = null;
      boolean isEscapeMode = false;
      int length = json.length();
      int number = 0;

      for(int i = 0; i < length; ++i) {
         char key = json.charAt(i);
         if ('"' != key && '\'' != key) {
            if ('\\' == key) {
               if (null != wrapChar) {
                  isEscapeMode = !isEscapeMode;
                  result.append(key);
                  continue;
               }

               result.append(key);
            }

            if (null != wrapChar) {
               result.append(key);
            } else if (key != '[' && key != '{') {
               if (key != ']' && key != '}') {
                  if (key == ',') {
                     result.append(key);
                     result.append('\n');
                     result.append(indent(number));
                  } else {
                     if (i > 1 && json.charAt(i - 1) == ':') {
                        result.append(' ');
                     }

                     result.append(key);
                  }
               } else {
                  result.append('\n');
                  --number;
                  result.append(indent(number));
                  result.append(key);
               }
            } else {
               if (i > 1 && json.charAt(i - 1) == ':') {
                  result.append('\n');
                  result.append(indent(number));
               }

               result.append(key);
               result.append('\n');
               ++number;
               result.append(indent(number));
            }
         } else {
            if (null == wrapChar) {
               wrapChar = key;
            } else if (isEscapeMode) {
               isEscapeMode = false;
            } else if (wrapChar.equals(key)) {
               wrapChar = null;
            }

            if (i > 1 && json.charAt(i - 1) == ':') {
               result.append(' ');
            }

            result.append(key);
         }
      }

      return result.toString();
   }

   private static String indent(int number) {
      return StrUtil.repeat("    ", number);
   }
}
