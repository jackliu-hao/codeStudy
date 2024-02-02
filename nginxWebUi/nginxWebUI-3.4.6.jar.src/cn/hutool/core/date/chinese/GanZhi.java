/*    */ package cn.hutool.core.date.chinese;
/*    */ 
/*    */ import java.time.LocalDate;
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
/*    */ public class GanZhi
/*    */ {
/* 21 */   private static final String[] GAN = new String[] { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
/* 22 */   private static final String[] ZHI = new String[] { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String cyclicalm(int num) {
/* 31 */     return GAN[num % 10] + ZHI[num % 12];
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
/*    */   public static String getGanzhiOfYear(int year) {
/* 43 */     return cyclicalm(year - 1900 + 36);
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
/*    */   public static String getGanzhiOfMonth(int year, int month, int day) {
/* 57 */     int firstNode = SolarTerms.getTerm(year, month * 2 - 1);
/*    */     
/* 59 */     int monthOffset = (year - 1900) * 12 + month + 11;
/* 60 */     if (day >= firstNode) {
/* 61 */       monthOffset++;
/*    */     }
/* 63 */     return cyclicalm(monthOffset);
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
/*    */   public static String getGanzhiOfDay(int year, int month, int day) {
/* 77 */     long days = LocalDate.of(year, month, day).toEpochDay() - 1L;
/*    */     
/* 79 */     return cyclicalm((int)(days - LunarInfo.BASE_DAY + 41L));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\chinese\GanZhi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */