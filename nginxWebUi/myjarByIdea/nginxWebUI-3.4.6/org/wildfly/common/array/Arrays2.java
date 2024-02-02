package org.wildfly.common.array;

import java.lang.reflect.Array;
import java.util.Arrays;

public final class Arrays2 {
   private Arrays2() {
   }

   public static boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int len) {
      if (offs1 >= 0 && offs1 + len <= a1.length) {
         if (offs2 >= 0 && offs2 + len <= a2.length) {
            for(int i = 0; i < len; ++i) {
               if (a1[i + offs1] != a2[i + offs2]) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean equals(byte[] a1, int offs1, byte[] a2) {
      return equals((byte[])a1, offs1, (byte[])a2, 0, a2.length);
   }

   public static boolean equals(char[] a1, int offs1, char[] a2, int offs2, int len) {
      if (offs1 + len > a1.length) {
         return false;
      } else if (offs2 + len > a2.length) {
         return false;
      } else {
         for(int i = 0; i < len; ++i) {
            if (a1[i + offs1] != a2[i + offs2]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean equals(char[] a1, int offs1, char[] a2) {
      return equals((char[])a1, offs1, (char[])a2, 0, a2.length);
   }

   public static boolean equals(char[] a1, int offs1, String a2, int offs2, int len) {
      if (offs1 + len > a1.length) {
         return false;
      } else if (offs2 + len > a2.length()) {
         return false;
      } else {
         for(int i = 0; i < len; ++i) {
            if (a1[i + offs1] != a2.charAt(i + offs2)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean equals(char[] a1, int offs1, String a2) {
      return equals((char[])a1, offs1, (String)a2, 0, a2.length());
   }

   public static boolean equals(String a1, int offs1, char[] a2) {
      return equals((char[])a2, 0, (String)a1, offs1, a2.length);
   }

   public static boolean equals(String a1, char[] a2) {
      return equals((String)a1, 0, (char[])a2);
   }

   @SafeVarargs
   public static <T> T[] of(T... items) {
      return items;
   }

   private static char hex(int v) {
      return (char)(v < 10 ? 48 + v : 97 + v - 10);
   }

   public static String toString(byte[] bytes) {
      StringBuilder b = new StringBuilder(bytes.length * 2);
      byte[] var2 = bytes;
      int var3 = bytes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte x = var2[var4];
         b.append(hex((x & 240) >> 4)).append(hex(x & 15));
      }

      return b.toString();
   }

   public static int indexOf(byte[] array, int search, int offs, int len) {
      for(int i = 0; i < len; ++i) {
         if (array[offs + i] == (byte)search) {
            return offs + i;
         }
      }

      return -1;
   }

   public static int indexOf(byte[] array, int search, int offs) {
      return indexOf(array, search, offs, array.length - offs);
   }

   public static int indexOf(byte[] array, int search) {
      return indexOf(array, search, 0, array.length);
   }

   public static <E> E[] createArray(Class<E> elementType, int size) {
      return (Object[])Array.newInstance(elementType, size);
   }

   public static <E> E[] compactNulls(E[] original) {
      int r = 0;

      do {
         E item = original[r++];
         if (item == null) {
            int w = r - 1;

            do {
               item = original[r++];
               if (item != null) {
                  original[w++] = item;
               }
            } while(r != original.length);

            return Arrays.copyOf(original, w);
         }
      } while(r != original.length);

      return original;
   }

   public static String objectToString(Object value) {
      if (value == null) {
         return "null";
      } else if (value instanceof Object[]) {
         return Arrays.deepToString((Object[])value);
      } else if (value.getClass().isArray()) {
         StringBuilder sb = new StringBuilder();
         sb.append('[');

         for(int i = 0; i < Array.getLength(value); ++i) {
            if (i != 0) {
               sb.append(", ");
            }

            sb.append(String.valueOf(Array.get(value, i)));
         }

         sb.append(']');
         return sb.toString();
      } else {
         return value.toString();
      }
   }
}
