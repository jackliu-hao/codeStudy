package org.noear.solon.core.util;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import org.noear.solon.core.handle.Context;

public class ConvertUtil {
   public static Object to(AnnotatedElement element, Class<?> type, String key, String val, Context ctx) throws ClassCastException {
      if (String.class == type) {
         return val;
      } else if (val.length() == 0) {
         return null;
      } else {
         Object rst = null;
         if (rst == null && Date.class == type) {
            try {
               rst = DateAnalyzer.getGlobal().parse(val);
            } catch (ParseException var7) {
               throw new RuntimeException(var7);
            }
         }

         if (rst == null && type.isArray()) {
            String[] ary = null;
            if (ctx == null) {
               ary = val.split(",");
            } else {
               ary = ctx.paramValues(key);
               if (ary == null || ary.length == 1) {
                  ary = val.split(",");
               }
            }

            rst = tryToArray(ary, type);
         }

         if (rst == null) {
            rst = tryTo(type, val);
         }

         if (rst == null) {
            throw new ClassCastException("Unsupported type:" + type.getName());
         } else {
            return rst;
         }
      }
   }

   public static Object to(Class<?> type, String val) throws ClassCastException {
      if (String.class == type) {
         return val;
      } else if (val.length() == 0) {
         return null;
      } else {
         Object rst = tryTo(type, val);
         if (rst != null) {
            return rst;
         } else if (Date.class == type) {
            try {
               return DateAnalyzer.getGlobal().parse(val);
            } catch (RuntimeException var4) {
               throw var4;
            } catch (Throwable var5) {
               throw new RuntimeException(var5);
            }
         } else {
            throw new ClassCastException("不支持类型:" + type.getName());
         }
      }
   }

   private static Object tryToArray(String[] ary, Class<?> type) {
      int len = ary.length;
      if (is(String[].class, type)) {
         return ary;
      } else {
         int i;
         if (is(short[].class, type)) {
            short[] ary2 = new short[len];

            for(i = 0; i < len; ++i) {
               ary2[i] = Short.parseShort(ary[i]);
            }

            return ary2;
         } else if (is(int[].class, type)) {
            int[] ary2 = new int[len];

            for(i = 0; i < len; ++i) {
               ary2[i] = Integer.parseInt(ary[i]);
            }

            return ary2;
         } else if (is(long[].class, type)) {
            long[] ary2 = new long[len];

            for(i = 0; i < len; ++i) {
               ary2[i] = Long.parseLong(ary[i]);
            }

            return ary2;
         } else if (is(float[].class, type)) {
            float[] ary2 = new float[len];

            for(i = 0; i < len; ++i) {
               ary2[i] = Float.parseFloat(ary[i]);
            }

            return ary2;
         } else if (is(double[].class, type)) {
            double[] ary2 = new double[len];

            for(i = 0; i < len; ++i) {
               ary2[i] = Double.parseDouble(ary[i]);
            }

            return ary2;
         } else if (!is(Object[].class, type)) {
            return null;
         } else {
            Class<?> c = type.getComponentType();
            Object[] ary2 = (Object[])((Object[])Array.newInstance(c, len));

            for(int i = 0; i < len; ++i) {
               ary2[i] = tryTo(c, ary[i]);
            }

            return ary2;
         }
      }
   }

   public static Object tryTo(Class<?> type, String val) {
      if (Short.class != type && type != Short.TYPE) {
         if (Integer.class != type && type != Integer.TYPE) {
            if (Long.class != type && type != Long.TYPE) {
               if (Double.class != type && type != Double.TYPE) {
                  if (Float.class != type && type != Float.TYPE) {
                     if (Boolean.class != type && type != Boolean.TYPE) {
                        if (LocalDate.class == type) {
                           return LocalDate.parse(val);
                        } else if (LocalTime.class == type) {
                           return LocalTime.parse(val);
                        } else if (LocalDateTime.class == type) {
                           return LocalDateTime.parse(val);
                        } else if (BigDecimal.class == type) {
                           return new BigDecimal(val);
                        } else if (BigInteger.class == type) {
                           return new BigInteger(val);
                        } else {
                           return type.isEnum() ? enumOf(type, val) : null;
                        }
                     } else {
                        return "1".equals(val) ? true : Boolean.parseBoolean(val);
                     }
                  } else {
                     return Float.parseFloat(val);
                  }
               } else {
                  return Double.parseDouble(val);
               }
            } else {
               return Long.parseLong(val);
            }
         } else {
            return Integer.parseInt(val);
         }
      } else {
         return Short.parseShort(val);
      }
   }

   private static <T extends Enum<T>> T enumOf(Class<T> enumType, String name) {
      Enum[] var2 = (Enum[])enumType.getEnumConstants();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         T each = var2[var4];
         if (each.name().compareToIgnoreCase(name) == 0) {
            return each;
         }
      }

      return null;
   }

   private static boolean is(Class<?> s, Class<?> t) {
      return s.isAssignableFrom(t);
   }
}
