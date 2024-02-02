package io.undertow.util;

public class HexConverter {
   private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private static final byte[] HEX_BYTES = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

   public static String convertToHexString(byte[] toBeConverted) {
      if (toBeConverted == null) {
         throw new NullPointerException("Parameter to be converted can not be null");
      } else {
         char[] converted = new char[toBeConverted.length * 2];

         for(int i = 0; i < toBeConverted.length; ++i) {
            byte b = toBeConverted[i];
            converted[i * 2] = HEX_CHARS[b >> 4 & 15];
            converted[i * 2 + 1] = HEX_CHARS[b & 15];
         }

         return String.valueOf(converted);
      }
   }

   public static byte[] convertToHexBytes(byte[] toBeConverted) {
      if (toBeConverted == null) {
         throw new NullPointerException("Parameter to be converted can not be null");
      } else {
         byte[] converted = new byte[toBeConverted.length * 2];

         for(int i = 0; i < toBeConverted.length; ++i) {
            byte b = toBeConverted[i];
            converted[i * 2] = HEX_BYTES[b >> 4 & 15];
            converted[i * 2 + 1] = HEX_BYTES[b & 15];
         }

         return converted;
      }
   }

   public static byte[] convertFromHex(char[] toConvert) {
      if (toConvert.length % 2 != 0) {
         throw new IllegalArgumentException("The supplied character array must contain an even number of hex chars.");
      } else {
         byte[] response = new byte[toConvert.length / 2];

         for(int i = 0; i < response.length; ++i) {
            int posOne = i * 2;
            response[i] = (byte)(toByte(toConvert, posOne) << 4 | toByte(toConvert, posOne + 1));
         }

         return response;
      }
   }

   private static byte toByte(char[] toConvert, int pos) {
      int response = Character.digit(toConvert[pos], 16);
      if (response >= 0 && response <= 15) {
         return (byte)response;
      } else {
         throw new IllegalArgumentException("Non-hex character '" + toConvert[pos] + "' at index=" + pos);
      }
   }

   public static byte[] convertFromHex(String toConvert) {
      return convertFromHex(toConvert.toCharArray());
   }

   public static void main(String[] args) {
      byte[] toConvert = new byte[256];

      for(int i = 0; i < toConvert.length; ++i) {
         toConvert[i] = (byte)i;
      }

      String hexValue = convertToHexString(toConvert);
      System.out.println("Converted - " + hexValue);
      byte[] convertedBack = convertFromHex(hexValue);
      StringBuilder sb = new StringBuilder();
      byte[] var5 = convertedBack;
      int var6 = convertedBack.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         byte current = var5[var7];
         sb.append(current).append(" ");
      }

      System.out.println("Converted Back " + sb.toString());
   }
}
