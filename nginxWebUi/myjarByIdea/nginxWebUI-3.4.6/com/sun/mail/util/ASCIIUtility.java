package com.sun.mail.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ASCIIUtility {
   private ASCIIUtility() {
   }

   public static int parseInt(byte[] b, int start, int end, int radix) throws NumberFormatException {
      if (b == null) {
         throw new NumberFormatException("null");
      } else {
         int result = 0;
         boolean negative = false;
         int i = start;
         if (end > start) {
            int limit;
            if (b[start] == 45) {
               negative = true;
               limit = Integer.MIN_VALUE;
               i = start + 1;
            } else {
               limit = -2147483647;
            }

            int multmin = limit / radix;
            int digit;
            if (i < end) {
               digit = Character.digit((char)b[i++], radix);
               if (digit < 0) {
                  throw new NumberFormatException("illegal number: " + toString(b, start, end));
               }

               result = -digit;
            }

            while(i < end) {
               digit = Character.digit((char)b[i++], radix);
               if (digit < 0) {
                  throw new NumberFormatException("illegal number");
               }

               if (result < multmin) {
                  throw new NumberFormatException("illegal number");
               }

               result *= radix;
               if (result < limit + digit) {
                  throw new NumberFormatException("illegal number");
               }

               result -= digit;
            }

            if (negative) {
               if (i > start + 1) {
                  return result;
               } else {
                  throw new NumberFormatException("illegal number");
               }
            } else {
               return -result;
            }
         } else {
            throw new NumberFormatException("illegal number");
         }
      }
   }

   public static int parseInt(byte[] b, int start, int end) throws NumberFormatException {
      return parseInt(b, start, end, 10);
   }

   public static long parseLong(byte[] b, int start, int end, int radix) throws NumberFormatException {
      if (b == null) {
         throw new NumberFormatException("null");
      } else {
         long result = 0L;
         boolean negative = false;
         int i = start;
         if (end > start) {
            long limit;
            if (b[start] == 45) {
               negative = true;
               limit = Long.MIN_VALUE;
               i = start + 1;
            } else {
               limit = -9223372036854775807L;
            }

            long multmin = limit / (long)radix;
            int digit;
            if (i < end) {
               digit = Character.digit((char)b[i++], radix);
               if (digit < 0) {
                  throw new NumberFormatException("illegal number: " + toString(b, start, end));
               }

               result = (long)(-digit);
            }

            while(i < end) {
               digit = Character.digit((char)b[i++], radix);
               if (digit < 0) {
                  throw new NumberFormatException("illegal number");
               }

               if (result < multmin) {
                  throw new NumberFormatException("illegal number");
               }

               result *= (long)radix;
               if (result < limit + (long)digit) {
                  throw new NumberFormatException("illegal number");
               }

               result -= (long)digit;
            }

            if (negative) {
               if (i > start + 1) {
                  return result;
               } else {
                  throw new NumberFormatException("illegal number");
               }
            } else {
               return -result;
            }
         } else {
            throw new NumberFormatException("illegal number");
         }
      }
   }

   public static long parseLong(byte[] b, int start, int end) throws NumberFormatException {
      return parseLong(b, start, end, 10);
   }

   public static String toString(byte[] b, int start, int end) {
      int size = end - start;
      char[] theChars = new char[size];
      int i = 0;

      for(int j = start; i < size; theChars[i++] = (char)(b[j++] & 255)) {
      }

      return new String(theChars);
   }

   public static String toString(byte[] b) {
      return toString(b, 0, b.length);
   }

   public static String toString(ByteArrayInputStream is) {
      int size = is.available();
      char[] theChars = new char[size];
      byte[] bytes = new byte[size];
      is.read(bytes, 0, size);

      for(int i = 0; i < size; theChars[i] = (char)(bytes[i++] & 255)) {
      }

      return new String(theChars);
   }

   public static byte[] getBytes(String s) {
      char[] chars = s.toCharArray();
      int size = chars.length;
      byte[] bytes = new byte[size];

      for(int i = 0; i < size; bytes[i] = (byte)chars[i++]) {
      }

      return bytes;
   }

   public static byte[] getBytes(InputStream is) throws IOException {
      int size = 1024;
      byte[] buf;
      if (is instanceof ByteArrayInputStream) {
         size = is.available();
         buf = new byte[size];
         is.read(buf, 0, size);
      } else {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         buf = new byte[size];

         int len;
         while((len = is.read(buf, 0, size)) != -1) {
            bos.write(buf, 0, len);
         }

         buf = bos.toByteArray();
      }

      return buf;
   }
}
