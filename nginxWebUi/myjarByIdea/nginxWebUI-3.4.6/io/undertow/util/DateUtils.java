package io.undertow.util;

import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DateUtils {
   private static final Locale LOCALE_US;
   private static final TimeZone GMT_ZONE;
   private static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
   private static final AtomicReference<String> cachedDateString;
   private static final ThreadLocal<SimpleDateFormat> RFC1123_PATTERN_FORMAT;
   private static final Runnable INVALIDATE_TASK;
   private static final String RFC1036_PATTERN = "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";
   private static final String ASCITIME_PATTERN = "EEE MMM d HH:mm:ss yyyyy";
   private static final String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
   private static final String COMMON_LOG_PATTERN = "[dd/MMM/yyyy:HH:mm:ss Z]";
   private static final ThreadLocal<SimpleDateFormat> COMMON_LOG_PATTERN_FORMAT;
   private static final ThreadLocal<SimpleDateFormat> OLD_COOKIE_FORMAT;

   public static String toDateString(Date date) {
      SimpleDateFormat df = (SimpleDateFormat)RFC1123_PATTERN_FORMAT.get();
      df.setTimeZone(GMT_ZONE);
      return df.format(date);
   }

   public static String toOldCookieDateString(Date date) {
      return ((SimpleDateFormat)OLD_COOKIE_FORMAT.get()).format(date);
   }

   public static String toCommonLogFormat(Date date) {
      return ((SimpleDateFormat)COMMON_LOG_PATTERN_FORMAT.get()).format(date);
   }

   public static Date parseDate(String date) {
      int semicolonIndex = date.indexOf(59);
      String trimmedDate = semicolonIndex >= 0 ? date.substring(0, semicolonIndex) : date;
      ParsePosition pp = new ParsePosition(0);
      SimpleDateFormat dateFormat = (SimpleDateFormat)RFC1123_PATTERN_FORMAT.get();
      dateFormat.setTimeZone(GMT_ZONE);
      Date val = dateFormat.parse(trimmedDate, pp);
      if (val != null && pp.getIndex() == trimmedDate.length()) {
         return val;
      } else {
         pp = new ParsePosition(0);
         dateFormat = new SimpleDateFormat("EEEEEEEEE, dd-MMM-yy HH:mm:ss z", LOCALE_US);
         dateFormat.setTimeZone(GMT_ZONE);
         val = dateFormat.parse(trimmedDate, pp);
         if (val != null && pp.getIndex() == trimmedDate.length()) {
            return val;
         } else {
            pp = new ParsePosition(0);
            dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyyy", LOCALE_US);
            dateFormat.setTimeZone(GMT_ZONE);
            val = dateFormat.parse(trimmedDate, pp);
            if (val != null && pp.getIndex() == trimmedDate.length()) {
               return val;
            } else {
               pp = new ParsePosition(0);
               dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", LOCALE_US);
               dateFormat.setTimeZone(GMT_ZONE);
               val = dateFormat.parse(trimmedDate, pp);
               return val != null && pp.getIndex() == trimmedDate.length() ? val : null;
            }
         }
      }
   }

   public static boolean handleIfModifiedSince(HttpServerExchange exchange, Date lastModified) {
      return handleIfModifiedSince(exchange.getRequestHeaders().getFirst(Headers.IF_MODIFIED_SINCE), lastModified);
   }

   public static boolean handleIfModifiedSince(String modifiedSince, Date lastModified) {
      if (lastModified == null) {
         return true;
      } else if (modifiedSince == null) {
         return true;
      } else {
         Date modDate = parseDate(modifiedSince);
         if (modDate == null) {
            return true;
         } else {
            return lastModified.getTime() > modDate.getTime() + 999L;
         }
      }
   }

   public static boolean handleIfUnmodifiedSince(HttpServerExchange exchange, Date lastModified) {
      return handleIfUnmodifiedSince(exchange.getRequestHeaders().getFirst(Headers.IF_UNMODIFIED_SINCE), lastModified);
   }

   public static boolean handleIfUnmodifiedSince(String modifiedSince, Date lastModified) {
      if (lastModified == null) {
         return true;
      } else if (modifiedSince == null) {
         return true;
      } else {
         Date modDate = parseDate(modifiedSince);
         if (modDate == null) {
            return true;
         } else {
            return lastModified.getTime() < modDate.getTime() + 999L;
         }
      }
   }

   public static void addDateHeaderIfRequired(HttpServerExchange exchange) {
      HeaderMap responseHeaders = exchange.getResponseHeaders();
      if (exchange.getConnection().getUndertowOptions().get(UndertowOptions.ALWAYS_SET_DATE, true) && !responseHeaders.contains(Headers.DATE)) {
         String dateString = getCurrentDateTime(exchange);
         responseHeaders.put(Headers.DATE, dateString);
      }

   }

   public static String getCurrentDateTime(HttpServerExchange exchange) {
      String dateString = (String)cachedDateString.get();
      if (dateString == null) {
         long realTime = System.currentTimeMillis();
         long mod = realTime % 1000L;
         long toGo = 1000L - mod;
         dateString = toDateString(new Date(realTime));
         if (cachedDateString.compareAndSet((Object)null, dateString)) {
            WorkerUtils.executeAfter(exchange.getIoThread(), INVALIDATE_TASK, toGo, TimeUnit.MILLISECONDS);
         }
      }

      return dateString;
   }

   private DateUtils() {
   }

   static {
      LOCALE_US = Locale.US;
      GMT_ZONE = TimeZone.getTimeZone("GMT");
      cachedDateString = new AtomicReference();
      RFC1123_PATTERN_FORMAT = new ThreadLocal<SimpleDateFormat>() {
         protected SimpleDateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", DateUtils.LOCALE_US);
            return df;
         }
      };
      INVALIDATE_TASK = new Runnable() {
         public void run() {
            DateUtils.cachedDateString.set((Object)null);
         }
      };
      COMMON_LOG_PATTERN_FORMAT = new ThreadLocal<SimpleDateFormat>() {
         protected SimpleDateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]", DateUtils.LOCALE_US);
            return df;
         }
      };
      OLD_COOKIE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
         protected SimpleDateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", DateUtils.LOCALE_US);
            df.setTimeZone(DateUtils.GMT_ZONE);
            return df;
         }
      };
   }
}
