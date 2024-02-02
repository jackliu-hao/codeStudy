package cn.hutool.core.util;

import cn.hutool.core.text.ASCIIStrCache;
import cn.hutool.core.text.CharPool;

public class CharUtil implements CharPool {
   public static boolean isAscii(char ch) {
      return ch < 128;
   }

   public static boolean isAsciiPrintable(char ch) {
      return ch >= ' ' && ch < 127;
   }

   public static boolean isAsciiControl(char ch) {
      return ch < ' ' || ch == 127;
   }

   public static boolean isLetter(char ch) {
      return isLetterUpper(ch) || isLetterLower(ch);
   }

   public static boolean isLetterUpper(char ch) {
      return ch >= 'A' && ch <= 'Z';
   }

   public static boolean isLetterLower(char ch) {
      return ch >= 'a' && ch <= 'z';
   }

   public static boolean isNumber(char ch) {
      return ch >= '0' && ch <= '9';
   }

   public static boolean isHexChar(char c) {
      return isNumber(c) || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
   }

   public static boolean isLetterOrNumber(char ch) {
      return isLetter(ch) || isNumber(ch);
   }

   public static String toString(char c) {
      return ASCIIStrCache.toString(c);
   }

   public static boolean isCharClass(Class<?> clazz) {
      return clazz == Character.class || clazz == Character.TYPE;
   }

   public static boolean isChar(Object value) {
      return value instanceof Character || value.getClass() == Character.TYPE;
   }

   public static boolean isBlankChar(char c) {
      return isBlankChar((int)c);
   }

   public static boolean isBlankChar(int c) {
      return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234 || c == 0;
   }

   public static boolean isEmoji(char c) {
      return c != 0 && c != '\t' && c != '\n' && c != '\r' && (c < ' ' || c > '\ud7ff') && (c < '\ue000' || c > '�') && (c < 1048576 || c > 1114111);
   }

   public static boolean isFileSeparator(char c) {
      return '/' == c || '\\' == c;
   }

   public static boolean equals(char c1, char c2, boolean caseInsensitive) {
      if (caseInsensitive) {
         return Character.toLowerCase(c1) == Character.toLowerCase(c2);
      } else {
         return c1 == c2;
      }
   }

   public static int getType(int c) {
      return Character.getType(c);
   }

   public static int digit16(int b) {
      return Character.digit(b, 16);
   }

   public static char toCloseChar(char c) {
      int result = c;
      if (c >= '1' && c <= '9') {
         result = 9312 + c - 49;
      } else if (c >= 'A' && c <= 'Z') {
         result = 9398 + c - 65;
      } else if (c >= 'a' && c <= 'z') {
         result = 9424 + c - 97;
      }

      return (char)result;
   }

   public static char toCloseByNumber(int number) {
      if (number > 20) {
         throw new IllegalArgumentException("Number must be [1-20]");
      } else {
         return (char)(9312 + number - 1);
      }
   }
}
