/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Zodiac
/*     */ {
/*  15 */   private static final int[] DAY_ARR = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
/*     */   
/*  17 */   private static final String[] ZODIACS = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
/*  18 */   private static final String[] CHINESE_ZODIACS = new String[] { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getZodiac(Date date) {
/*  27 */     return getZodiac(DateUtil.calendar(date));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getZodiac(Calendar calendar) {
/*  37 */     if (null == calendar) {
/*  38 */       return null;
/*     */     }
/*  40 */     return getZodiac(calendar.get(2), calendar.get(5));
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
/*     */   public static String getZodiac(Month month, int day) {
/*  52 */     return getZodiac(month.getValue(), day);
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
/*     */   public static String getZodiac(int month, int day) {
/*  64 */     return (day < DAY_ARR[month]) ? ZODIACS[month] : ZODIACS[month + 1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getChineseZodiac(Date date) {
/*  75 */     return getChineseZodiac(DateUtil.calendar(date));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getChineseZodiac(Calendar calendar) {
/*  85 */     if (null == calendar) {
/*  86 */       return null;
/*     */     }
/*  88 */     return getChineseZodiac(calendar.get(1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getChineseZodiac(int year) {
/*  98 */     if (year < 1900) {
/*  99 */       return null;
/*     */     }
/* 101 */     return CHINESE_ZODIACS[(year - 1900) % CHINESE_ZODIACS.length];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\Zodiac.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */