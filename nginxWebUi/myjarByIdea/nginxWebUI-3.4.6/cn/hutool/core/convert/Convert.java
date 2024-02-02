package cn.hutool.core.convert;

import cn.hutool.core.convert.impl.CollectionConverter;
import cn.hutool.core.convert.impl.EnumConverter;
import cn.hutool.core.convert.impl.MapConverter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Convert {
   public static String toStr(Object value, String defaultValue) {
      return (String)convertQuietly(String.class, value, defaultValue);
   }

   public static String toStr(Object value) {
      return toStr(value, (String)null);
   }

   public static String[] toStrArray(Object value) {
      return (String[])convert(String[].class, value);
   }

   public static Character toChar(Object value, Character defaultValue) {
      return (Character)convertQuietly(Character.class, value, defaultValue);
   }

   public static Character toChar(Object value) {
      return toChar(value, (Character)null);
   }

   public static Character[] toCharArray(Object value) {
      return (Character[])convert(Character[].class, value);
   }

   public static Byte toByte(Object value, Byte defaultValue) {
      return (Byte)convertQuietly(Byte.class, value, defaultValue);
   }

   public static Byte toByte(Object value) {
      return toByte(value, (Byte)null);
   }

   public static Byte[] toByteArray(Object value) {
      return (Byte[])convert(Byte[].class, value);
   }

   public static byte[] toPrimitiveByteArray(Object value) {
      return (byte[])convert(byte[].class, value);
   }

   public static Short toShort(Object value, Short defaultValue) {
      return (Short)convertQuietly(Short.class, value, defaultValue);
   }

   public static Short toShort(Object value) {
      return toShort(value, (Short)null);
   }

   public static Short[] toShortArray(Object value) {
      return (Short[])convert(Short[].class, value);
   }

   public static Number toNumber(Object value, Number defaultValue) {
      return (Number)convertQuietly(Number.class, value, defaultValue);
   }

   public static Number toNumber(Object value) {
      return toNumber(value, (Number)null);
   }

   public static Number[] toNumberArray(Object value) {
      return (Number[])convert(Number[].class, value);
   }

   public static Integer toInt(Object value, Integer defaultValue) {
      return (Integer)convertQuietly(Integer.class, value, defaultValue);
   }

   public static Integer toInt(Object value) {
      return toInt(value, (Integer)null);
   }

   public static Integer[] toIntArray(Object value) {
      return (Integer[])convert(Integer[].class, value);
   }

   public static Long toLong(Object value, Long defaultValue) {
      return (Long)convertQuietly(Long.class, value, defaultValue);
   }

   public static Long toLong(Object value) {
      return toLong(value, (Long)null);
   }

   public static Long[] toLongArray(Object value) {
      return (Long[])convert(Long[].class, value);
   }

   public static Double toDouble(Object value, Double defaultValue) {
      return (Double)convertQuietly(Double.class, value, defaultValue);
   }

   public static Double toDouble(Object value) {
      return toDouble(value, (Double)null);
   }

   public static Double[] toDoubleArray(Object value) {
      return (Double[])convert(Double[].class, value);
   }

   public static Float toFloat(Object value, Float defaultValue) {
      return (Float)convertQuietly(Float.class, value, defaultValue);
   }

   public static Float toFloat(Object value) {
      return toFloat(value, (Float)null);
   }

   public static Float[] toFloatArray(Object value) {
      return (Float[])convert(Float[].class, value);
   }

   public static Boolean toBool(Object value, Boolean defaultValue) {
      return (Boolean)convertQuietly(Boolean.class, value, defaultValue);
   }

   public static Boolean toBool(Object value) {
      return toBool(value, (Boolean)null);
   }

   public static Boolean[] toBooleanArray(Object value) {
      return (Boolean[])convert(Boolean[].class, value);
   }

   public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
      return (BigInteger)convertQuietly(BigInteger.class, value, defaultValue);
   }

   public static BigInteger toBigInteger(Object value) {
      return toBigInteger(value, (BigInteger)null);
   }

   public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
      return (BigDecimal)convertQuietly(BigDecimal.class, value, defaultValue);
   }

   public static BigDecimal toBigDecimal(Object value) {
      return toBigDecimal(value, (BigDecimal)null);
   }

   public static Date toDate(Object value, Date defaultValue) {
      return (Date)convertQuietly(Date.class, value, defaultValue);
   }

   public static LocalDateTime toLocalDateTime(Object value, LocalDateTime defaultValue) {
      return (LocalDateTime)convertQuietly(LocalDateTime.class, value, defaultValue);
   }

   public static LocalDateTime toLocalDateTime(Object value) {
      return toLocalDateTime(value, (LocalDateTime)null);
   }

   public static Date toInstant(Object value, Date defaultValue) {
      return (Date)convertQuietly(Instant.class, value, defaultValue);
   }

   public static Date toDate(Object value) {
      return toDate(value, (Date)null);
   }

   public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue) {
      return (Enum)(new EnumConverter(clazz)).convertQuietly(value, defaultValue);
   }

   public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value) {
      return toEnum(clazz, value, (Enum)null);
   }

   public static Collection<?> toCollection(Class<?> collectionType, Class<?> elementType, Object value) {
      return (new CollectionConverter(collectionType, elementType)).convert(value, (Collection)null);
   }

   public static List<?> toList(Object value) {
      return (List)convert(List.class, value);
   }

   public static <T> List<T> toList(Class<T> elementType, Object value) {
      return (List)toCollection(ArrayList.class, elementType, value);
   }

   public static <T> Set<T> toSet(Class<T> elementType, Object value) {
      return (Set)toCollection(HashSet.class, elementType, value);
   }

   public static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object value) {
      return (Map)(new MapConverter(HashMap.class, keyType, valueType)).convert(value, (Object)null);
   }

   public static <T> T convertByClassName(String className, Object value) throws ConvertException {
      return convert(ClassUtil.loadClass(className), value);
   }

   public static <T> T convert(Class<T> type, Object value) throws ConvertException {
      return convert((Type)type, value);
   }

   public static <T> T convert(TypeReference<T> reference, Object value) throws ConvertException {
      return convert((Type)reference.getType(), value, (Object)null);
   }

   public static <T> T convert(Type type, Object value) throws ConvertException {
      return convert((Type)type, value, (Object)null);
   }

   public static <T> T convert(Class<T> type, Object value, T defaultValue) throws ConvertException {
      return convert((Type)type, value, defaultValue);
   }

   public static <T> T convert(Type type, Object value, T defaultValue) throws ConvertException {
      return convertWithCheck(type, value, defaultValue, false);
   }

   public static <T> T convertQuietly(Type type, Object value) {
      return convertQuietly(type, value, (Object)null);
   }

   public static <T> T convertQuietly(Type type, Object value, T defaultValue) {
      return convertWithCheck(type, value, defaultValue, true);
   }

   public static <T> T convertWithCheck(Type type, Object value, T defaultValue, boolean quietly) {
      ConverterRegistry registry = ConverterRegistry.getInstance();

      try {
         return registry.convert(type, value, defaultValue);
      } catch (Exception var6) {
         if (quietly) {
            return defaultValue;
         } else {
            throw var6;
         }
      }
   }

   public static String toSBC(String input) {
      return toSBC(input, (Set)null);
   }

   public static String toSBC(String input, Set<Character> notConvertSet) {
      if (StrUtil.isEmpty(input)) {
         return input;
      } else {
         char[] c = input.toCharArray();

         for(int i = 0; i < c.length; ++i) {
            if (null == notConvertSet || !notConvertSet.contains(c[i])) {
               if (c[i] == ' ') {
                  c[i] = 12288;
               } else if (c[i] < 127) {
                  c[i] += 'ﻠ';
               }
            }
         }

         return new String(c);
      }
   }

   public static String toDBC(String input) {
      return toDBC(input, (Set)null);
   }

   public static String toDBC(String text, Set<Character> notConvertSet) {
      if (StrUtil.isBlank(text)) {
         return text;
      } else {
         char[] c = text.toCharArray();

         for(int i = 0; i < c.length; ++i) {
            if (null == notConvertSet || !notConvertSet.contains(c[i])) {
               if (c[i] != 12288 && c[i] != 160 && c[i] != 8199 && c[i] != 8239) {
                  if (c[i] > '\uff00' && c[i] < '｟') {
                     c[i] -= 'ﻠ';
                  }
               } else {
                  c[i] = ' ';
               }
            }
         }

         return new String(c);
      }
   }

   public static String toHex(String str, Charset charset) {
      return HexUtil.encodeHexStr(str, charset);
   }

   public static String toHex(byte[] bytes) {
      return HexUtil.encodeHexStr(bytes);
   }

   public static byte[] hexToBytes(String src) {
      return HexUtil.decodeHex(src.toCharArray());
   }

   public static String hexToStr(String hexStr, Charset charset) {
      return HexUtil.decodeHexStr(hexStr, charset);
   }

   public static String strToUnicode(String strText) {
      return UnicodeUtil.toUnicode(strText);
   }

   public static String unicodeToStr(String unicode) {
      return UnicodeUtil.toString(unicode);
   }

   public static String convertCharset(String str, String sourceCharset, String destCharset) {
      return StrUtil.hasBlank(new CharSequence[]{str, sourceCharset, destCharset}) ? str : CharsetUtil.convert(str, sourceCharset, destCharset);
   }

   public static long convertTime(long sourceDuration, TimeUnit sourceUnit, TimeUnit destUnit) {
      Assert.notNull(sourceUnit, "sourceUnit is null !");
      Assert.notNull(destUnit, "destUnit is null !");
      return destUnit.convert(sourceDuration, sourceUnit);
   }

   public static Class<?> wrap(Class<?> clazz) {
      return BasicType.wrap(clazz);
   }

   public static Class<?> unWrap(Class<?> clazz) {
      return BasicType.unWrap(clazz);
   }

   public static String numberToWord(Number number) {
      return NumberWordFormatter.format((Object)number);
   }

   public static String numberToSimple(Number number) {
      return NumberWordFormatter.formatSimple(number.longValue());
   }

   public static String numberToChinese(double number, boolean isUseTraditional) {
      return NumberChineseFormatter.format(number, isUseTraditional);
   }

   public static int chineseToNumber(String number) {
      return NumberChineseFormatter.chineseToNumber(number);
   }

   public static String digitToChinese(Number n) {
      return null == n ? "零" : NumberChineseFormatter.format(n.doubleValue(), true, true);
   }

   public static byte intToByte(int intValue) {
      return (byte)intValue;
   }

   public static int byteToUnsignedInt(byte byteValue) {
      return byteValue & 255;
   }

   public static short bytesToShort(byte[] bytes) {
      return ByteUtil.bytesToShort(bytes);
   }

   public static byte[] shortToBytes(short shortValue) {
      return ByteUtil.shortToBytes(shortValue);
   }

   public static int bytesToInt(byte[] bytes) {
      return ByteUtil.bytesToInt(bytes);
   }

   public static byte[] intToBytes(int intValue) {
      return ByteUtil.intToBytes(intValue);
   }

   public static byte[] longToBytes(long longValue) {
      return ByteUtil.longToBytes(longValue);
   }

   public static long bytesToLong(byte[] bytes) {
      return ByteUtil.bytesToLong(bytes);
   }
}
