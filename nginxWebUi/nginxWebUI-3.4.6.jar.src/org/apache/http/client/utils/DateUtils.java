/*     */ package org.apache.http.client.utils;
/*     */ 
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import org.apache.http.util.Args;
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
/*     */ public final class DateUtils
/*     */ {
/*     */   public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
/*     */   public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
/*     */   public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
/*  67 */   private static final String[] DEFAULT_PATTERNS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy" };
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
/*     */   
/*     */   static {
/*  78 */     Calendar calendar = Calendar.getInstance();
/*  79 */     calendar.setTimeZone(GMT);
/*  80 */     calendar.set(2000, 0, 1, 0, 0, 0);
/*  81 */     calendar.set(14, 0);
/*  82 */     DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
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
/*     */   public static Date parseDate(String dateValue) {
/*  94 */     return parseDate(dateValue, null, null);
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
/*     */   public static Date parseDate(String dateValue, String[] dateFormats) {
/* 106 */     return parseDate(dateValue, dateFormats, null);
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
/*     */   public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) {
/* 125 */     Args.notNull(dateValue, "Date value");
/* 126 */     String[] localDateFormats = (dateFormats != null) ? dateFormats : DEFAULT_PATTERNS;
/* 127 */     Date localStartDate = (startDate != null) ? startDate : DEFAULT_TWO_DIGIT_YEAR_START;
/* 128 */     String v = dateValue;
/*     */ 
/*     */     
/* 131 */     if (v.length() > 1 && v.startsWith("'") && v.endsWith("'")) {
/* 132 */       v = v.substring(1, v.length() - 1);
/*     */     }
/*     */     
/* 135 */     for (String dateFormat : localDateFormats) {
/* 136 */       SimpleDateFormat dateParser = DateFormatHolder.formatFor(dateFormat);
/* 137 */       dateParser.set2DigitYearStart(localStartDate);
/* 138 */       ParsePosition pos = new ParsePosition(0);
/* 139 */       Date result = dateParser.parse(v, pos);
/* 140 */       if (pos.getIndex() != 0) {
/* 141 */         return result;
/*     */       }
/*     */     } 
/* 144 */     return null;
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
/*     */   public static String formatDate(Date date) {
/* 156 */     return formatDate(date, "EEE, dd MMM yyyy HH:mm:ss zzz");
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
/*     */   public static String formatDate(Date date, String pattern) {
/* 173 */     Args.notNull(date, "Date");
/* 174 */     Args.notNull(pattern, "Pattern");
/* 175 */     SimpleDateFormat formatter = DateFormatHolder.formatFor(pattern);
/* 176 */     return formatter.format(date);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clearThreadLocal() {
/* 185 */     DateFormatHolder.clearThreadLocal();
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
/*     */   static final class DateFormatHolder
/*     */   {
/* 201 */     private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>();
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
/*     */     public static SimpleDateFormat formatFor(String pattern) {
/* 216 */       SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
/* 217 */       Map<String, SimpleDateFormat> formats = (ref == null) ? null : ref.get();
/* 218 */       if (formats == null) {
/* 219 */         formats = new HashMap<String, SimpleDateFormat>();
/* 220 */         THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
/*     */       } 
/*     */ 
/*     */       
/* 224 */       SimpleDateFormat format = formats.get(pattern);
/* 225 */       if (format == null) {
/* 226 */         format = new SimpleDateFormat(pattern, Locale.US);
/* 227 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 228 */         formats.put(pattern, format);
/*     */       } 
/*     */       
/* 231 */       return format;
/*     */     }
/*     */     
/*     */     public static void clearThreadLocal() {
/* 235 */       THREADLOCAL_FORMATS.remove();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\DateUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */