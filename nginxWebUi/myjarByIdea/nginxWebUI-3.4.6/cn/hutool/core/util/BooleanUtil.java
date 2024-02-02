package cn.hutool.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import java.util.Set;

public class BooleanUtil {
   private static final Set<String> TRUE_SET = CollUtil.newHashSet((Object[])("true", "yes", "y", "t", "ok", "1", "on", "是", "对", "真", "對", "√"));
   private static final Set<String> FALSE_SET = CollUtil.newHashSet((Object[])("false", "no", "n", "f", "0", "off", "否", "错", "假", "錯", "×"));

   public static Boolean negate(Boolean bool) {
      if (bool == null) {
         return null;
      } else {
         return bool ? Boolean.FALSE : Boolean.TRUE;
      }
   }

   public static boolean isTrue(Boolean bool) {
      return Boolean.TRUE.equals(bool);
   }

   public static boolean isFalse(Boolean bool) {
      return Boolean.FALSE.equals(bool);
   }

   public static boolean negate(boolean bool) {
      return !bool;
   }

   public static boolean toBoolean(String valueStr) {
      if (StrUtil.isNotBlank(valueStr)) {
         valueStr = valueStr.trim().toLowerCase();
         return TRUE_SET.contains(valueStr);
      } else {
         return false;
      }
   }

   public static Boolean toBooleanObject(String valueStr) {
      if (StrUtil.isNotBlank(valueStr)) {
         valueStr = valueStr.trim().toLowerCase();
         if (TRUE_SET.contains(valueStr)) {
            return true;
         }

         if (FALSE_SET.contains(valueStr)) {
            return false;
         }
      }

      return null;
   }

   public static int toInt(boolean value) {
      return value ? 1 : 0;
   }

   public static Integer toInteger(boolean value) {
      return toInt(value);
   }

   public static char toChar(boolean value) {
      return (char)toInt(value);
   }

   public static Character toCharacter(boolean value) {
      return toChar(value);
   }

   public static byte toByte(boolean value) {
      return (byte)toInt(value);
   }

   public static Byte toByteObj(boolean value) {
      return toByte(value);
   }

   public static long toLong(boolean value) {
      return (long)toInt(value);
   }

   public static Long toLongObj(boolean value) {
      return toLong(value);
   }

   public static short toShort(boolean value) {
      return (short)toInt(value);
   }

   public static Short toShortObj(boolean value) {
      return toShort(value);
   }

   public static float toFloat(boolean value) {
      return (float)toInt(value);
   }

   public static Float toFloatObj(boolean value) {
      return toFloat(value);
   }

   public static double toDouble(boolean value) {
      return (double)toInt(value);
   }

   public static Double toDoubleObj(boolean value) {
      return toDouble(value);
   }

   public static String toStringTrueFalse(boolean bool) {
      return toString(bool, "true", "false");
   }

   public static String toStringOnOff(boolean bool) {
      return toString(bool, "on", "off");
   }

   public static String toStringYesNo(boolean bool) {
      return toString(bool, "yes", "no");
   }

   public static String toString(boolean bool, String trueString, String falseString) {
      return bool ? trueString : falseString;
   }

   public static boolean and(boolean... array) {
      if (ArrayUtil.isEmpty((boolean[])array)) {
         throw new IllegalArgumentException("The Array must not be empty !");
      } else {
         boolean[] var1 = array;
         int var2 = array.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            boolean element = var1[var3];
            if (!element) {
               return false;
            }
         }

         return true;
      }
   }

   public static Boolean andOfWrap(Boolean... array) {
      if (ArrayUtil.isEmpty((Object[])array)) {
         throw new IllegalArgumentException("The Array must not be empty !");
      } else {
         boolean[] primitive = (boolean[])Convert.convert((Class)boolean[].class, array);
         return and(primitive);
      }
   }

   public static boolean or(boolean... array) {
      if (ArrayUtil.isEmpty((boolean[])array)) {
         throw new IllegalArgumentException("The Array must not be empty !");
      } else {
         boolean[] var1 = array;
         int var2 = array.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            boolean element = var1[var3];
            if (element) {
               return true;
            }
         }

         return false;
      }
   }

   public static Boolean orOfWrap(Boolean... array) {
      if (ArrayUtil.isEmpty((Object[])array)) {
         throw new IllegalArgumentException("The Array must not be empty !");
      } else {
         boolean[] primitive = (boolean[])Convert.convert((Class)boolean[].class, array);
         return or(primitive);
      }
   }

   public static boolean xor(boolean... array) {
      if (ArrayUtil.isEmpty((boolean[])array)) {
         throw new IllegalArgumentException("The Array must not be empty");
      } else {
         boolean result = false;
         boolean[] var2 = array;
         int var3 = array.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            boolean element = var2[var4];
            result ^= element;
         }

         return result;
      }
   }

   public static Boolean xorOfWrap(Boolean... array) {
      if (ArrayUtil.isEmpty((Object[])array)) {
         throw new IllegalArgumentException("The Array must not be empty !");
      } else {
         boolean[] primitive = (boolean[])Convert.convert((Class)boolean[].class, array);
         return xor(primitive);
      }
   }

   public static boolean isBoolean(Class<?> clazz) {
      return clazz == Boolean.class || clazz == Boolean.TYPE;
   }
}
