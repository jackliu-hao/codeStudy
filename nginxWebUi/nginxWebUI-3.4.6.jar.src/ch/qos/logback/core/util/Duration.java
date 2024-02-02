/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import java.util.regex.Matcher;
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
/*     */ public class Duration
/*     */ {
/*     */   private static final String DOUBLE_PART = "([0-9]*(.[0-9]+)?)";
/*     */   private static final int DOUBLE_GROUP = 1;
/*     */   private static final String UNIT_PART = "(|milli(second)?|second(e)?|minute|hour|day)s?";
/*     */   private static final int UNIT_GROUP = 3;
/*  42 */   private static final Pattern DURATION_PATTERN = Pattern.compile("([0-9]*(.[0-9]+)?)\\s*(|milli(second)?|second(e)?|minute|hour|day)s?", 2);
/*     */   
/*     */   static final long SECONDS_COEFFICIENT = 1000L;
/*     */   
/*     */   static final long MINUTES_COEFFICIENT = 60000L;
/*     */   static final long HOURS_COEFFICIENT = 3600000L;
/*     */   static final long DAYS_COEFFICIENT = 86400000L;
/*     */   final long millis;
/*     */   
/*     */   public Duration(long millis) {
/*  52 */     this.millis = millis;
/*     */   }
/*     */   
/*     */   public static Duration buildByMilliseconds(double value) {
/*  56 */     return new Duration((long)value);
/*     */   }
/*     */   
/*     */   public static Duration buildBySeconds(double value) {
/*  60 */     return new Duration((long)(1000.0D * value));
/*     */   }
/*     */   
/*     */   public static Duration buildByMinutes(double value) {
/*  64 */     return new Duration((long)(60000.0D * value));
/*     */   }
/*     */   
/*     */   public static Duration buildByHours(double value) {
/*  68 */     return new Duration((long)(3600000.0D * value));
/*     */   }
/*     */   
/*     */   public static Duration buildByDays(double value) {
/*  72 */     return new Duration((long)(8.64E7D * value));
/*     */   }
/*     */   
/*     */   public static Duration buildUnbounded() {
/*  76 */     return new Duration(Long.MAX_VALUE);
/*     */   }
/*     */   
/*     */   public long getMilliseconds() {
/*  80 */     return this.millis;
/*     */   }
/*     */   
/*     */   public static Duration valueOf(String durationStr) {
/*  84 */     Matcher matcher = DURATION_PATTERN.matcher(durationStr);
/*     */     
/*  86 */     if (matcher.matches()) {
/*  87 */       String doubleStr = matcher.group(1);
/*  88 */       String unitStr = matcher.group(3);
/*     */       
/*  90 */       double doubleValue = Double.valueOf(doubleStr).doubleValue();
/*  91 */       if (unitStr.equalsIgnoreCase("milli") || unitStr.equalsIgnoreCase("millisecond") || unitStr.length() == 0)
/*  92 */         return buildByMilliseconds(doubleValue); 
/*  93 */       if (unitStr.equalsIgnoreCase("second") || unitStr.equalsIgnoreCase("seconde"))
/*  94 */         return buildBySeconds(doubleValue); 
/*  95 */       if (unitStr.equalsIgnoreCase("minute"))
/*  96 */         return buildByMinutes(doubleValue); 
/*  97 */       if (unitStr.equalsIgnoreCase("hour"))
/*  98 */         return buildByHours(doubleValue); 
/*  99 */       if (unitStr.equalsIgnoreCase("day")) {
/* 100 */         return buildByDays(doubleValue);
/*     */       }
/* 102 */       throw new IllegalStateException("Unexpected " + unitStr);
/*     */     } 
/*     */     
/* 105 */     throw new IllegalArgumentException("String value [" + durationStr + "] is not in the expected format.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 111 */     if (this.millis < 1000L)
/* 112 */       return this.millis + " milliseconds"; 
/* 113 */     if (this.millis < 60000L)
/* 114 */       return (this.millis / 1000L) + " seconds"; 
/* 115 */     if (this.millis < 3600000L) {
/* 116 */       return (this.millis / 60000L) + " minutes";
/*     */     }
/* 118 */     return (this.millis / 3600000L) + " hours";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\Duration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */