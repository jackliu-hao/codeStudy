/*    */ package cn.hutool.cron.pattern;
/*    */ 
/*    */ import cn.hutool.core.date.Week;
/*    */ import java.time.LocalDateTime;
/*    */ import java.util.Calendar;
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
/*    */ class PatternUtil
/*    */ {
/*    */   static int[] getFields(LocalDateTime dateTime, boolean isMatchSecond) {
/* 26 */     int second = isMatchSecond ? dateTime.getSecond() : -1;
/* 27 */     int minute = dateTime.getMinute();
/* 28 */     int hour = dateTime.getHour();
/* 29 */     int dayOfMonth = dateTime.getDayOfMonth();
/* 30 */     int month = dateTime.getMonthValue();
/* 31 */     int dayOfWeek = Week.of(dateTime.getDayOfWeek()).getValue() - 1;
/* 32 */     int year = dateTime.getYear();
/* 33 */     return new int[] { second, minute, hour, dayOfMonth, month, dayOfWeek, year };
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
/*    */   
/*    */   static int[] getFields(Calendar calendar, boolean isMatchSecond) {
/* 46 */     int second = isMatchSecond ? calendar.get(13) : -1;
/* 47 */     int minute = calendar.get(12);
/* 48 */     int hour = calendar.get(11);
/* 49 */     int dayOfMonth = calendar.get(5);
/* 50 */     int month = calendar.get(2) + 1;
/* 51 */     int dayOfWeek = calendar.get(7) - 1;
/* 52 */     int year = calendar.get(1);
/* 53 */     return new int[] { second, minute, hour, dayOfMonth, month, dayOfWeek, year };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\PatternUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */