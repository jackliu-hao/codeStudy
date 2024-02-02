package org.apache.commons.compress.harmony.unpack200;

public final class SegmentUtils {
   public static int countArgs(String descriptor) {
      return countArgs(descriptor, 1);
   }

   public static int countInvokeInterfaceArgs(String descriptor) {
      return countArgs(descriptor, 2);
   }

   protected static int countArgs(String descriptor, int widthOfLongsAndDoubles) {
      int bra = descriptor.indexOf(40);
      int ket = descriptor.indexOf(41);
      if (bra != -1 && ket != -1 && ket >= bra) {
         boolean inType = false;
         boolean consumingNextType = false;
         int count = 0;

         for(int i = bra + 1; i < ket; ++i) {
            char charAt = descriptor.charAt(i);
            if (inType && charAt == ';') {
               inType = false;
               consumingNextType = false;
            } else if (!inType && charAt == 'L') {
               inType = true;
               ++count;
            } else if (charAt == '[') {
               consumingNextType = true;
            } else if (!inType) {
               if (consumingNextType) {
                  ++count;
                  consumingNextType = false;
               } else if (charAt != 'D' && charAt != 'J') {
                  ++count;
               } else {
                  count += widthOfLongsAndDoubles;
               }
            }
         }

         return count;
      } else {
         throw new IllegalArgumentException("No arguments");
      }
   }

   public static int countMatches(long[] flags, IMatcher matcher) {
      int count = 0;

      for(int i = 0; i < flags.length; ++i) {
         if (matcher.matches(flags[i])) {
            ++count;
         }
      }

      return count;
   }

   public static int countBit16(int[] flags) {
      int count = 0;

      for(int i = 0; i < flags.length; ++i) {
         if ((flags[i] & 65536) != 0) {
            ++count;
         }
      }

      return count;
   }

   public static int countBit16(long[] flags) {
      int count = 0;

      for(int i = 0; i < flags.length; ++i) {
         if ((flags[i] & 65536L) != 0L) {
            ++count;
         }
      }

      return count;
   }

   public static int countBit16(long[][] flags) {
      int count = 0;

      for(int i = 0; i < flags.length; ++i) {
         for(int j = 0; j < flags[i].length; ++j) {
            if ((flags[i][j] & 65536L) != 0L) {
               ++count;
            }
         }
      }

      return count;
   }

   public static int countMatches(long[][] flags, IMatcher matcher) {
      int count = 0;

      for(int i = 0; i < flags.length; ++i) {
         count += countMatches(flags[i], matcher);
      }

      return count;
   }
}
