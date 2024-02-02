/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.time.DayOfWeek;
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
/*     */ public enum Week
/*     */ {
/*  27 */   SUNDAY(1),
/*     */ 
/*     */ 
/*     */   
/*  31 */   MONDAY(2),
/*     */ 
/*     */ 
/*     */   
/*  35 */   TUESDAY(3),
/*     */ 
/*     */ 
/*     */   
/*  39 */   WEDNESDAY(4),
/*     */ 
/*     */ 
/*     */   
/*  43 */   THURSDAY(5),
/*     */ 
/*     */ 
/*     */   
/*  47 */   FRIDAY(6),
/*     */ 
/*     */ 
/*     */   
/*  51 */   SATURDAY(7);
/*     */   private static final String[] ALIASES;
/*     */   private static final Week[] ENUMS;
/*     */   private final int value;
/*     */   
/*     */   static {
/*  57 */     ALIASES = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
/*  58 */     ENUMS = values();
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
/*     */   
/*     */   Week(int value) {
/*  71 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValue() {
/*  80 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIso8601Value() {
/*  90 */     int iso8601IntValue = getValue() - 1;
/*  91 */     if (0 == iso8601IntValue) {
/*  92 */       iso8601IntValue = 7;
/*     */     }
/*  94 */     return iso8601IntValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toChinese() {
/* 104 */     return toChinese("星期");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toChinese(String weekNamePre) {
/* 115 */     switch (this) {
/*     */       case SUNDAY:
/* 117 */         return weekNamePre + "日";
/*     */       case MONDAY:
/* 119 */         return weekNamePre + "一";
/*     */       case TUESDAY:
/* 121 */         return weekNamePre + "二";
/*     */       case WEDNESDAY:
/* 123 */         return weekNamePre + "三";
/*     */       case THURSDAY:
/* 125 */         return weekNamePre + "四";
/*     */       case FRIDAY:
/* 127 */         return weekNamePre + "五";
/*     */       case SATURDAY:
/* 129 */         return weekNamePre + "六";
/*     */     } 
/* 131 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DayOfWeek toJdkDayOfWeek() {
/* 142 */     return DayOfWeek.of(getIso8601Value());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Week of(int calendarWeekIntValue) {
/* 159 */     if (calendarWeekIntValue > ENUMS.length || calendarWeekIntValue < 1) {
/* 160 */       return null;
/*     */     }
/* 162 */     return ENUMS[calendarWeekIntValue - 1];
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
/*     */   public static Week of(String name) throws IllegalArgumentException {
/* 174 */     Assert.notBlank(name);
/* 175 */     Week of = of(ArrayUtil.indexOfIgnoreCase((CharSequence[])ALIASES, name) + 1);
/* 176 */     if (null == of) {
/* 177 */       of = valueOf(name.toUpperCase());
/*     */     }
/* 179 */     return of;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Week of(DayOfWeek dayOfWeek) {
/* 197 */     Assert.notNull(dayOfWeek);
/* 198 */     int week = dayOfWeek.getValue() + 1;
/* 199 */     if (8 == week)
/*     */     {
/* 201 */       week = 1;
/*     */     }
/* 203 */     return of(week);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\Week.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */