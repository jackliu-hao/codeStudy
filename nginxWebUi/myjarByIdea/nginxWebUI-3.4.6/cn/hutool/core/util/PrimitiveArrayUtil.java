package cn.hutool.core.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

public class PrimitiveArrayUtil {
   public static final int INDEX_NOT_FOUND = -1;

   public static boolean isEmpty(long[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(int[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(short[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(char[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(byte[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(double[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(float[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(boolean[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isNotEmpty(long[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(int[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(short[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(char[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(byte[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(double[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(float[] array) {
      return !isEmpty(array);
   }

   public static boolean isNotEmpty(boolean[] array) {
      return !isEmpty(array);
   }

   public static byte[] resize(byte[] bytes, int newSize) {
      if (newSize < 0) {
         return bytes;
      } else {
         byte[] newArray = new byte[newSize];
         if (newSize > 0 && isNotEmpty(bytes)) {
            System.arraycopy(bytes, 0, newArray, 0, Math.min(bytes.length, newSize));
         }

         return newArray;
      }
   }

   public static byte[] addAll(byte[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         byte[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            byte[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         byte[] result = new byte[length];
         length = 0;
         byte[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            byte[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static int[] addAll(int[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         int[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            int[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         int[] result = new int[length];
         length = 0;
         int[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            int[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static long[] addAll(long[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         long[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            long[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         long[] result = new long[length];
         length = 0;
         long[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            long[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static double[] addAll(double[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         double[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            double[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         double[] result = new double[length];
         length = 0;
         double[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            double[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static float[] addAll(float[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         float[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            float[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         float[] result = new float[length];
         length = 0;
         float[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            float[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static char[] addAll(char[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         char[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            char[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         char[] result = new char[length];
         length = 0;
         char[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            char[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static boolean[] addAll(boolean[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         boolean[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            boolean[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         boolean[] result = new boolean[length];
         length = 0;
         boolean[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            boolean[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static short[] addAll(short[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         short[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            short[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         short[] result = new short[length];
         length = 0;
         short[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            short[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static int[] range(int excludedEnd) {
      return range(0, excludedEnd, 1);
   }

   public static int[] range(int includedStart, int excludedEnd) {
      return range(includedStart, excludedEnd, 1);
   }

   public static int[] range(int includedStart, int excludedEnd, int step) {
      int deviation;
      if (includedStart > excludedEnd) {
         deviation = includedStart;
         includedStart = excludedEnd;
         excludedEnd = deviation;
      }

      if (step <= 0) {
         step = 1;
      }

      deviation = excludedEnd - includedStart;
      int length = deviation / step;
      if (deviation % step != 0) {
         ++length;
      }

      int[] range = new int[length];

      for(int i = 0; i < length; ++i) {
         range[i] = includedStart;
         includedStart += step;
      }

      return range;
   }

   public static byte[][] split(byte[] array, int len) {
      int amount = array.length / len;
      int remainder = array.length % len;
      if (remainder != 0) {
         ++amount;
      }

      byte[][] arrays = new byte[amount][];

      for(int i = 0; i < amount; ++i) {
         byte[] arr;
         if (i == amount - 1 && remainder != 0) {
            arr = new byte[remainder];
            System.arraycopy(array, i * len, arr, 0, remainder);
         } else {
            arr = new byte[len];
            System.arraycopy(array, i * len, arr, 0, len);
         }

         arrays[i] = arr;
      }

      return arrays;
   }

   public static int indexOf(long[] array, long value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(long[] array, long value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(long[] array, long value) {
      return indexOf(array, value) > -1;
   }

   public static int indexOf(int[] array, int value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(int[] array, int value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(int[] array, int value) {
      return indexOf(array, value) > -1;
   }

   public static int indexOf(short[] array, short value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(short[] array, short value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(short[] array, short value) {
      return indexOf(array, value) > -1;
   }

   public static int indexOf(char[] array, char value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(char[] array, char value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(char[] array, char value) {
      return indexOf(array, value) > -1;
   }

   public static int indexOf(byte[] array, byte value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(byte[] array, byte value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(byte[] array, byte value) {
      return indexOf(array, value) > -1;
   }

   public static int indexOf(double[] array, double value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (NumberUtil.equals(value, array[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(double[] array, double value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (NumberUtil.equals(value, array[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(double[] array, double value) {
      return indexOf(array, value) > -1;
   }

   public static int indexOf(float[] array, float value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (NumberUtil.equals(value, array[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(float[] array, float value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (NumberUtil.equals(value, array[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(float[] array, float value) {
      return indexOf(array, value) > -1;
   }

   public static int indexOf(boolean[] array, boolean value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int lastIndexOf(boolean[] array, boolean value) {
      if (null != array) {
         for(int i = array.length - 1; i >= 0; --i) {
            if (value == array[i]) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean contains(boolean[] array, boolean value) {
      return indexOf(array, value) > -1;
   }

   public static Integer[] wrap(int... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Integer[0];
         } else {
            Integer[] array = new Integer[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static int[] unWrap(Integer... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new int[0];
         } else {
            int[] array = new int[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Integer)ObjectUtil.defaultIfNull(values[i], (int)0);
            }

            return array;
         }
      }
   }

   public static Long[] wrap(long... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Long[0];
         } else {
            Long[] array = new Long[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static long[] unWrap(Long... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new long[0];
         } else {
            long[] array = new long[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Long)ObjectUtil.defaultIfNull(values[i], (Object)0L);
            }

            return array;
         }
      }
   }

   public static Character[] wrap(char... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Character[0];
         } else {
            Character[] array = new Character[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static char[] unWrap(Character... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new char[0];
         } else {
            char[] array = new char[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Character)ObjectUtil.defaultIfNull(values[i], (Object)'\u0000');
            }

            return array;
         }
      }
   }

   public static Byte[] wrap(byte... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Byte[0];
         } else {
            Byte[] array = new Byte[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static byte[] unWrap(Byte... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new byte[0];
         } else {
            byte[] array = new byte[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Byte)ObjectUtil.defaultIfNull(values[i], (byte)0);
            }

            return array;
         }
      }
   }

   public static Short[] wrap(short... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Short[0];
         } else {
            Short[] array = new Short[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static short[] unWrap(Short... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new short[0];
         } else {
            short[] array = new short[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Short)ObjectUtil.defaultIfNull(values[i], (Object)Short.valueOf((short)0));
            }

            return array;
         }
      }
   }

   public static Float[] wrap(float... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Float[0];
         } else {
            Float[] array = new Float[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static float[] unWrap(Float... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new float[0];
         } else {
            float[] array = new float[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Float)ObjectUtil.defaultIfNull(values[i], (Object)0.0F);
            }

            return array;
         }
      }
   }

   public static Double[] wrap(double... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Double[0];
         } else {
            Double[] array = new Double[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static double[] unWrap(Double... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new double[0];
         } else {
            double[] array = new double[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Double)ObjectUtil.defaultIfNull(values[i], (Object)0.0);
            }

            return array;
         }
      }
   }

   public static Boolean[] wrap(boolean... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new Boolean[0];
         } else {
            Boolean[] array = new Boolean[length];

            for(int i = 0; i < length; ++i) {
               array[i] = values[i];
            }

            return array;
         }
      }
   }

   public static boolean[] unWrap(Boolean... values) {
      if (null == values) {
         return null;
      } else {
         int length = values.length;
         if (0 == length) {
            return new boolean[0];
         } else {
            boolean[] array = new boolean[length];

            for(int i = 0; i < length; ++i) {
               array[i] = (Boolean)ObjectUtil.defaultIfNull(values[i], (Object)false);
            }

            return array;
         }
      }
   }

   public static byte[] sub(byte[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new byte[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new byte[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static int[] sub(int[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new int[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new int[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static long[] sub(long[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new long[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new long[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static short[] sub(short[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new short[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new short[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static char[] sub(char[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new char[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new char[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static double[] sub(double[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new double[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new double[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static float[] sub(float[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new float[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new float[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static boolean[] sub(boolean[] array, int start, int end) {
      int length = Array.getLength(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new boolean[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new boolean[0];
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static long[] remove(long[] array, int index) throws IllegalArgumentException {
      return (long[])((long[])remove((Object)array, index));
   }

   public static int[] remove(int[] array, int index) throws IllegalArgumentException {
      return (int[])((int[])remove((Object)array, index));
   }

   public static short[] remove(short[] array, int index) throws IllegalArgumentException {
      return (short[])((short[])remove((Object)array, index));
   }

   public static char[] remove(char[] array, int index) throws IllegalArgumentException {
      return (char[])((char[])remove((Object)array, index));
   }

   public static byte[] remove(byte[] array, int index) throws IllegalArgumentException {
      return (byte[])((byte[])remove((Object)array, index));
   }

   public static double[] remove(double[] array, int index) throws IllegalArgumentException {
      return (double[])((double[])remove((Object)array, index));
   }

   public static float[] remove(float[] array, int index) throws IllegalArgumentException {
      return (float[])((float[])remove((Object)array, index));
   }

   public static boolean[] remove(boolean[] array, int index) throws IllegalArgumentException {
      return (boolean[])((boolean[])remove((Object)array, index));
   }

   public static Object remove(Object array, int index) throws IllegalArgumentException {
      if (null == array) {
         return null;
      } else {
         int length = Array.getLength(array);
         if (index >= 0 && index < length) {
            Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
            System.arraycopy(array, 0, result, 0, index);
            if (index < length - 1) {
               System.arraycopy(array, index + 1, result, index, length - index - 1);
            }

            return result;
         } else {
            return array;
         }
      }
   }

   public static long[] removeEle(long[] array, long element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static int[] removeEle(int[] array, int element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static short[] removeEle(short[] array, short element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static char[] removeEle(char[] array, char element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static byte[] removeEle(byte[] array, byte element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static double[] removeEle(double[] array, double element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static float[] removeEle(float[] array, float element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static boolean[] removeEle(boolean[] array, boolean element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static long[] reverse(long[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static long[] reverse(long[] array) {
      return reverse((long[])array, 0, array.length);
   }

   public static int[] reverse(int[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static int[] reverse(int[] array) {
      return reverse((int[])array, 0, array.length);
   }

   public static short[] reverse(short[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static short[] reverse(short[] array) {
      return reverse((short[])array, 0, array.length);
   }

   public static char[] reverse(char[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static char[] reverse(char[] array) {
      return reverse((char[])array, 0, array.length);
   }

   public static byte[] reverse(byte[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static byte[] reverse(byte[] array) {
      return reverse((byte[])array, 0, array.length);
   }

   public static double[] reverse(double[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static double[] reverse(double[] array) {
      return reverse((double[])array, 0, array.length);
   }

   public static float[] reverse(float[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static float[] reverse(float[] array) {
      return reverse((float[])array, 0, array.length);
   }

   public static boolean[] reverse(boolean[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            swap(array, i, j);
            --j;
         }

         return array;
      }
   }

   public static boolean[] reverse(boolean[] array) {
      return reverse((boolean[])array, 0, array.length);
   }

   public static long min(long... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         long min = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (min > numberArray[i]) {
               min = numberArray[i];
            }
         }

         return min;
      }
   }

   public static int min(int... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         int min = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (min > numberArray[i]) {
               min = numberArray[i];
            }
         }

         return min;
      }
   }

   public static short min(short... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         short min = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (min > numberArray[i]) {
               min = numberArray[i];
            }
         }

         return min;
      }
   }

   public static char min(char... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         char min = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (min > numberArray[i]) {
               min = numberArray[i];
            }
         }

         return min;
      }
   }

   public static byte min(byte... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         byte min = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (min > numberArray[i]) {
               min = numberArray[i];
            }
         }

         return min;
      }
   }

   public static double min(double... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         double min = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (min > numberArray[i]) {
               min = numberArray[i];
            }
         }

         return min;
      }
   }

   public static float min(float... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         float min = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (min > numberArray[i]) {
               min = numberArray[i];
            }
         }

         return min;
      }
   }

   public static long max(long... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         long max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (max < numberArray[i]) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static int max(int... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         int max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (max < numberArray[i]) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static short max(short... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         short max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (max < numberArray[i]) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static char max(char... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         char max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (max < numberArray[i]) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static byte max(byte... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         byte max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (max < numberArray[i]) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static double max(double... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         double max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (max < numberArray[i]) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static float max(float... numberArray) {
      if (isEmpty(numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         float max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (max < numberArray[i]) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static int[] shuffle(int[] array) {
      return shuffle((int[])array, RandomUtil.getRandom());
   }

   public static int[] shuffle(int[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static long[] shuffle(long[] array) {
      return shuffle((long[])array, RandomUtil.getRandom());
   }

   public static long[] shuffle(long[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static double[] shuffle(double[] array) {
      return shuffle((double[])array, RandomUtil.getRandom());
   }

   public static double[] shuffle(double[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static float[] shuffle(float[] array) {
      return shuffle((float[])array, RandomUtil.getRandom());
   }

   public static float[] shuffle(float[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static boolean[] shuffle(boolean[] array) {
      return shuffle((boolean[])array, RandomUtil.getRandom());
   }

   public static boolean[] shuffle(boolean[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static byte[] shuffle(byte[] array) {
      return shuffle((byte[])array, RandomUtil.getRandom());
   }

   public static byte[] shuffle(byte[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static char[] shuffle(char[] array) {
      return shuffle((char[])array, RandomUtil.getRandom());
   }

   public static char[] shuffle(char[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static short[] shuffle(short[] array) {
      return shuffle((short[])array, RandomUtil.getRandom());
   }

   public static short[] shuffle(short[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static int[] swap(int[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         int tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static long[] swap(long[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         long tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static double[] swap(double[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         double tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static float[] swap(float[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         float tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static boolean[] swap(boolean[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         boolean tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static byte[] swap(byte[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         byte tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static char[] swap(char[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         char tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static short[] swap(short[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         short tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static boolean isSorted(byte[] array) {
      return isSortedASC(array);
   }

   public static boolean isSortedASC(byte[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] > array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSortedDESC(byte[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] < array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSorted(short[] array) {
      return isSortedASC(array);
   }

   public static boolean isSortedASC(short[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] > array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSortedDESC(short[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] < array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSorted(char[] array) {
      return isSortedASC(array);
   }

   public static boolean isSortedASC(char[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] > array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSortedDESC(char[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] < array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSorted(int[] array) {
      return isSortedASC(array);
   }

   public static boolean isSortedASC(int[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] > array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSortedDESC(int[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] < array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSorted(long[] array) {
      return isSortedASC(array);
   }

   public static boolean isSortedASC(long[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] > array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSortedDESC(long[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] < array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSorted(double[] array) {
      return isSortedASC(array);
   }

   public static boolean isSortedASC(double[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] > array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSortedDESC(double[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] < array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSorted(float[] array) {
      return isSortedASC(array);
   }

   public static boolean isSortedASC(float[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] > array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSortedDESC(float[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i] < array[i + 1]) {
               return false;
            }
         }

         return true;
      }
   }
}
