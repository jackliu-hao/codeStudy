/*     */ package cn.hutool.cron.pattern;
/*     */ 
/*     */ import cn.hutool.core.date.Month;
/*     */ import cn.hutool.core.date.Week;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.cron.CronException;
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
/*     */ public enum Part
/*     */ {
/*  23 */   SECOND(13, 0, 59),
/*  24 */   MINUTE(12, 0, 59),
/*  25 */   HOUR(11, 0, 23),
/*  26 */   DAY_OF_MONTH(5, 1, 31),
/*  27 */   MONTH(2, Month.JANUARY.getValueBaseOne(), Month.DECEMBER.getValueBaseOne()),
/*  28 */   DAY_OF_WEEK(7, Week.SUNDAY.ordinal(), Week.SATURDAY.ordinal()),
/*  29 */   YEAR(1, 1970, 2099);
/*     */   
/*     */   static {
/*  32 */     ENUMS = values();
/*     */   }
/*     */ 
/*     */   
/*     */   private static final Part[] ENUMS;
/*     */   
/*     */   private final int calendarField;
/*     */   
/*     */   private final int min;
/*     */   
/*     */   private final int max;
/*     */ 
/*     */   
/*     */   Part(int calendarField, int min, int max) {
/*  46 */     this.calendarField = calendarField;
/*  47 */     if (min > max) {
/*  48 */       this.min = max;
/*  49 */       this.max = min;
/*     */     } else {
/*  51 */       this.min = min;
/*  52 */       this.max = max;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCalendarField() {
/*  62 */     return this.calendarField;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMin() {
/*  71 */     return this.min;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMax() {
/*  80 */     return this.max;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int checkValue(int value) throws CronException {
/*  91 */     Assert.checkBetween(value, this.min, this.max, () -> new CronException("Value {} out of range: [{} , {}]", new Object[] { Integer.valueOf(value), Integer.valueOf(this.min), Integer.valueOf(this.max) }));
/*     */     
/*  93 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Part of(int i) {
/* 103 */     return ENUMS[i];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\Part.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */