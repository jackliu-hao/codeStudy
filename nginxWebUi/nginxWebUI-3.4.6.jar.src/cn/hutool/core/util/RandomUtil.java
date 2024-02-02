/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.date.DateField;
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.lang.WeightRandom;
/*     */ import java.awt.Color;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ThreadLocalRandom;
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
/*     */ public class RandomUtil
/*     */ {
/*     */   public static final String BASE_NUMBER = "0123456789";
/*     */   public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
/*     */   public static final String BASE_CHAR_NUMBER = "abcdefghijklmnopqrstuvwxyz0123456789";
/*     */   
/*     */   public static ThreadLocalRandom getRandom() {
/*  59 */     return ThreadLocalRandom.current();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SecureRandom createSecureRandom(byte[] seed) {
/*  70 */     return (null == seed) ? new SecureRandom() : new SecureRandom(seed);
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
/*     */   public static SecureRandom getSecureRandom() {
/*  84 */     return getSecureRandom(null);
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
/*     */   public static SecureRandom getSecureRandom(byte[] seed) {
/* 100 */     return createSecureRandom(seed);
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
/*     */   public static SecureRandom getSHA1PRNGRandom(byte[] seed) {
/*     */     SecureRandom random;
/*     */     try {
/* 118 */       random = SecureRandom.getInstance("SHA1PRNG");
/* 119 */     } catch (NoSuchAlgorithmException e) {
/* 120 */       throw new UtilException(e);
/*     */     } 
/* 122 */     if (null != seed) {
/* 123 */       random.setSeed(seed);
/*     */     }
/* 125 */     return random;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SecureRandom getSecureRandomStrong() {
/*     */     try {
/* 137 */       return SecureRandom.getInstanceStrong();
/* 138 */     } catch (NoSuchAlgorithmException e) {
/* 139 */       throw new UtilException(e);
/*     */     } 
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
/*     */   public static Random getRandom(boolean isSecure) {
/* 153 */     return isSecure ? getSecureRandom() : getRandom();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean randomBoolean() {
/* 163 */     return (0 == randomInt(2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char randomChinese() {
/* 173 */     return (char)randomInt(19968, 40959);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int randomInt(int min, int max) {
/* 184 */     return getRandom().nextInt(min, max);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int randomInt() {
/* 194 */     return getRandom().nextInt();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int randomInt(int limit) {
/* 205 */     return getRandom().nextInt(limit);
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
/*     */   public static long randomLong(long min, long max) {
/* 218 */     return getRandom().nextLong(min, max);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long randomLong() {
/* 229 */     return getRandom().nextLong();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long randomLong(long limit) {
/* 240 */     return getRandom().nextLong(limit);
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
/*     */   public static double randomDouble(double min, double max) {
/* 253 */     return getRandom().nextDouble(min, max);
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
/*     */   public static double randomDouble(double min, double max, int scale, RoundingMode roundingMode) {
/* 267 */     return NumberUtil.round(randomDouble(min, max), scale, roundingMode).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double randomDouble() {
/* 278 */     return getRandom().nextDouble();
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
/*     */   public static double randomDouble(int scale, RoundingMode roundingMode) {
/* 290 */     return NumberUtil.round(randomDouble(), scale, roundingMode).doubleValue();
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
/*     */   public static double randomDouble(double limit) {
/* 302 */     return getRandom().nextDouble(limit);
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
/*     */   public static double randomDouble(double limit, int scale, RoundingMode roundingMode) {
/* 315 */     return NumberUtil.round(randomDouble(limit), scale, roundingMode).doubleValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigDecimal randomBigDecimal() {
/* 325 */     return NumberUtil.toBigDecimal(Double.valueOf(getRandom().nextDouble()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigDecimal randomBigDecimal(BigDecimal limit) {
/* 336 */     return NumberUtil.toBigDecimal(Double.valueOf(getRandom().nextDouble(limit.doubleValue())));
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
/*     */   public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max) {
/* 348 */     return NumberUtil.toBigDecimal(Double.valueOf(getRandom().nextDouble(min.doubleValue(), max.doubleValue())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] randomBytes(int length) {
/* 358 */     byte[] bytes = new byte[length];
/* 359 */     getRandom().nextBytes(bytes);
/* 360 */     return bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T randomEle(List<T> list) {
/* 371 */     return randomEle(list, list.size());
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
/*     */   public static <T> T randomEle(List<T> list, int limit) {
/* 383 */     if (list.size() < limit) {
/* 384 */       limit = list.size();
/*     */     }
/* 386 */     return list.get(randomInt(limit));
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
/*     */   public static <T> T randomEle(T[] array) {
/* 398 */     return randomEle(array, array.length);
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
/*     */   public static <T> T randomEle(T[] array, int limit) {
/* 411 */     if (array.length < limit) {
/* 412 */       limit = array.length;
/*     */     }
/* 414 */     return array[randomInt(limit)];
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
/*     */   public static <T> List<T> randomEles(List<T> list, int count) {
/* 426 */     List<T> result = new ArrayList<>(count);
/* 427 */     int limit = list.size();
/* 428 */     while (result.size() < count) {
/* 429 */       result.add(randomEle(list, limit));
/*     */     }
/*     */     
/* 432 */     return result;
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
/*     */   public static <T> List<T> randomEleList(List<T> source, int count) {
/* 446 */     if (count >= source.size()) {
/* 447 */       return ListUtil.toList(source);
/*     */     }
/* 449 */     int[] randomList = ArrayUtil.sub(randomInts(source.size()), 0, count);
/* 450 */     List<T> result = new ArrayList<>();
/* 451 */     for (int e : randomList) {
/* 452 */       result.add(source.get(e));
/*     */     }
/* 454 */     return result;
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
/*     */   public static <T> Set<T> randomEleSet(Collection<T> collection, int count) {
/* 467 */     ArrayList<T> source = CollUtil.distinct(collection);
/* 468 */     if (count > source.size()) {
/* 469 */       throw new IllegalArgumentException("Count is larger than collection distinct size !");
/*     */     }
/*     */     
/* 472 */     Set<T> result = new LinkedHashSet<>(count);
/* 473 */     int limit = source.size();
/* 474 */     while (result.size() < count) {
/* 475 */       result.add(randomEle(source, limit));
/*     */     }
/*     */     
/* 478 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] randomInts(int length) {
/* 489 */     int[] range = ArrayUtil.range(length);
/* 490 */     for (int i = 0; i < length; i++) {
/* 491 */       int random = randomInt(i, length);
/* 492 */       ArrayUtil.swap(range, i, random);
/*     */     } 
/* 494 */     return range;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String randomString(int length) {
/* 504 */     return randomString("abcdefghijklmnopqrstuvwxyz0123456789", length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String randomStringUpper(int length) {
/* 515 */     return randomString("abcdefghijklmnopqrstuvwxyz0123456789", length).toUpperCase();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String randomStringWithoutStr(int length, String elemData) {
/* 526 */     String baseStr = "abcdefghijklmnopqrstuvwxyz0123456789";
/* 527 */     baseStr = StrUtil.removeAll(baseStr, elemData.toLowerCase().toCharArray());
/* 528 */     return randomString(baseStr, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String randomNumbers(int length) {
/* 538 */     return randomString("0123456789", length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String randomString(String baseString, int length) {
/* 549 */     if (StrUtil.isEmpty(baseString)) {
/* 550 */       return "";
/*     */     }
/* 552 */     StringBuilder sb = new StringBuilder(length);
/*     */     
/* 554 */     if (length < 1) {
/* 555 */       length = 1;
/*     */     }
/* 557 */     int baseLength = baseString.length();
/* 558 */     for (int i = 0; i < length; i++) {
/* 559 */       int number = randomInt(baseLength);
/* 560 */       sb.append(baseString.charAt(number));
/*     */     } 
/* 562 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char randomNumber() {
/* 572 */     return randomChar("0123456789");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char randomChar() {
/* 582 */     return randomChar("abcdefghijklmnopqrstuvwxyz0123456789");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char randomChar(String baseString) {
/* 593 */     return baseString.charAt(randomInt(baseString.length()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Color randomColor() {
/* 605 */     Random random = getRandom();
/* 606 */     return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
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
/*     */   public static <T> WeightRandom<T> weightRandom(WeightRandom.WeightObj<T>[] weightObjs) {
/* 618 */     return new WeightRandom((WeightRandom.WeightObj[])weightObjs);
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
/*     */   public static <T> WeightRandom<T> weightRandom(Iterable<WeightRandom.WeightObj<T>> weightObjs) {
/* 630 */     return new WeightRandom(weightObjs);
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
/*     */   public static DateTime randomDay(int min, int max) {
/* 642 */     return randomDate((Date)DateUtil.date(), DateField.DAY_OF_YEAR, min, max);
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
/*     */   public static DateTime randomDate(Date baseDate, DateField dateField, int min, int max) {
/*     */     DateTime dateTime;
/* 656 */     if (null == baseDate) {
/* 657 */       dateTime = DateUtil.date();
/*     */     }
/*     */     
/* 660 */     return DateUtil.offset((Date)dateTime, dateField, randomInt(min, max));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\RandomUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */