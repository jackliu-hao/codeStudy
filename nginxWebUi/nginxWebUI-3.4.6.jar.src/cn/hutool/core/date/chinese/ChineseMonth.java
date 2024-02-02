/*    */ package cn.hutool.core.date.chinese;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChineseMonth
/*    */ {
/* 11 */   private static final String[] MONTH_NAME = new String[] { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二" };
/* 12 */   private static final String[] MONTH_NAME_TRADITIONAL = new String[] { "正", "二", "三", "四", "五", "六", "七", "八", "九", "寒", "冬", "腊" };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isLeapMonth(int year, int month) {
/* 23 */     return (month == LunarInfo.leapMonth(year));
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
/*    */   public static String getChineseMonthName(boolean isLeapMonth, int month, boolean isTraditional) {
/* 37 */     return (isLeapMonth ? "闰" : "") + (isTraditional ? MONTH_NAME_TRADITIONAL : MONTH_NAME)[month - 1] + "月";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\chinese\ChineseMonth.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */