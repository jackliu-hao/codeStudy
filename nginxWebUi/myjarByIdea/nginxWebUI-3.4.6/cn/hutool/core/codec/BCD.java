package cn.hutool.core.codec;

import cn.hutool.core.lang.Assert;

public class BCD {
   public static byte[] strToBcd(String asc) {
      int len = asc.length();
      int mod = len % 2;
      if (mod != 0) {
         asc = "0" + asc;
         len = asc.length();
      }

      if (len >= 2) {
         len >>= 1;
      }

      byte[] bbt = new byte[len];
      byte[] abt = asc.getBytes();

      for(int p = 0; p < asc.length() / 2; ++p) {
         int j;
         if (abt[2 * p] >= 48 && abt[2 * p] <= 57) {
            j = abt[2 * p] - 48;
         } else if (abt[2 * p] >= 97 && abt[2 * p] <= 122) {
            j = abt[2 * p] - 97 + 10;
         } else {
            j = abt[2 * p] - 65 + 10;
         }

         int k;
         if (abt[2 * p + 1] >= 48 && abt[2 * p + 1] <= 57) {
            k = abt[2 * p + 1] - 48;
         } else if (abt[2 * p + 1] >= 97 && abt[2 * p + 1] <= 122) {
            k = abt[2 * p + 1] - 97 + 10;
         } else {
            k = abt[2 * p + 1] - 65 + 10;
         }

         int a = (j << 4) + k;
         byte b = (byte)a;
         bbt[p] = b;
      }

      return bbt;
   }

   public static byte[] ascToBcd(byte[] ascii) {
      Assert.notNull(ascii, "Ascii must be not null!");
      return ascToBcd(ascii, ascii.length);
   }

   public static byte[] ascToBcd(byte[] ascii, int ascLength) {
      Assert.notNull(ascii, "Ascii must be not null!");
      byte[] bcd = new byte[ascLength / 2];
      int j = 0;

      for(int i = 0; i < (ascLength + 1) / 2; ++i) {
         bcd[i] = ascToBcd(ascii[j++]);
         bcd[i] = (byte)((j >= ascLength ? 0 : ascToBcd(ascii[j++])) + (bcd[i] << 4));
      }

      return bcd;
   }

   public static String bcdToStr(byte[] bytes) {
      Assert.notNull(bytes, "Bcd bytes must be not null!");
      char[] temp = new char[bytes.length * 2];

      for(int i = 0; i < bytes.length; ++i) {
         char val = (char)((bytes[i] & 240) >> 4 & 15);
         temp[i * 2] = (char)(val > '\t' ? val + 65 - 10 : val + 48);
         val = (char)(bytes[i] & 15);
         temp[i * 2 + 1] = (char)(val > '\t' ? val + 65 - 10 : val + 48);
      }

      return new String(temp);
   }

   private static byte ascToBcd(byte asc) {
      byte bcd;
      if (asc >= 48 && asc <= 57) {
         bcd = (byte)(asc - 48);
      } else if (asc >= 65 && asc <= 70) {
         bcd = (byte)(asc - 65 + 10);
      } else if (asc >= 97 && asc <= 102) {
         bcd = (byte)(asc - 97 + 10);
      } else {
         bcd = (byte)(asc - 48);
      }

      return bcd;
   }
}
