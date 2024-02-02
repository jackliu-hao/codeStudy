package com.mysql.cj.util;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlType;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class TimeUtil {
   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
   public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   public static final DateTimeFormatter TIME_FORMATTER_NO_FRACT_NO_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss");
   public static final DateTimeFormatter TIME_FORMATTER_WITH_NANOS_NO_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS");
   public static final DateTimeFormatter TIME_FORMATTER_NO_FRACT_WITH_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ssXXX");
   public static final DateTimeFormatter TIME_FORMATTER_WITH_NANOS_WITH_OFFSET = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSSXXX");
   public static final DateTimeFormatter DATETIME_FORMATTER_NO_FRACT_NO_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   public static final DateTimeFormatter DATETIME_FORMATTER_WITH_MILLIS_NO_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
   public static final DateTimeFormatter DATETIME_FORMATTER_WITH_NANOS_NO_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
   public static final DateTimeFormatter DATETIME_FORMATTER_NO_FRACT_WITH_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
   public static final DateTimeFormatter DATETIME_FORMATTER_WITH_NANOS_WITH_OFFSET = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSXXX");
   public static final Pattern DATE_LITERAL_WITH_DELIMITERS = Pattern.compile("(\\d{4}|\\d{2})[\\p{Punct}&&[^:]](([0])?[1-9]|[1][0-2])[\\p{Punct}&&[^:]](([0])?[1-9]|[1-2]\\d|[3][0-1])");
   public static final Pattern DATE_LITERAL_NO_DELIMITERS = Pattern.compile("(\\d{4}|\\d{2})([0][1-9]|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])");
   public static final Pattern TIME_LITERAL_WITH_DELIMITERS = Pattern.compile("(([0-1])?\\d|[2][0-3]):([0-5])?\\d(:([0-5])?\\d(\\.\\d{1,9})?)?");
   public static final Pattern TIME_LITERAL_SHORT6 = Pattern.compile("([0-1]\\d|[2][0-3])([0-5]\\d){2}(\\.\\d{1,9})?");
   public static final Pattern TIME_LITERAL_SHORT4 = Pattern.compile("([0-5]\\d){2}(\\.\\d{1,9})?");
   public static final Pattern TIME_LITERAL_SHORT2 = Pattern.compile("[0-5]\\d(\\.\\d{1,9})?");
   public static final Pattern DATETIME_LITERAL_WITH_DELIMITERS = Pattern.compile("(\\d{4}|\\d{2})\\p{Punct}(([0])?[1-9]|[1][0-2])\\p{Punct}(([0])?[1-9]|[1-2]\\d|[3][0-1])[ T](([0-1])?\\d|[2][0-3])\\p{Punct}([0-5])?\\d(\\p{Punct}([0-5])?\\d(\\.\\d{1,9})?)?");
   public static final Pattern DATETIME_LITERAL_SHORT14 = Pattern.compile("\\d{4}([0][1-9]|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])([0-1]\\d|[2][0-3])([0-5]\\d){2}(\\.\\d{1,9}){0,1}");
   public static final Pattern DATETIME_LITERAL_SHORT12 = Pattern.compile("\\d{2}([0][1-9]|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])([0-1]\\d|[2][0-3])([0-5]\\d){2}(\\.\\d{1,9}){0,1}");
   public static final Pattern DURATION_LITERAL_WITH_DAYS = Pattern.compile("(-)?(([0-2])?\\d|[3][0-4]) (([0-1])?\\d|[2][0-3])(:([0-5])?\\d(:([0-5])?\\d(\\.\\d{1,9})?)?)?");
   public static final Pattern DURATION_LITERAL_NO_DAYS = Pattern.compile("(-)?\\d{1,3}:([0-5])?\\d(:([0-5])?\\d(\\.\\d{1,9})?)?");
   private static final String TIME_ZONE_MAPPINGS_RESOURCE = "/com/mysql/cj/util/TimeZoneMapping.properties";
   private static Properties timeZoneMappings = null;
   protected static final Method systemNanoTimeMethod;

   public static boolean nanoTimeAvailable() {
      return systemNanoTimeMethod != null;
   }

   public static long getCurrentTimeNanosOrMillis() {
      if (systemNanoTimeMethod != null) {
         try {
            return (Long)systemNanoTimeMethod.invoke((Object)null, (Object[])null);
         } catch (IllegalArgumentException var1) {
         } catch (IllegalAccessException var2) {
         } catch (InvocationTargetException var3) {
         }
      }

      return System.currentTimeMillis();
   }

   public static String getCanonicalTimeZone(String timezoneStr, ExceptionInterceptor exceptionInterceptor) {
      if (timezoneStr == null) {
         return null;
      } else {
         timezoneStr = timezoneStr.trim();
         if (timezoneStr.length() > 2 && (timezoneStr.charAt(0) == '+' || timezoneStr.charAt(0) == '-') && Character.isDigit(timezoneStr.charAt(1))) {
            return "GMT" + timezoneStr;
         } else {
            Class var2 = TimeUtil.class;
            synchronized(TimeUtil.class) {
               if (timeZoneMappings == null) {
                  loadTimeZoneMappings(exceptionInterceptor);
               }
            }

            String canonicalTz;
            if ((canonicalTz = timeZoneMappings.getProperty(timezoneStr)) != null) {
               return canonicalTz;
            } else {
               throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("TimeUtil.UnrecognizedTimeZoneId", new Object[]{timezoneStr}), exceptionInterceptor);
            }
         }
      }
   }

   public static Timestamp adjustNanosPrecision(Timestamp ts, int fsp, boolean serverRoundFracSecs) {
      if (fsp >= 0 && fsp <= 6) {
         Timestamp res = (Timestamp)ts.clone();
         double tail = Math.pow(10.0, (double)(9 - fsp));
         int nanos = serverRoundFracSecs ? (int)Math.round((double)res.getNanos() / tail) * (int)tail : (int)((double)res.getNanos() / tail) * (int)tail;
         if (nanos > 999999999) {
            nanos %= 1000000000;
            res.setTime(res.getTime() + 1000L);
         }

         res.setNanos(nanos);
         return res;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
      }
   }

   public static LocalDateTime adjustNanosPrecision(LocalDateTime x, int fsp, boolean serverRoundFracSecs) {
      if (fsp >= 0 && fsp <= 6) {
         int originalNano = x.getNano();
         double tail = Math.pow(10.0, (double)(9 - fsp));
         int adjustedNano = serverRoundFracSecs ? (int)Math.round((double)originalNano / tail) * (int)tail : (int)((double)originalNano / tail) * (int)tail;
         if (adjustedNano > 999999999) {
            adjustedNano %= 1000000000;
            x = x.plusSeconds(1L);
         }

         return x.withNano(adjustedNano);
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
      }
   }

   public static LocalTime adjustNanosPrecision(LocalTime x, int fsp, boolean serverRoundFracSecs) {
      if (fsp >= 0 && fsp <= 6) {
         int originalNano = x.getNano();
         double tail = Math.pow(10.0, (double)(9 - fsp));
         int adjustedNano = serverRoundFracSecs ? (int)Math.round((double)originalNano / tail) * (int)tail : (int)((double)originalNano / tail) * (int)tail;
         if (adjustedNano > 999999999) {
            adjustedNano %= 1000000000;
            x = x.plusSeconds(1L);
         }

         return x.withNano(adjustedNano);
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
      }
   }

   public static Duration adjustNanosPrecision(Duration x, int fsp, boolean serverRoundFracSecs) {
      if (fsp >= 0 && fsp <= 6) {
         int originalNano = x.getNano();
         double tail = Math.pow(10.0, (double)(9 - fsp));
         int adjustedNano = serverRoundFracSecs ? (int)Math.round((double)originalNano / tail) * (int)tail : (int)((double)originalNano / tail) * (int)tail;
         if (adjustedNano > 999999999) {
            adjustedNano %= 1000000000;
            x = x.plusSeconds(1L);
         }

         return x.withNanos(adjustedNano);
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range.");
      }
   }

   public static String formatNanos(int nanos, int fsp) {
      return formatNanos(nanos, fsp, true);
   }

   public static String formatNanos(int nanos, int fsp, boolean truncateTrailingZeros) {
      if (nanos >= 0 && nanos <= 999999999) {
         if (fsp >= 0 && fsp <= 6) {
            if (fsp != 0 && nanos != 0) {
               nanos = (int)((double)nanos / Math.pow(10.0, (double)(9 - fsp)));
               if (nanos == 0) {
                  return "0";
               } else {
                  String nanosString = Integer.toString(nanos);
                  String zeroPadding = "000000000";
                  nanosString = "000000000".substring(0, fsp - nanosString.length()) + nanosString;
                  if (truncateTrailingZeros) {
                     int pos;
                     for(pos = fsp - 1; nanosString.charAt(pos) == '0'; --pos) {
                     }

                     nanosString = nanosString.substring(0, pos + 1);
                  }

                  return nanosString;
               }
            } else {
               return "0";
            }
         } else {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "fsp value must be in 0 to 6 range but was " + fsp);
         }
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "nanos value must be in 0 to 999999999 range but was " + nanos);
      }
   }

   private static void loadTimeZoneMappings(ExceptionInterceptor exceptionInterceptor) {
      timeZoneMappings = new Properties();

      try {
         timeZoneMappings.load(TimeUtil.class.getResourceAsStream("/com/mysql/cj/util/TimeZoneMapping.properties"));
      } catch (IOException var5) {
         throw ExceptionFactory.createException(Messages.getString("TimeUtil.LoadTimeZoneMappingError"), exceptionInterceptor);
      }

      String[] var1 = TimeZone.getAvailableIDs();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String tz = var1[var3];
         if (!timeZoneMappings.containsKey(tz)) {
            timeZoneMappings.put(tz, tz);
         }
      }

   }

   public static Timestamp truncateFractionalSeconds(Timestamp timestamp) {
      Timestamp truncatedTimestamp = new Timestamp(timestamp.getTime());
      truncatedTimestamp.setNanos(0);
      return truncatedTimestamp;
   }

   public static Time truncateFractionalSeconds(Time time) {
      Time truncatedTime = new Time(time.getTime() / 1000L * 1000L);
      return truncatedTime;
   }

   public static Boolean hasFractionalSeconds(Time t) {
      return t.getTime() % 1000L > 0L;
   }

   public static SimpleDateFormat getSimpleDateFormat(SimpleDateFormat cachedSimpleDateFormat, String pattern, TimeZone tz) {
      SimpleDateFormat sdf = cachedSimpleDateFormat != null && cachedSimpleDateFormat.toPattern().equals(pattern) ? cachedSimpleDateFormat : new SimpleDateFormat(pattern, Locale.US);
      if (tz != null) {
         sdf.setTimeZone(tz);
      }

      return sdf;
   }

   public static SimpleDateFormat getSimpleDateFormat(String pattern, Calendar cal) {
      SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
      if (cal != null) {
         cal = (Calendar)cal.clone();
         sdf.setCalendar(cal);
      }

      return sdf;
   }

   public static Object parseToDateTimeObject(String s, MysqlType targetMysqlType) throws IOException {
      if (DATE_LITERAL_WITH_DELIMITERS.matcher(s).matches()) {
         return LocalDate.parse(getCanonicalDate(s), DateTimeFormatter.ISO_LOCAL_DATE);
      } else if (DATE_LITERAL_NO_DELIMITERS.matcher(s).matches() && (targetMysqlType != MysqlType.TIME || !TIME_LITERAL_SHORT6.matcher(s).matches())) {
         return s.length() == 8 ? LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE) : LocalDate.parse(s, DateTimeFormatter.ofPattern("yyMMdd"));
      } else if (TIME_LITERAL_WITH_DELIMITERS.matcher(s).matches()) {
         return LocalTime.parse(getCanonicalTime(s), (new DateTimeFormatterBuilder()).appendPattern("HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
      } else if (TIME_LITERAL_SHORT6.matcher(s).matches()) {
         return LocalTime.parse(s, (new DateTimeFormatterBuilder()).appendPattern("HHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
      } else if (TIME_LITERAL_SHORT4.matcher(s).matches()) {
         return LocalTime.parse("00" + s, (new DateTimeFormatterBuilder()).appendPattern("HHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
      } else if (TIME_LITERAL_SHORT2.matcher(s).matches()) {
         return LocalTime.parse("0000" + s, (new DateTimeFormatterBuilder()).appendPattern("HHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
      } else if (DATETIME_LITERAL_SHORT14.matcher(s).matches()) {
         return LocalDateTime.parse(s, (new DateTimeFormatterBuilder()).appendPattern("yyyyMMddHHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
      } else if (DATETIME_LITERAL_SHORT12.matcher(s).matches()) {
         return LocalDateTime.parse(s, (new DateTimeFormatterBuilder()).appendPattern("yyMMddHHmmss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
      } else if (DATETIME_LITERAL_WITH_DELIMITERS.matcher(s).matches()) {
         return LocalDateTime.parse(getCanonicalDateTime(s), (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter());
      } else if (!DURATION_LITERAL_WITH_DAYS.matcher(s).matches() && !DURATION_LITERAL_NO_DAYS.matcher(s).matches()) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "There is no known date-time pattern for '" + s + "' value");
      } else {
         s = s.startsWith("-") ? s.replace("-", "-P") : "P" + s;
         s = s.contains(" ") ? s.replace(" ", "DT") : s.replace("P", "PT");
         String[] ch = new String[]{"H", "M", "S"};

         int pos;
         for(pos = 0; s.contains(":"); s = s.replaceFirst(":", ch[pos++])) {
         }

         s = s + ch[pos];
         return Duration.parse(s);
      }
   }

   private static String getCanonicalDate(String s) {
      String[] sa = s.split("\\p{Punct}");
      StringBuilder sb = new StringBuilder();
      if (sa[0].length() == 2) {
         sb.append(Integer.valueOf(sa[0]) > 69 ? "19" : "20");
      }

      sb.append(sa[0]);
      sb.append("-");
      if (sa[1].length() == 1) {
         sb.append("0");
      }

      sb.append(sa[1]);
      sb.append("-");
      if (sa[2].length() == 1) {
         sb.append("0");
      }

      sb.append(sa[2]);
      return sb.toString();
   }

   private static String getCanonicalTime(String s) {
      String[] sa = s.split("\\p{Punct}");
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < sa.length; ++i) {
         if (i > 0) {
            sb.append(i < 3 ? ":" : ".");
         }

         if (i < 3 && sa[i].length() == 1) {
            sb.append("0");
         }

         sb.append(sa[i]);
      }

      if (sa.length < 3) {
         sb.append(":00");
      }

      return sb.toString();
   }

   private static String getCanonicalDateTime(String s) {
      String[] sa = s.split("[ T]");
      StringBuilder sb = new StringBuilder();
      sb.append(getCanonicalDate(sa[0]));
      sb.append(" ");
      sb.append(getCanonicalTime(sa[1]));
      return sb.toString();
   }

   public static String getDurationString(Duration x) {
      String s = (x.isNegative() ? "-" + x.abs().toString() : x.toString()).replace("PT", "");
      if (s.contains("M")) {
         s = s.replace("H", ":");
         if (s.contains("S")) {
            s = s.replace("M", ":").replace("S", "");
         } else {
            s = s.replace("M", ":0");
         }
      } else {
         s = s.replace("H", ":0:0");
      }

      return s;
   }

   static {
      Method aMethod;
      try {
         aMethod = System.class.getMethod("nanoTime", (Class[])null);
      } catch (SecurityException var2) {
         aMethod = null;
      } catch (NoSuchMethodException var3) {
         aMethod = null;
      }

      systemNanoTimeMethod = aMethod;
   }
}
