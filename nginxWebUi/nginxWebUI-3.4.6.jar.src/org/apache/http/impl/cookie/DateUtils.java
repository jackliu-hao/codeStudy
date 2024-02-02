/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
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
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public final class DateUtils
/*     */ {
/*     */   public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
/*     */   public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
/*     */   public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
/*  62 */   public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
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
/*     */   public static Date parseDate(String dateValue) throws DateParseException {
/*  76 */     return parseDate(dateValue, null, null);
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
/*     */   public static Date parseDate(String dateValue, String[] dateFormats) throws DateParseException {
/*  91 */     return parseDate(dateValue, dateFormats, null);
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
/*     */ 
/*     */   
/*     */   public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) throws DateParseException {
/* 113 */     Date d = org.apache.http.client.utils.DateUtils.parseDate(dateValue, dateFormats, startDate);
/* 114 */     if (d == null) {
/* 115 */       throw new DateParseException("Unable to parse the date " + dateValue);
/*     */     }
/* 117 */     return d;
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
/* 129 */     return org.apache.http.client.utils.DateUtils.formatDate(date);
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
/* 146 */     return org.apache.http.client.utils.DateUtils.formatDate(date, pattern);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\DateUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */