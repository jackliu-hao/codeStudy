package org.apache.commons.compress.utils;

import java.lang.Character.UnicodeBlock;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class ArchiveUtils {
   private static final int MAX_SANITIZED_NAME_LENGTH = 255;

   private ArchiveUtils() {
   }

   public static String toString(ArchiveEntry entry) {
      StringBuilder sb = new StringBuilder();
      sb.append((char)(entry.isDirectory() ? 'd' : '-'));
      String size = Long.toString(entry.getSize());
      sb.append(' ');

      for(int i = 7; i > size.length(); --i) {
         sb.append(' ');
      }

      sb.append(size);
      sb.append(' ').append(entry.getName());
      return sb.toString();
   }

   public static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length) {
      byte[] buffer1 = expected.getBytes(StandardCharsets.US_ASCII);
      return isEqual(buffer1, 0, buffer1.length, buffer, offset, length, false);
   }

   public static boolean matchAsciiBuffer(String expected, byte[] buffer) {
      return matchAsciiBuffer(expected, buffer, 0, buffer.length);
   }

   public static byte[] toAsciiBytes(String inputString) {
      return inputString.getBytes(StandardCharsets.US_ASCII);
   }

   public static String toAsciiString(byte[] inputBytes) {
      return new String(inputBytes, StandardCharsets.US_ASCII);
   }

   public static String toAsciiString(byte[] inputBytes, int offset, int length) {
      return new String(inputBytes, offset, length, StandardCharsets.US_ASCII);
   }

   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2, boolean ignoreTrailingNulls) {
      int minLen = length1 < length2 ? length1 : length2;

      int i;
      for(i = 0; i < minLen; ++i) {
         if (buffer1[offset1 + i] != buffer2[offset2 + i]) {
            return false;
         }
      }

      if (length1 == length2) {
         return true;
      } else if (!ignoreTrailingNulls) {
         return false;
      } else {
         if (length1 > length2) {
            for(i = length2; i < length1; ++i) {
               if (buffer1[offset1 + i] != 0) {
                  return false;
               }
            }
         } else {
            for(i = length1; i < length2; ++i) {
               if (buffer2[offset2 + i] != 0) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
      return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, false);
   }

   public static boolean isEqual(byte[] buffer1, byte[] buffer2) {
      return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, false);
   }

   public static boolean isEqual(byte[] buffer1, byte[] buffer2, boolean ignoreTrailingNulls) {
      return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, ignoreTrailingNulls);
   }

   public static boolean isEqualWithNull(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
      return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, true);
   }

   public static boolean isArrayZero(byte[] a, int size) {
      for(int i = 0; i < size; ++i) {
         if (a[i] != 0) {
            return false;
         }
      }

      return true;
   }

   public static String sanitize(String s) {
      char[] cs = s.toCharArray();
      char[] chars = cs.length <= 255 ? cs : Arrays.copyOf(cs, 255);
      if (cs.length > 255) {
         for(int i = 252; i < 255; ++i) {
            chars[i] = '.';
         }
      }

      StringBuilder sb = new StringBuilder();
      char[] var4 = chars;
      int var5 = chars.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         char c = var4[var6];
         if (!Character.isISOControl(c)) {
            Character.UnicodeBlock block = UnicodeBlock.of(c);
            if (block != null && block != UnicodeBlock.SPECIALS) {
               sb.append(c);
               continue;
            }
         }

         sb.append('?');
      }

      return sb.toString();
   }
}
