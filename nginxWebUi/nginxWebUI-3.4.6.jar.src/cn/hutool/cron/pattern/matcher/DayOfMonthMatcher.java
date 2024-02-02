/*    */ package cn.hutool.cron.pattern.matcher;
/*    */ 
/*    */ import cn.hutool.core.date.Month;
/*    */ import java.util.List;
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
/*    */ public class DayOfMonthMatcher
/*    */   extends BoolArrayMatcher
/*    */ {
/*    */   public DayOfMonthMatcher(List<Integer> intValueList) {
/* 21 */     super(intValueList);
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
/*    */   public boolean match(int value, int month, boolean isLeapYear) {
/* 33 */     return (match(Integer.valueOf(value)) || (value > 27 && 
/*    */       
/* 35 */       match(Integer.valueOf(31)) && isLastDayOfMonth(value, month, isLeapYear)));
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
/*    */ 
/*    */ 
/*    */   
/*    */   private static boolean isLastDayOfMonth(int value, int month, boolean isLeapYear) {
/* 51 */     return (value == Month.getLastDay(month - 1, isLeapYear));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\matcher\DayOfMonthMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */