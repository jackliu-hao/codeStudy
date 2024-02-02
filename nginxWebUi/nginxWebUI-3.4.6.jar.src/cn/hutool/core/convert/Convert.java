/*      */ package cn.hutool.core.convert;
/*      */ 
/*      */ import cn.hutool.core.convert.impl.CollectionConverter;
/*      */ import cn.hutool.core.convert.impl.EnumConverter;
/*      */ import cn.hutool.core.convert.impl.MapConverter;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.lang.TypeReference;
/*      */ import cn.hutool.core.text.UnicodeUtil;
/*      */ import cn.hutool.core.util.ByteUtil;
/*      */ import cn.hutool.core.util.CharsetUtil;
/*      */ import cn.hutool.core.util.ClassUtil;
/*      */ import cn.hutool.core.util.HexUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import java.lang.reflect.Type;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.nio.charset.Charset;
/*      */ import java.time.Instant;
/*      */ import java.time.LocalDateTime;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Convert
/*      */ {
/*      */   public static String toStr(Object value, String defaultValue) {
/*   50 */     return convertQuietly(String.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(Object value) {
/*   62 */     return toStr(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] toStrArray(Object value) {
/*   73 */     return convert((Class)String[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Character toChar(Object value, Character defaultValue) {
/*   86 */     return convertQuietly(Character.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Character toChar(Object value) {
/*   98 */     return toChar(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Character[] toCharArray(Object value) {
/*  109 */     return convert((Class)Character[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Byte toByte(Object value, Byte defaultValue) {
/*  122 */     return convertQuietly(Byte.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Byte toByte(Object value) {
/*  134 */     return toByte(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Byte[] toByteArray(Object value) {
/*  145 */     return convert((Class)Byte[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toPrimitiveByteArray(Object value) {
/*  156 */     return convert((Class)byte[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Short toShort(Object value, Short defaultValue) {
/*  169 */     return convertQuietly(Short.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Short toShort(Object value) {
/*  181 */     return toShort(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Short[] toShortArray(Object value) {
/*  192 */     return convert((Class)Short[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Number toNumber(Object value, Number defaultValue) {
/*  205 */     return convertQuietly(Number.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Number toNumber(Object value) {
/*  217 */     return toNumber(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Number[] toNumberArray(Object value) {
/*  228 */     return convert((Class)Number[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Integer toInt(Object value, Integer defaultValue) {
/*  241 */     return convertQuietly(Integer.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Integer toInt(Object value) {
/*  253 */     return toInt(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Integer[] toIntArray(Object value) {
/*  263 */     return convert((Class)Integer[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Long toLong(Object value, Long defaultValue) {
/*  276 */     return convertQuietly(Long.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Long toLong(Object value) {
/*  288 */     return toLong(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Long[] toLongArray(Object value) {
/*  298 */     return convert((Class)Long[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Double toDouble(Object value, Double defaultValue) {
/*  311 */     return convertQuietly(Double.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Double toDouble(Object value) {
/*  323 */     return toDouble(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Double[] toDoubleArray(Object value) {
/*  333 */     return convert((Class)Double[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Float toFloat(Object value, Float defaultValue) {
/*  346 */     return convertQuietly(Float.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Float toFloat(Object value) {
/*  358 */     return toFloat(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Float[] toFloatArray(Object value) {
/*  368 */     return convert((Class)Float[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Boolean toBool(Object value, Boolean defaultValue) {
/*  381 */     return convertQuietly(Boolean.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Boolean toBool(Object value) {
/*  393 */     return toBool(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Boolean[] toBooleanArray(Object value) {
/*  403 */     return convert((Class)Boolean[].class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
/*  416 */     return convertQuietly(BigInteger.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigInteger toBigInteger(Object value) {
/*  428 */     return toBigInteger(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
/*  441 */     return convertQuietly(BigDecimal.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BigDecimal toBigDecimal(Object value) {
/*  453 */     return toBigDecimal(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Date toDate(Object value, Date defaultValue) {
/*  467 */     return convertQuietly(Date.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LocalDateTime toLocalDateTime(Object value, LocalDateTime defaultValue) {
/*  481 */     return convertQuietly(LocalDateTime.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LocalDateTime toLocalDateTime(Object value) {
/*  493 */     return toLocalDateTime(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Date toInstant(Object value, Date defaultValue) {
/*  507 */     return convertQuietly(Instant.class, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Date toDate(Object value) {
/*  520 */     return toDate(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue) {
/*  535 */     return (E)(new EnumConverter(clazz)).convertQuietly(value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value) {
/*  548 */     return toEnum(clazz, value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Collection<?> toCollection(Class<?> collectionType, Class<?> elementType, Object value) {
/*  561 */     return (new CollectionConverter(collectionType, elementType)).convert(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<?> toList(Object value) {
/*  572 */     return convert(List.class, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> List<T> toList(Class<T> elementType, Object value) {
/*  586 */     return (List)toCollection(ArrayList.class, elementType, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> Set<T> toSet(Class<T> elementType, Object value) {
/*  600 */     return (Set)toCollection(HashSet.class, elementType, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object value) {
/*  616 */     return (Map<K, V>)(new MapConverter(HashMap.class, keyType, valueType)).convert(value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convertByClassName(String className, Object value) throws ConvertException {
/*  630 */     return convert(ClassUtil.loadClass(className), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convert(Class<T> type, Object value) throws ConvertException {
/*  644 */     return convert(type, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convert(TypeReference<T> reference, Object value) throws ConvertException {
/*  657 */     return convert(reference.getType(), value, (T)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convert(Type type, Object value) throws ConvertException {
/*  670 */     return convert(type, value, (T)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convert(Class<T> type, Object value, T defaultValue) throws ConvertException {
/*  685 */     return convert(type, value, defaultValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convert(Type type, Object value, T defaultValue) throws ConvertException {
/*  699 */     return convertWithCheck(type, value, defaultValue, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convertQuietly(Type type, Object value) {
/*  713 */     return convertQuietly(type, value, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convertQuietly(Type type, Object value, T defaultValue) {
/*  728 */     return convertWithCheck(type, value, defaultValue, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T convertWithCheck(Type type, Object value, T defaultValue, boolean quietly) {
/*  744 */     ConverterRegistry registry = ConverterRegistry.getInstance();
/*      */     try {
/*  746 */       return registry.convert(type, value, defaultValue);
/*  747 */     } catch (Exception e) {
/*  748 */       if (quietly) {
/*  749 */         return defaultValue;
/*      */       }
/*  751 */       throw e;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toSBC(String input) {
/*  763 */     return toSBC(input, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toSBC(String input, Set<Character> notConvertSet) {
/*  774 */     if (StrUtil.isEmpty(input)) {
/*  775 */       return input;
/*      */     }
/*  777 */     char[] c = input.toCharArray();
/*  778 */     for (int i = 0; i < c.length; i++) {
/*  779 */       if (null == notConvertSet || !notConvertSet.contains(Character.valueOf(c[i])))
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*  784 */         if (c[i] == ' ') {
/*  785 */           c[i] = '　';
/*  786 */         } else if (c[i] < '') {
/*  787 */           c[i] = (char)(c[i] + 65248);
/*      */         }  } 
/*      */     } 
/*  790 */     return new String(c);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toDBC(String input) {
/*  800 */     return toDBC(input, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toDBC(String text, Set<Character> notConvertSet) {
/*  811 */     if (StrUtil.isBlank(text)) {
/*  812 */       return text;
/*      */     }
/*  814 */     char[] c = text.toCharArray();
/*  815 */     for (int i = 0; i < c.length; i++) {
/*  816 */       if (null == notConvertSet || !notConvertSet.contains(Character.valueOf(c[i])))
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*  821 */         if (c[i] == '　' || c[i] == ' ' || c[i] == ' ' || c[i] == ' ') {
/*      */           
/*  823 */           c[i] = ' ';
/*  824 */         } else if (c[i] > '＀' && c[i] < '｟') {
/*  825 */           c[i] = (char)(c[i] - 65248);
/*      */         } 
/*      */       }
/*      */     } 
/*  829 */     return new String(c);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toHex(String str, Charset charset) {
/*  842 */     return HexUtil.encodeHexStr(str, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toHex(byte[] bytes) {
/*  853 */     return HexUtil.encodeHexStr(bytes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] hexToBytes(String src) {
/*  864 */     return HexUtil.decodeHex(src.toCharArray());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String hexToStr(String hexStr, Charset charset) {
/*  877 */     return HexUtil.decodeHexStr(hexStr, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String strToUnicode(String strText) {
/*  888 */     return UnicodeUtil.toUnicode(strText);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unicodeToStr(String unicode) {
/*  899 */     return UnicodeUtil.toString(unicode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String convertCharset(String str, String sourceCharset, String destCharset) {
/*  913 */     if (StrUtil.hasBlank(new CharSequence[] { str, sourceCharset, destCharset })) {
/*  914 */       return str;
/*      */     }
/*      */     
/*  917 */     return CharsetUtil.convert(str, sourceCharset, destCharset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long convertTime(long sourceDuration, TimeUnit sourceUnit, TimeUnit destUnit) {
/*  929 */     Assert.notNull(sourceUnit, "sourceUnit is null !", new Object[0]);
/*  930 */     Assert.notNull(destUnit, "destUnit is null !", new Object[0]);
/*  931 */     return destUnit.convert(sourceDuration, sourceUnit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> wrap(Class<?> clazz) {
/*  944 */     return BasicType.wrap(clazz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> unWrap(Class<?> clazz) {
/*  956 */     return BasicType.unWrap(clazz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String numberToWord(Number number) {
/*  968 */     return NumberWordFormatter.format(number);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String numberToSimple(Number number) {
/*  983 */     return NumberWordFormatter.formatSimple(number.longValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String numberToChinese(double number, boolean isUseTraditional) {
/*  995 */     return NumberChineseFormatter.format(number, isUseTraditional);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int chineseToNumber(String number) {
/* 1010 */     return NumberChineseFormatter.chineseToNumber(number);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String digitToChinese(Number n) {
/* 1021 */     if (null == n) {
/* 1022 */       return "零";
/*      */     }
/* 1024 */     return NumberChineseFormatter.format(n.doubleValue(), true, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte intToByte(int intValue) {
/* 1036 */     return (byte)intValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int byteToUnsignedInt(byte byteValue) {
/* 1048 */     return byteValue & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static short bytesToShort(byte[] bytes) {
/* 1060 */     return ByteUtil.bytesToShort(bytes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] shortToBytes(short shortValue) {
/* 1072 */     return ByteUtil.shortToBytes(shortValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int bytesToInt(byte[] bytes) {
/* 1084 */     return ByteUtil.bytesToInt(bytes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] intToBytes(int intValue) {
/* 1096 */     return ByteUtil.intToBytes(intValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] longToBytes(long longValue) {
/* 1109 */     return ByteUtil.longToBytes(longValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long bytesToLong(byte[] bytes) {
/* 1122 */     return ByteUtil.bytesToLong(bytes);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\Convert.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */