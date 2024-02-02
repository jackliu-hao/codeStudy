/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.util.CachingDateFormatter;
/*    */ import java.util.List;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DateConverter
/*    */   extends ClassicConverter
/*    */ {
/* 25 */   long lastTimestamp = -1L;
/* 26 */   String timestampStrCache = null;
/* 27 */   CachingDateFormatter cachingDateFormatter = null;
/*    */ 
/*    */   
/*    */   public void start() {
/* 31 */     String datePattern = getFirstOption();
/* 32 */     if (datePattern == null) {
/* 33 */       datePattern = "yyyy-MM-dd HH:mm:ss,SSS";
/*    */     }
/*    */     
/* 36 */     if (datePattern.equals("ISO8601")) {
/* 37 */       datePattern = "yyyy-MM-dd HH:mm:ss,SSS";
/*    */     }
/*    */     
/*    */     try {
/* 41 */       this.cachingDateFormatter = new CachingDateFormatter(datePattern);
/*    */     
/*    */     }
/* 44 */     catch (IllegalArgumentException e) {
/* 45 */       addWarn("Could not instantiate SimpleDateFormat with pattern " + datePattern, e);
/*    */       
/* 47 */       this.cachingDateFormatter = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss,SSS");
/*    */     } 
/*    */     
/* 50 */     List<String> optionList = getOptionList();
/*    */ 
/*    */     
/* 53 */     if (optionList != null && optionList.size() > 1) {
/* 54 */       TimeZone tz = TimeZone.getTimeZone(optionList.get(1));
/* 55 */       this.cachingDateFormatter.setTimeZone(tz);
/*    */     } 
/*    */   }
/*    */   
/*    */   public String convert(ILoggingEvent le) {
/* 60 */     long timestamp = le.getTimeStamp();
/* 61 */     return this.cachingDateFormatter.format(timestamp);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\DateConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */