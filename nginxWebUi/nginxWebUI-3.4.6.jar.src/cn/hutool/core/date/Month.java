/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.time.format.TextStyle;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum Month
/*     */ {
/*  33 */   JANUARY(0),
/*     */ 
/*     */ 
/*     */   
/*  37 */   FEBRUARY(1),
/*     */ 
/*     */ 
/*     */   
/*  41 */   MARCH(2),
/*     */ 
/*     */ 
/*     */   
/*  45 */   APRIL(3),
/*     */ 
/*     */ 
/*     */   
/*  49 */   MAY(4),
/*     */ 
/*     */ 
/*     */   
/*  53 */   JUNE(5),
/*     */ 
/*     */ 
/*     */   
/*  57 */   JULY(6),
/*     */ 
/*     */ 
/*     */   
/*  61 */   AUGUST(7),
/*     */ 
/*     */ 
/*     */   
/*  65 */   SEPTEMBER(8),
/*     */ 
/*     */ 
/*     */   
/*  69 */   OCTOBER(9),
/*     */ 
/*     */ 
/*     */   
/*  73 */   NOVEMBER(10),
/*     */ 
/*     */ 
/*     */   
/*  77 */   DECEMBER(11),
/*     */ 
/*     */ 
/*     */   
/*  81 */   UNDECIMBER(12);
/*     */   private static final String[] ALIASES;
/*     */   private static final Month[] ENUMS;
/*     */   private final int value;
/*     */   
/*     */   static {
/*  87 */     ALIASES = new String[] { "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
/*  88 */     ENUMS = values();
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
/*     */   Month(int value) {
/* 101 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValue() {
/* 111 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValueBaseOne() {
/* 122 */     Assert.isFalse((this == UNDECIMBER), "Unsupported UNDECIMBER Field", new Object[0]);
/* 123 */     return getValue() + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLastDay(boolean isLeapYear) {
/* 133 */     switch (this) {
/*     */       case FEBRUARY:
/* 135 */         return isLeapYear ? 29 : 28;
/*     */       case APRIL:
/*     */       case JUNE:
/*     */       case SEPTEMBER:
/*     */       case NOVEMBER:
/* 140 */         return 30;
/*     */     } 
/* 142 */     return 31;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Month of(int calendarMonthIntValue) {
/* 167 */     if (calendarMonthIntValue >= ENUMS.length || calendarMonthIntValue < 0) {
/* 168 */       return null;
/*     */     }
/* 170 */     return ENUMS[calendarMonthIntValue];
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
/*     */   public static Month of(String name) throws IllegalArgumentException {
/* 182 */     Assert.notBlank(name);
/* 183 */     Month of = of(ArrayUtil.indexOfIgnoreCase((CharSequence[])ALIASES, name));
/* 184 */     if (null == of) {
/* 185 */       of = valueOf(name.toUpperCase());
/*     */     }
/* 187 */     return of;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Month of(java.time.Month month) {
/* 197 */     return of(month.ordinal());
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
/*     */   public static int getLastDay(int month, boolean isLeapYear) {
/* 209 */     Month of = of(month);
/* 210 */     Assert.notNull(of, "Invalid Month base 0: " + month, new Object[0]);
/* 211 */     return of.getLastDay(isLeapYear);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public java.time.Month toJdkMonth() {
/* 221 */     return java.time.Month.of(getValueBaseOne());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDisplayName(TextStyle style) {
/* 232 */     return getDisplayName(style, Locale.getDefault());
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
/*     */   public String getDisplayName(TextStyle style, Locale locale) {
/* 244 */     return toJdkMonth().getDisplayName(style, locale);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\Month.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */