package cn.hutool.core.codec;

public class Caesar {
   public static final String TABLE = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

   public static String encode(String message, int offset) {
      int len = message.length();
      char[] plain = message.toCharArray();

      for(int i = 0; i < len; ++i) {
         char c = message.charAt(i);
         if (Character.isLetter(c)) {
            plain[i] = encodeChar(c, offset);
         }
      }

      return new String(plain);
   }

   public static String decode(String cipherText, int offset) {
      int len = cipherText.length();
      char[] plain = cipherText.toCharArray();

      for(int i = 0; i < len; ++i) {
         char c = cipherText.charAt(i);
         if (Character.isLetter(c)) {
            plain[i] = decodeChar(c, offset);
         }
      }

      return new String(plain);
   }

   private static char encodeChar(char c, int offset) {
      int position = ("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".indexOf(c) + offset) % 52;
      return "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".charAt(position);
   }

   private static char decodeChar(char c, int offset) {
      int position = ("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".indexOf(c) - offset) % 52;
      if (position < 0) {
         position += 52;
      }

      return "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".charAt(position);
   }
}
