/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public class DateUtils
/*     */ {
/*  39 */   private static final Locale LOCALE_US = Locale.US;
/*     */   
/*  41 */   private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
/*     */   
/*     */   private static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
/*     */   
/*  45 */   private static final AtomicReference<String> cachedDateString = new AtomicReference<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private static final ThreadLocal<SimpleDateFormat> RFC1123_PATTERN_FORMAT = new ThreadLocal<SimpleDateFormat>()
/*     */     {
/*     */       protected SimpleDateFormat initialValue() {
/*  56 */         SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", DateUtils.LOCALE_US);
/*  57 */         return df;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   private static final Runnable INVALIDATE_TASK = new Runnable()
/*     */     {
/*     */       public void run() {
/*  67 */         DateUtils.cachedDateString.set(null);
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private static final String RFC1036_PATTERN = "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";
/*     */   
/*     */   private static final String ASCITIME_PATTERN = "EEE MMM d HH:mm:ss yyyyy";
/*     */   
/*     */   private static final String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
/*     */   private static final String COMMON_LOG_PATTERN = "[dd/MMM/yyyy:HH:mm:ss Z]";
/*     */   
/*  79 */   private static final ThreadLocal<SimpleDateFormat> COMMON_LOG_PATTERN_FORMAT = new ThreadLocal<SimpleDateFormat>()
/*     */     {
/*     */       protected SimpleDateFormat initialValue() {
/*  82 */         SimpleDateFormat df = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]", DateUtils.LOCALE_US);
/*  83 */         return df;
/*     */       }
/*     */     };
/*     */   
/*  87 */   private static final ThreadLocal<SimpleDateFormat> OLD_COOKIE_FORMAT = new ThreadLocal<SimpleDateFormat>()
/*     */     {
/*     */       protected SimpleDateFormat initialValue() {
/*  90 */         SimpleDateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", DateUtils.LOCALE_US);
/*  91 */         df.setTimeZone(DateUtils.GMT_ZONE);
/*  92 */         return df;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toDateString(Date date) {
/* 103 */     SimpleDateFormat df = RFC1123_PATTERN_FORMAT.get();
/*     */ 
/*     */ 
/*     */     
/* 107 */     df.setTimeZone(GMT_ZONE);
/* 108 */     return df.format(date);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String toOldCookieDateString(Date date) {
/* 113 */     return ((SimpleDateFormat)OLD_COOKIE_FORMAT.get()).format(date);
/*     */   }
/*     */   
/*     */   public static String toCommonLogFormat(Date date) {
/* 117 */     return ((SimpleDateFormat)COMMON_LOG_PATTERN_FORMAT.get()).format(date);
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
/*     */   public static Date parseDate(String date) {
/* 135 */     int semicolonIndex = date.indexOf(';');
/* 136 */     String trimmedDate = (semicolonIndex >= 0) ? date.substring(0, semicolonIndex) : date;
/*     */     
/* 138 */     ParsePosition pp = new ParsePosition(0);
/* 139 */     SimpleDateFormat dateFormat = RFC1123_PATTERN_FORMAT.get();
/* 140 */     dateFormat.setTimeZone(GMT_ZONE);
/* 141 */     Date val = dateFormat.parse(trimmedDate, pp);
/* 142 */     if (val != null && pp.getIndex() == trimmedDate.length()) {
/* 143 */       return val;
/*     */     }
/*     */     
/* 146 */     pp = new ParsePosition(0);
/* 147 */     dateFormat = new SimpleDateFormat("EEEEEEEEE, dd-MMM-yy HH:mm:ss z", LOCALE_US);
/* 148 */     dateFormat.setTimeZone(GMT_ZONE);
/* 149 */     val = dateFormat.parse(trimmedDate, pp);
/* 150 */     if (val != null && pp.getIndex() == trimmedDate.length()) {
/* 151 */       return val;
/*     */     }
/*     */     
/* 154 */     pp = new ParsePosition(0);
/* 155 */     dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyyy", LOCALE_US);
/* 156 */     dateFormat.setTimeZone(GMT_ZONE);
/* 157 */     val = dateFormat.parse(trimmedDate, pp);
/* 158 */     if (val != null && pp.getIndex() == trimmedDate.length()) {
/* 159 */       return val;
/*     */     }
/*     */     
/* 162 */     pp = new ParsePosition(0);
/* 163 */     dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", LOCALE_US);
/* 164 */     dateFormat.setTimeZone(GMT_ZONE);
/* 165 */     val = dateFormat.parse(trimmedDate, pp);
/* 166 */     if (val != null && pp.getIndex() == trimmedDate.length()) {
/* 167 */       return val;
/*     */     }
/*     */     
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfModifiedSince(HttpServerExchange exchange, Date lastModified) {
/* 181 */     return handleIfModifiedSince(exchange.getRequestHeaders().getFirst(Headers.IF_MODIFIED_SINCE), lastModified);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfModifiedSince(String modifiedSince, Date lastModified) {
/* 192 */     if (lastModified == null) {
/* 193 */       return true;
/*     */     }
/* 195 */     if (modifiedSince == null) {
/* 196 */       return true;
/*     */     }
/* 198 */     Date modDate = parseDate(modifiedSince);
/* 199 */     if (modDate == null) {
/* 200 */       return true;
/*     */     }
/* 202 */     return (lastModified.getTime() > modDate.getTime() + 999L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfUnmodifiedSince(HttpServerExchange exchange, Date lastModified) {
/* 213 */     return handleIfUnmodifiedSince(exchange.getRequestHeaders().getFirst(Headers.IF_UNMODIFIED_SINCE), lastModified);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean handleIfUnmodifiedSince(String modifiedSince, Date lastModified) {
/* 224 */     if (lastModified == null) {
/* 225 */       return true;
/*     */     }
/* 227 */     if (modifiedSince == null) {
/* 228 */       return true;
/*     */     }
/* 230 */     Date modDate = parseDate(modifiedSince);
/* 231 */     if (modDate == null) {
/* 232 */       return true;
/*     */     }
/* 234 */     return (lastModified.getTime() < modDate.getTime() + 999L);
/*     */   }
/*     */   
/*     */   public static void addDateHeaderIfRequired(HttpServerExchange exchange) {
/* 238 */     HeaderMap responseHeaders = exchange.getResponseHeaders();
/* 239 */     if (exchange.getConnection().getUndertowOptions().get(UndertowOptions.ALWAYS_SET_DATE, true) && !responseHeaders.contains(Headers.DATE)) {
/* 240 */       String dateString = getCurrentDateTime(exchange);
/* 241 */       responseHeaders.put(Headers.DATE, dateString);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static String getCurrentDateTime(HttpServerExchange exchange) {
/* 246 */     String dateString = cachedDateString.get();
/* 247 */     if (dateString == null) {
/*     */ 
/*     */ 
/*     */       
/* 251 */       long realTime = System.currentTimeMillis();
/* 252 */       long mod = realTime % 1000L;
/* 253 */       long toGo = 1000L - mod;
/* 254 */       dateString = toDateString(new Date(realTime));
/* 255 */       if (cachedDateString.compareAndSet(null, dateString)) {
/* 256 */         WorkerUtils.executeAfter(exchange.getIoThread(), INVALIDATE_TASK, toGo, TimeUnit.MILLISECONDS);
/*     */       }
/*     */     } 
/* 259 */     return dateString;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\DateUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */