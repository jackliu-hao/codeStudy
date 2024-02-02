/*    */ package ch.qos.logback.core.util;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
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
/*    */ public class TimeUtil
/*    */ {
/*    */   public static long computeStartOfNextSecond(long now) {
/* 22 */     Calendar cal = Calendar.getInstance();
/* 23 */     cal.setTime(new Date(now));
/* 24 */     cal.set(14, 0);
/* 25 */     cal.add(13, 1);
/* 26 */     return cal.getTime().getTime();
/*    */   }
/*    */   
/*    */   public static long computeStartOfNextMinute(long now) {
/* 30 */     Calendar cal = Calendar.getInstance();
/* 31 */     cal.setTime(new Date(now));
/* 32 */     cal.set(14, 0);
/* 33 */     cal.set(13, 0);
/* 34 */     cal.add(12, 1);
/* 35 */     return cal.getTime().getTime();
/*    */   }
/*    */   
/*    */   public static long computeStartOfNextHour(long now) {
/* 39 */     Calendar cal = Calendar.getInstance();
/* 40 */     cal.setTime(new Date(now));
/* 41 */     cal.set(14, 0);
/* 42 */     cal.set(13, 0);
/* 43 */     cal.set(12, 0);
/* 44 */     cal.add(10, 1);
/* 45 */     return cal.getTime().getTime();
/*    */   }
/*    */   
/*    */   public static long computeStartOfNextDay(long now) {
/* 49 */     Calendar cal = Calendar.getInstance();
/* 50 */     cal.setTime(new Date(now));
/*    */     
/* 52 */     cal.add(5, 1);
/* 53 */     cal.set(14, 0);
/* 54 */     cal.set(13, 0);
/* 55 */     cal.set(12, 0);
/* 56 */     cal.set(11, 0);
/* 57 */     return cal.getTime().getTime();
/*    */   }
/*    */   
/*    */   public static long computeStartOfNextWeek(long now) {
/* 61 */     Calendar cal = Calendar.getInstance();
/* 62 */     cal.setTime(new Date(now));
/*    */     
/* 64 */     cal.set(7, cal.getFirstDayOfWeek());
/* 65 */     cal.set(11, 0);
/* 66 */     cal.set(12, 0);
/* 67 */     cal.set(13, 0);
/* 68 */     cal.set(14, 0);
/* 69 */     cal.add(3, 1);
/* 70 */     return cal.getTime().getTime();
/*    */   }
/*    */   
/*    */   public static long computeStartOfNextMonth(long now) {
/* 74 */     Calendar cal = Calendar.getInstance();
/* 75 */     cal.setTime(new Date(now));
/*    */     
/* 77 */     cal.set(5, 1);
/* 78 */     cal.set(11, 0);
/* 79 */     cal.set(12, 0);
/* 80 */     cal.set(13, 0);
/* 81 */     cal.set(14, 0);
/* 82 */     cal.add(2, 1);
/* 83 */     return cal.getTime().getTime();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\TimeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */