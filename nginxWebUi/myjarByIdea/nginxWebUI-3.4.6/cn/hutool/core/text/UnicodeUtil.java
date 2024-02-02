package cn.hutool.core.text;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;

public class UnicodeUtil {
   public static String toString(String unicode) {
      if (StrUtil.isBlank(unicode)) {
         return unicode;
      } else {
         int len = unicode.length();
         StringBuilder sb = new StringBuilder(len);
         int pos = 0;

         int i;
         while((i = StrUtil.indexOfIgnoreCase(unicode, "\\u", pos)) != -1) {
            sb.append(unicode, pos, i);
            pos = i;
            if (i + 5 >= len) {
               break;
            }

            try {
               char c = (char)Integer.parseInt(unicode.substring(i + 2, i + 6), 16);
               sb.append(c);
               pos = i + 6;
            } catch (NumberFormatException var7) {
               sb.append(unicode, i, i + 2);
               pos = i + 2;
            }
         }

         if (pos < len) {
            sb.append(unicode, pos, len);
         }

         return sb.toString();
      }
   }

   public static String toUnicode(char c) {
      return HexUtil.toUnicodeHex(c);
   }

   public static String toUnicode(int c) {
      return HexUtil.toUnicodeHex(c);
   }

   public static String toUnicode(String str) {
      return toUnicode(str, true);
   }

   public static String toUnicode(String str, boolean isSkipAscii) {
      if (StrUtil.isEmpty(str)) {
         return str;
      } else {
         int len = str.length();
         StringBuilder unicode = new StringBuilder(str.length() * 6);

         for(int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (isSkipAscii && CharUtil.isAsciiPrintable(c)) {
               unicode.append(c);
            } else {
               unicode.append(HexUtil.toUnicodeHex(c));
            }
         }

         return unicode.toString();
      }
   }
}
