/*     */ package com.mysql.cj.util;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.time.Duration;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeFormatterBuilder;
/*     */ import java.time.temporal.ChronoField;
/*     */ import java.util.Calendar;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Pattern;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimeUtil
/*     */ {
/*  62 */   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
/*     */   
/*  64 */   public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
/*  65 */   public static final DateTimeFormatter TIME_FORMATTER_NO_FRACT_NO_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss");
/*  66 */   public static final DateTimeFormatter TIME_FORMATTER_WITH_NANOS_NO_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS");
/*  67 */   public static final DateTimeFormatter TIME_FORMATTER_NO_FRACT_WITH_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ssXXX");
/*  68 */   public static final DateTimeFormatter TIME_FORMATTER_WITH_NANOS_WITH_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSSXXX");
/*  69 */   public static final DateTimeFormatter DATETIME_FORMATTER_NO_FRACT_NO_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
/*  70 */   public static final DateTimeFormatter DATETIME_FORMATTER_WITH_MILLIS_NO_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
/*  71 */   public static final DateTimeFormatter DATETIME_FORMATTER_WITH_NANOS_NO_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
/*  72 */   public static final DateTimeFormatter DATETIME_FORMATTER_NO_FRACT_WITH_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
/*  73 */   public static final DateTimeFormatter DATETIME_FORMATTER_WITH_NANOS_WITH_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSXXX");
/*     */ 
/*     */   
/*  76 */   public static final Pattern DATE_LITERAL_WITH_DELIMITERS = Pattern.compile("(\\d{4}|\\d{2})[\\p{Punct}&&[^:]](([0])?[1-9]|[1][0-2])[\\p{Punct}&&[^:]](([0])?[1-9]|[1-2]\\d|[3][0-1])");
/*  77 */   public static final Pattern DATE_LITERAL_NO_DELIMITERS = Pattern.compile("(\\d{4}|\\d{2})([0][1-9]|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])");
/*     */   
/*  79 */   public static final Pattern TIME_LITERAL_WITH_DELIMITERS = Pattern.compile("(([0-1])?\\d|[2][0-3]):([0-5])?\\d(:([0-5])?\\d(\\.\\d{1,9})?)?");
/*  80 */   public static final Pattern TIME_LITERAL_SHORT6 = Pattern.compile("([0-1]\\d|[2][0-3])([0-5]\\d){2}(\\.\\d{1,9})?");
/*  81 */   public static final Pattern TIME_LITERAL_SHORT4 = Pattern.compile("([0-5]\\d){2}(\\.\\d{1,9})?");
/*  82 */   public static final Pattern TIME_LITERAL_SHORT2 = Pattern.compile("[0-5]\\d(\\.\\d{1,9})?");
/*     */   
/*  84 */   public static final Pattern DATETIME_LITERAL_WITH_DELIMITERS = Pattern.compile("(\\d{4}|\\d{2})\\p{Punct}(([0])?[1-9]|[1][0-2])\\p{Punct}(([0])?[1-9]|[1-2]\\d|[3][0-1])[ T](([0-1])?\\d|[2][0-3])\\p{Punct}([0-5])?\\d(\\p{Punct}([0-5])?\\d(\\.\\d{1,9})?)?");
/*     */ 
/*     */   
/*  87 */   public static final Pattern DATETIME_LITERAL_SHORT14 = Pattern.compile("\\d{4}([0][1-9]|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])([0-1]\\d|[2][0-3])([0-5]\\d){2}(\\.\\d{1,9}){0,1}");
/*     */   
/*  89 */   public static final Pattern DATETIME_LITERAL_SHORT12 = Pattern.compile("\\d{2}([0][1-9]|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])([0-1]\\d|[2][0-3])([0-5]\\d){2}(\\.\\d{1,9}){0,1}");
/*     */ 
/*     */   
/*  92 */   public static final Pattern DURATION_LITERAL_WITH_DAYS = Pattern.compile("(-)?(([0-2])?\\d|[3][0-4]) (([0-1])?\\d|[2][0-3])(:([0-5])?\\d(:([0-5])?\\d(\\.\\d{1,9})?)?)?");
/*  93 */   public static final Pattern DURATION_LITERAL_NO_DAYS = Pattern.compile("(-)?\\d{1,3}:([0-5])?\\d(:([0-5])?\\d(\\.\\d{1,9})?)?");
/*     */ 
/*     */   
/*     */   private static final String TIME_ZONE_MAPPINGS_RESOURCE = "/com/mysql/cj/util/TimeZoneMapping.properties";
/*     */   
/*  98 */   private static Properties timeZoneMappings = null;
/*     */ 
/*     */   
/*     */   protected static final Method systemNanoTimeMethod;
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/* 106 */       aMethod = System.class.getMethod("nanoTime", (Class[])null);
/* 107 */     } catch (SecurityException e) {
/* 108 */       aMethod = null;
/* 109 */     } catch (NoSuchMethodException e) {
/* 110 */       aMethod = null;
/*     */     } 
/*     */     
/* 113 */     systemNanoTimeMethod = aMethod;
/*     */   } static {
/*     */     Method aMethod;
/*     */   } public static boolean nanoTimeAvailable() {
/* 117 */     return (systemNanoTimeMethod != null);
/*     */   }
/*     */   
/*     */   public static long getCurrentTimeNanosOrMillis() {
/* 121 */     if (systemNanoTimeMethod != null) {
/*     */       try {
/* 123 */         return ((Long)systemNanoTimeMethod.invoke(null, (Object[])null)).longValue();
/* 124 */       } catch (IllegalArgumentException illegalArgumentException) {
/*     */       
/* 126 */       } catch (IllegalAccessException illegalAccessException) {
/*     */       
/* 128 */       } catch (InvocationTargetException invocationTargetException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 133 */     return System.currentTimeMillis();
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
/*     */   public static String getCanonicalTimeZone(String timezoneStr, ExceptionInterceptor exceptionInterceptor) {
/* 147 */     if (timezoneStr == null) {
/* 148 */       return null;
/*     */     }
/*     */     
/* 151 */     timezoneStr = timezoneStr.trim();
/*     */ 
/*     */     
/* 154 */     if (timezoneStr.length() > 2 && (
/* 155 */       timezoneStr.charAt(0) == '+' || timezoneStr.charAt(0) == '-') && Character.isDigit(timezoneStr.charAt(1))) {
/* 156 */       return "GMT" + timezoneStr;
/*     */     }
/*     */ 
/*     */     
/* 160 */     synchronized (TimeUtil.class) {
/* 161 */       if (timeZoneMappings == null) {
/* 162 */         loadTimeZoneMappings(exceptionInterceptor);
/*     */       }
/*     */     } 
/*     */     
/*     */     String canonicalTz;
/* 167 */     if ((canonicalTz = timeZoneMappings.getProperty(timezoneStr)) != null) {
/* 168 */       return canonicalTz;
/*     */     }
/*     */     
/* 171 */     throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/* 172 */         Messages.getString("TimeUtil.UnrecognizedTimeZoneId", new Object[] { timezoneStr }), exceptionInterceptor);
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
/*     */   public static Timestamp adjustNanosPrecision(Timestamp ts, int fsp, boolean serverRoundFracSecs) {
/* 189 */     if (fsp < 0 || fsp > 6) {
/* 190 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
/*     */     }
/* 192 */     Timestamp res = (Timestamp)ts.clone();
/* 193 */     double tail = Math.pow(10.0D, (9 - fsp));
/* 194 */     int nanos = serverRoundFracSecs ? ((int)Math.round(res.getNanos() / tail) * (int)tail) : ((int)(res.getNanos() / tail) * (int)tail);
/* 195 */     if (nanos > 999999999) {
/* 196 */       nanos %= 1000000000;
/* 197 */       res.setTime(res.getTime() + 1000L);
/*     */     } 
/* 199 */     res.setNanos(nanos);
/* 200 */     return res;
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
/*     */   public static LocalDateTime adjustNanosPrecision(LocalDateTime x, int fsp, boolean serverRoundFracSecs) {
/* 217 */     if (fsp < 0 || fsp > 6) {
/* 218 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
/*     */     }
/* 220 */     int originalNano = x.getNano();
/* 221 */     double tail = Math.pow(10.0D, (9 - fsp));
/*     */     
/* 223 */     int adjustedNano = serverRoundFracSecs ? ((int)Math.round(originalNano / tail) * (int)tail) : ((int)(originalNano / tail) * (int)tail);
/* 224 */     if (adjustedNano > 999999999) {
/* 225 */       adjustedNano %= 1000000000;
/* 226 */       x = x.plusSeconds(1L);
/*     */     } 
/* 228 */     return x.withNano(adjustedNano);
/*     */   }
/*     */   
/*     */   public static LocalTime adjustNanosPrecision(LocalTime x, int fsp, boolean serverRoundFracSecs) {
/* 232 */     if (fsp < 0 || fsp > 6) {
/* 233 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
/*     */     }
/* 235 */     int originalNano = x.getNano();
/* 236 */     double tail = Math.pow(10.0D, (9 - fsp));
/*     */     
/* 238 */     int adjustedNano = serverRoundFracSecs ? ((int)Math.round(originalNano / tail) * (int)tail) : ((int)(originalNano / tail) * (int)tail);
/* 239 */     if (adjustedNano > 999999999) {
/* 240 */       adjustedNano %= 1000000000;
/* 241 */       x = x.plusSeconds(1L);
/*     */     } 
/* 243 */     return x.withNano(adjustedNano);
/*     */   }
/*     */   
/*     */   public static Duration adjustNanosPrecision(Duration x, int fsp, boolean serverRoundFracSecs) {
/* 247 */     if (fsp < 0 || fsp > 6) {
/* 248 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
/*     */     }
/* 250 */     int originalNano = x.getNano();
/* 251 */     double tail = Math.pow(10.0D, (9 - fsp));
/*     */     
/* 253 */     int adjustedNano = serverRoundFracSecs ? ((int)Math.round(originalNano / tail) * (int)tail) : ((int)(originalNano / tail) * (int)tail);
/* 254 */     if (adjustedNano > 999999999) {
/* 255 */       adjustedNano %= 1000000000;
/* 256 */       x = x.plusSeconds(1L);
/*     */     } 
/* 258 */     return x.withNanos(adjustedNano);
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
/*     */   public static String formatNanos(int nanos, int fsp) {
/* 272 */     return formatNanos(nanos, fsp, true);
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
/*     */   public static String formatNanos(int nanos, int fsp, boolean truncateTrailingZeros) {
/* 288 */     if (nanos < 0 || nanos > 999999999) {
/* 289 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "nanos value must be in 0 to 999999999 range but was " + nanos);
/*     */     }
/* 291 */     if (fsp < 0 || fsp > 6) {
/* 292 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range but was " + fsp);
/*     */     }
/*     */     
/* 295 */     if (fsp == 0 || nanos == 0) {
/* 296 */       return "0";
/*     */     }
/*     */ 
/*     */     
/* 300 */     nanos = (int)(nanos / Math.pow(10.0D, (9 - fsp)));
/* 301 */     if (nanos == 0) {
/* 302 */       return "0";
/*     */     }
/*     */     
/* 305 */     String nanosString = Integer.toString(nanos);
/* 306 */     String zeroPadding = "000000000";
/*     */     
/* 308 */     nanosString = "000000000".substring(0, fsp - nanosString.length()) + nanosString;
/*     */     
/* 310 */     if (truncateTrailingZeros) {
/* 311 */       int pos = fsp - 1;
/* 312 */       while (nanosString.charAt(pos) == '0') {
/* 313 */         pos--;
/*     */       }
/* 315 */       nanosString = nanosString.substring(0, pos + 1);
/*     */     } 
/* 317 */     return nanosString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void loadTimeZoneMappings(ExceptionInterceptor exceptionInterceptor) {
/* 327 */     timeZoneMappings = new Properties();
/*     */     try {
/* 329 */       timeZoneMappings.load(TimeUtil.class.getResourceAsStream("/com/mysql/cj/util/TimeZoneMapping.properties"));
/* 330 */     } catch (IOException e) {
/* 331 */       throw ExceptionFactory.createException(Messages.getString("TimeUtil.LoadTimeZoneMappingError"), exceptionInterceptor);
/*     */     } 
/*     */     
/* 334 */     for (String tz : TimeZone.getAvailableIDs()) {
/* 335 */       if (!timeZoneMappings.containsKey(tz)) {
/* 336 */         timeZoneMappings.put(tz, tz);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Timestamp truncateFractionalSeconds(Timestamp timestamp) {
/* 342 */     Timestamp truncatedTimestamp = new Timestamp(timestamp.getTime());
/* 343 */     truncatedTimestamp.setNanos(0);
/* 344 */     return truncatedTimestamp;
/*     */   }
/*     */   
/*     */   public static Time truncateFractionalSeconds(Time time) {
/* 348 */     Time truncatedTime = new Time(time.getTime() / 1000L * 1000L);
/* 349 */     return truncatedTime;
/*     */   }
/*     */   
/*     */   public static Boolean hasFractionalSeconds(Time t) {
/* 353 */     return Boolean.valueOf((t.getTime() % 1000L > 0L));
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
/*     */   public static SimpleDateFormat getSimpleDateFormat(SimpleDateFormat cachedSimpleDateFormat, String pattern, TimeZone tz) {
/* 371 */     SimpleDateFormat sdf = (cachedSimpleDateFormat != null && cachedSimpleDateFormat.toPattern().equals(pattern)) ? cachedSimpleDateFormat : new SimpleDateFormat(pattern, Locale.US);
/*     */     
/* 373 */     if (tz != null) {
/* 374 */       sdf.setTimeZone(tz);
/*     */     }
/* 376 */     return sdf;
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
/*     */   public static SimpleDateFormat getSimpleDateFormat(String pattern, Calendar cal) {
/* 392 */     SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
/* 393 */     if (cal != null) {
/* 394 */       cal = (Calendar)cal.clone();
/* 395 */       sdf.setCalendar(cal);
/*     */     } 
/* 397 */     return sdf;
/*     */   }
/*     */   
/*     */   public static Object parseToDateTimeObject(String s, MysqlType targetMysqlType) throws IOException {
/* 401 */     if (DATE_LITERAL_WITH_DELIMITERS.matcher(s).matches()) {
/* 402 */       return LocalDate.parse(getCanonicalDate(s), DateTimeFormatter.ISO_LOCAL_DATE);
/*     */     }
/* 404 */     if (DATE_LITERAL_NO_DELIMITERS.matcher(s).matches() && (targetMysqlType != MysqlType.TIME || !TIME_LITERAL_SHORT6.matcher(s).matches())) {
/* 405 */       return (s.length() == 8) ? LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE) : LocalDate.parse(s, DateTimeFormatter.ofPattern("yyMMdd"));
/*     */     }
/* 407 */     if (TIME_LITERAL_WITH_DELIMITERS.matcher(s).matches()) {
/* 408 */       return LocalTime.parse(getCanonicalTime(s), (new DateTimeFormatterBuilder())
/* 409 */           .appendPattern("HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
/*     */     }
/* 411 */     if (TIME_LITERAL_SHORT6.matcher(s).matches()) {
/* 412 */       return LocalTime.parse(s, (new DateTimeFormatterBuilder())
/* 413 */           .appendPattern("HHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
/*     */     }
/* 415 */     if (TIME_LITERAL_SHORT4.matcher(s).matches()) {
/* 416 */       return LocalTime.parse("00" + s, (new DateTimeFormatterBuilder())
/* 417 */           .appendPattern("HHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
/*     */     }
/* 419 */     if (TIME_LITERAL_SHORT2.matcher(s).matches()) {
/* 420 */       return LocalTime.parse("0000" + s, (new DateTimeFormatterBuilder())
/* 421 */           .appendPattern("HHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
/*     */     }
/* 423 */     if (DATETIME_LITERAL_SHORT14.matcher(s).matches()) {
/* 424 */       return LocalDateTime.parse(s, (new DateTimeFormatterBuilder())
/* 425 */           .appendPattern("yyyyMMddHHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
/*     */     }
/* 427 */     if (DATETIME_LITERAL_SHORT12.matcher(s).matches()) {
/* 428 */       return LocalDateTime.parse(s, (new DateTimeFormatterBuilder())
/* 429 */           .appendPattern("yyMMddHHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
/*     */     }
/* 431 */     if (DATETIME_LITERAL_WITH_DELIMITERS.matcher(s).matches()) {
/* 432 */       return LocalDateTime.parse(getCanonicalDateTime(s), (new DateTimeFormatterBuilder())
/* 433 */           .appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
/*     */     }
/* 435 */     if (DURATION_LITERAL_WITH_DAYS.matcher(s).matches() || DURATION_LITERAL_NO_DAYS.matcher(s).matches()) {
/* 436 */       s = s.startsWith("-") ? s.replace("-", "-P") : ("P" + s);
/* 437 */       s = s.contains(" ") ? s.replace(" ", "DT") : s.replace("P", "PT");
/* 438 */       String[] ch = { "H", "M", "S" };
/* 439 */       int pos = 0;
/* 440 */       while (s.contains(":")) {
/* 441 */         s = s.replaceFirst(":", ch[pos++]);
/*     */       }
/* 443 */       s = s + ch[pos];
/* 444 */       return Duration.parse(s);
/*     */     } 
/* 446 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "There is no known date-time pattern for '" + s + "' value");
/*     */   }
/*     */   
/*     */   private static String getCanonicalDate(String s) {
/* 450 */     String[] sa = s.split("\\p{Punct}");
/* 451 */     StringBuilder sb = new StringBuilder();
/* 452 */     if (sa[0].length() == 2) {
/* 453 */       sb.append((Integer.valueOf(sa[0]).intValue() > 69) ? "19" : "20");
/*     */     }
/* 455 */     sb.append(sa[0]);
/* 456 */     sb.append("-");
/* 457 */     if (sa[1].length() == 1) {
/* 458 */       sb.append("0");
/*     */     }
/* 460 */     sb.append(sa[1]);
/* 461 */     sb.append("-");
/* 462 */     if (sa[2].length() == 1) {
/* 463 */       sb.append("0");
/*     */     }
/* 465 */     sb.append(sa[2]);
/*     */     
/* 467 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private static String getCanonicalTime(String s) {
/* 471 */     String[] sa = s.split("\\p{Punct}");
/* 472 */     StringBuilder sb = new StringBuilder();
/*     */     
/* 474 */     for (int i = 0; i < sa.length; i++) {
/* 475 */       if (i > 0) {
/* 476 */         sb.append((i < 3) ? ":" : ".");
/*     */       }
/* 478 */       if (i < 3 && sa[i].length() == 1) {
/* 479 */         sb.append("0");
/*     */       }
/* 481 */       sb.append(sa[i]);
/*     */     } 
/*     */     
/* 484 */     if (sa.length < 3) {
/* 485 */       sb.append(":00");
/*     */     }
/*     */     
/* 488 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private static String getCanonicalDateTime(String s) {
/* 492 */     String[] sa = s.split("[ T]");
/* 493 */     StringBuilder sb = new StringBuilder();
/* 494 */     sb.append(getCanonicalDate(sa[0]));
/* 495 */     sb.append(" ");
/* 496 */     sb.append(getCanonicalTime(sa[1]));
/* 497 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static String getDurationString(Duration x) {
/* 501 */     String s = (x.isNegative() ? ("-" + x.abs().toString()) : x.toString()).replace("PT", "");
/* 502 */     if (s.contains("M")) {
/* 503 */       s = s.replace("H", ":");
/* 504 */       if (s.contains("S")) {
/* 505 */         s = s.replace("M", ":").replace("S", "");
/*     */       } else {
/* 507 */         s = s.replace("M", ":0");
/*     */       } 
/*     */     } else {
/* 510 */       s = s.replace("H", ":0:0");
/*     */     } 
/* 512 */     return s;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\TimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */