package cn.hutool.core.codec;

public class Rot {
   private static final char aCHAR = 'a';
   private static final char zCHAR = 'z';
   private static final char ACHAR = 'A';
   private static final char ZCHAR = 'Z';
   private static final char CHAR0 = '0';
   private static final char CHAR9 = '9';

   public static String encode13(String message) {
      return encode13(message, true);
   }

   public static String encode13(String message, boolean isEnocdeNumber) {
      return encode(message, 13, isEnocdeNumber);
   }

   public static String encode(String message, int offset, boolean isEnocdeNumber) {
      int len = message.length();
      char[] chars = new char[len];

      for(int i = 0; i < len; ++i) {
         chars[i] = encodeChar(message.charAt(i), offset, isEnocdeNumber);
      }

      return new String(chars);
   }

   public static String decode13(String rot) {
      return decode13(rot, true);
   }

   public static String decode13(String rot, boolean isDecodeNumber) {
      return decode(rot, 13, isDecodeNumber);
   }

   public static String decode(String rot, int offset, boolean isDecodeNumber) {
      int len = rot.length();
      char[] chars = new char[len];

      for(int i = 0; i < len; ++i) {
         chars[i] = decodeChar(rot.charAt(i), offset, isDecodeNumber);
      }

      return new String(chars);
   }

   private static char encodeChar(char c, int offset, boolean isDecodeNumber) {
      if (isDecodeNumber && c >= '0' && c <= '9') {
         c = (char)(c - 48);
         c = (char)((c + offset) % 10);
         c = (char)(c + 48);
      }

      if (c >= 'A' && c <= 'Z') {
         c = (char)(c - 65);
         c = (char)((c + offset) % 26);
         c = (char)(c + 65);
      } else if (c >= 'a' && c <= 'z') {
         c = (char)(c - 97);
         c = (char)((c + offset) % 26);
         c = (char)(c + 97);
      }

      return c;
   }

   private static char decodeChar(char c, int offset, boolean isDecodeNumber) {
      int temp = c;
      if (isDecodeNumber && c >= '0' && c <= '9') {
         temp = c - 48;

         for(temp -= offset; temp < 0; temp += 10) {
         }

         temp += 48;
      }

      if (temp >= 65 && temp <= 90) {
         temp -= 65;

         for(temp -= offset; temp < 0; temp += 26) {
         }

         temp += 65;
      } else if (temp >= 97 && temp <= 122) {
         temp -= 97;
         temp -= offset;
         if (temp < 0) {
            temp += 26;
         }

         temp += 97;
      }

      return (char)temp;
   }
}
