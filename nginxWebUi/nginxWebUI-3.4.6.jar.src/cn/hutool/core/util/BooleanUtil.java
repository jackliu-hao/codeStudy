/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BooleanUtil
/*     */ {
/*  17 */   private static final Set<String> TRUE_SET = CollUtil.newHashSet((Object[])new String[] { "true", "yes", "y", "t", "ok", "1", "on", "是", "对", "真", "對", "√" });
/*     */   
/*  19 */   private static final Set<String> FALSE_SET = CollUtil.newHashSet((Object[])new String[] { "false", "no", "n", "f", "0", "off", "否", "错", "假", "錯", "×" });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Boolean negate(Boolean bool) {
/*  28 */     if (bool == null) {
/*  29 */       return null;
/*     */     }
/*  31 */     return bool.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isTrue(Boolean bool) {
/*  47 */     return Boolean.TRUE.equals(bool);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isFalse(Boolean bool) {
/*  63 */     return Boolean.FALSE.equals(bool);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean negate(boolean bool) {
/*  73 */     return !bool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean toBoolean(String valueStr) {
/*  83 */     if (StrUtil.isNotBlank(valueStr)) {
/*  84 */       valueStr = valueStr.trim().toLowerCase();
/*  85 */       return TRUE_SET.contains(valueStr);
/*     */     } 
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Boolean toBooleanObject(String valueStr) {
/* 101 */     if (StrUtil.isNotBlank(valueStr)) {
/* 102 */       valueStr = valueStr.trim().toLowerCase();
/* 103 */       if (TRUE_SET.contains(valueStr))
/* 104 */         return Boolean.valueOf(true); 
/* 105 */       if (FALSE_SET.contains(valueStr)) {
/* 106 */         return Boolean.valueOf(false);
/*     */       }
/*     */     } 
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int toInt(boolean value) {
/* 119 */     return value ? 1 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Integer toInteger(boolean value) {
/* 129 */     return Integer.valueOf(toInt(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char toChar(boolean value) {
/* 139 */     return (char)toInt(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Character toCharacter(boolean value) {
/* 149 */     return Character.valueOf(toChar(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte toByte(boolean value) {
/* 159 */     return (byte)toInt(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Byte toByteObj(boolean value) {
/* 169 */     return Byte.valueOf(toByte(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long toLong(boolean value) {
/* 179 */     return toInt(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Long toLongObj(boolean value) {
/* 189 */     return Long.valueOf(toLong(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short toShort(boolean value) {
/* 199 */     return (short)toInt(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Short toShortObj(boolean value) {
/* 209 */     return Short.valueOf(toShort(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float toFloat(boolean value) {
/* 219 */     return toInt(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Float toFloatObj(boolean value) {
/* 229 */     return Float.valueOf(toFloat(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double toDouble(boolean value) {
/* 239 */     return toInt(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Double toDoubleObj(boolean value) {
/* 249 */     return Double.valueOf(toDouble(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toStringTrueFalse(boolean bool) {
/* 264 */     return toString(bool, "true", "false");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toStringOnOff(boolean bool) {
/* 279 */     return toString(bool, "on", "off");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toStringYesNo(boolean bool) {
/* 294 */     return toString(bool, "yes", "no");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(boolean bool, String trueString, String falseString) {
/* 311 */     return bool ? trueString : falseString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean and(boolean... array) {
/* 329 */     if (ArrayUtil.isEmpty(array)) {
/* 330 */       throw new IllegalArgumentException("The Array must not be empty !");
/*     */     }
/* 332 */     for (boolean element : array) {
/* 333 */       if (false == element) {
/* 334 */         return false;
/*     */       }
/*     */     } 
/* 337 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Boolean andOfWrap(Boolean... array) {
/* 356 */     if (ArrayUtil.isEmpty(array)) {
/* 357 */       throw new IllegalArgumentException("The Array must not be empty !");
/*     */     }
/* 359 */     boolean[] primitive = (boolean[])Convert.convert(boolean[].class, array);
/* 360 */     return Boolean.valueOf(and(primitive));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean or(boolean... array) {
/* 379 */     if (ArrayUtil.isEmpty(array)) {
/* 380 */       throw new IllegalArgumentException("The Array must not be empty !");
/*     */     }
/* 382 */     for (boolean element : array) {
/* 383 */       if (element) {
/* 384 */         return true;
/*     */       }
/*     */     } 
/* 387 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Boolean orOfWrap(Boolean... array) {
/* 407 */     if (ArrayUtil.isEmpty(array)) {
/* 408 */       throw new IllegalArgumentException("The Array must not be empty !");
/*     */     }
/* 410 */     boolean[] primitive = (boolean[])Convert.convert(boolean[].class, array);
/* 411 */     return Boolean.valueOf(or(primitive));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean xor(boolean... array) {
/* 430 */     if (ArrayUtil.isEmpty(array)) {
/* 431 */       throw new IllegalArgumentException("The Array must not be empty");
/*     */     }
/*     */     
/* 434 */     boolean result = false;
/* 435 */     for (boolean element : array) {
/* 436 */       result ^= element;
/*     */     }
/*     */     
/* 439 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Boolean xorOfWrap(Boolean... array) {
/* 455 */     if (ArrayUtil.isEmpty(array)) {
/* 456 */       throw new IllegalArgumentException("The Array must not be empty !");
/*     */     }
/* 458 */     boolean[] primitive = (boolean[])Convert.convert(boolean[].class, array);
/* 459 */     return Boolean.valueOf(xor(primitive));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBoolean(Class<?> clazz) {
/* 470 */     return (clazz == Boolean.class || clazz == boolean.class);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\BooleanUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */