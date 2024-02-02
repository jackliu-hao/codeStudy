/*    */ package ch.qos.logback.core.util;
/*    */ 
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CachingDateFormatter
/*    */ {
/* 28 */   long lastTimestamp = -1L;
/* 29 */   String cachedStr = null;
/*    */   final SimpleDateFormat sdf;
/*    */   
/*    */   public CachingDateFormatter(String pattern) {
/* 33 */     this.sdf = new SimpleDateFormat(pattern);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String format(long now) {
/* 45 */     synchronized (this) {
/* 46 */       if (now != this.lastTimestamp) {
/* 47 */         this.lastTimestamp = now;
/* 48 */         this.cachedStr = this.sdf.format(new Date(now));
/*    */       } 
/* 50 */       return this.cachedStr;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void setTimeZone(TimeZone tz) {
/* 55 */     this.sdf.setTimeZone(tz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\CachingDateFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */