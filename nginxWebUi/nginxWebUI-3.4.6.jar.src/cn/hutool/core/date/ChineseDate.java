/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.convert.NumberChineseFormatter;
/*     */ import cn.hutool.core.date.chinese.ChineseMonth;
/*     */ import cn.hutool.core.date.chinese.GanZhi;
/*     */ import cn.hutool.core.date.chinese.LunarFestival;
/*     */ import cn.hutool.core.date.chinese.LunarInfo;
/*     */ import cn.hutool.core.date.chinese.SolarTerms;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.time.LocalDate;
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
/*     */ public class ChineseDate
/*     */ {
/*     */   private final int year;
/*     */   private final int month;
/*     */   private final boolean isLeapMonth;
/*     */   private final int day;
/*     */   private final int gyear;
/*     */   private final int gmonthBase1;
/*     */   private final int gday;
/*     */   
/*     */   public ChineseDate(Date date) {
/*  51 */     this(LocalDateTimeUtil.ofDate(date.toInstant()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChineseDate(LocalDate localDate) {
/*  62 */     this.gyear = localDate.getYear();
/*  63 */     this.gmonthBase1 = localDate.getMonthValue();
/*  64 */     this.gday = localDate.getDayOfMonth();
/*     */ 
/*     */     
/*  67 */     int offset = (int)(localDate.toEpochDay() - LunarInfo.BASE_DAY);
/*     */ 
/*     */     
/*     */     int iYear;
/*     */ 
/*     */     
/*  73 */     for (iYear = 1900; iYear <= LunarInfo.MAX_YEAR; iYear++) {
/*  74 */       int daysOfYear = LunarInfo.yearDays(iYear);
/*  75 */       if (offset < daysOfYear) {
/*     */         break;
/*     */       }
/*  78 */       offset -= daysOfYear;
/*     */     } 
/*     */     
/*  81 */     this.year = iYear;
/*     */     
/*  83 */     int leapMonth = LunarInfo.leapMonth(iYear);
/*     */ 
/*     */ 
/*     */     
/*  87 */     boolean hasLeapMonth = false; int month;
/*  88 */     for (month = 1; month < 13; month++) {
/*     */       int daysOfMonth;
/*  90 */       if (leapMonth > 0 && month == leapMonth + 1) {
/*  91 */         daysOfMonth = LunarInfo.leapDays(this.year);
/*  92 */         hasLeapMonth = true;
/*     */       }
/*     */       else {
/*     */         
/*  96 */         daysOfMonth = LunarInfo.monthDays(this.year, hasLeapMonth ? (month - 1) : month);
/*     */       } 
/*     */       
/*  99 */       if (offset < daysOfMonth) {
/*     */         break;
/*     */       }
/*     */       
/* 103 */       offset -= daysOfMonth;
/*     */     } 
/*     */     
/* 106 */     this.isLeapMonth = (leapMonth > 0 && month == leapMonth + 1);
/* 107 */     if (hasLeapMonth && false == this.isLeapMonth)
/*     */     {
/* 109 */       month--;
/*     */     }
/* 111 */     this.month = month;
/* 112 */     this.day = offset + 1;
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
/*     */   public ChineseDate(int chineseYear, int chineseMonth, int chineseDay) {
/* 125 */     this(chineseYear, chineseMonth, chineseDay, (chineseMonth == LunarInfo.leapMonth(chineseYear)));
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
/*     */   public ChineseDate(int chineseYear, int chineseMonth, int chineseDay, boolean isLeapMonth) {
/* 139 */     this.day = chineseDay;
/*     */     
/* 141 */     this.isLeapMonth = isLeapMonth;
/*     */     
/* 143 */     this.month = isLeapMonth ? (chineseMonth + 1) : chineseMonth;
/* 144 */     this.year = chineseYear;
/*     */     
/* 146 */     DateTime dateTime = lunar2solar(chineseYear, chineseMonth, chineseDay, isLeapMonth);
/* 147 */     if (null != dateTime) {
/*     */       
/* 149 */       this.gday = dateTime.dayOfMonth();
/*     */       
/* 151 */       this.gmonthBase1 = dateTime.month() + 1;
/*     */       
/* 153 */       this.gyear = dateTime.year();
/*     */     } else {
/*     */       
/* 156 */       this.gday = -1;
/*     */       
/* 158 */       this.gmonthBase1 = -1;
/*     */       
/* 160 */       this.gyear = -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getChineseYear() {
/* 170 */     return this.year;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGregorianYear() {
/* 180 */     return this.gyear;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMonth() {
/* 191 */     return this.month;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGregorianMonthBase1() {
/* 201 */     return this.gmonthBase1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGregorianMonth() {
/* 211 */     return this.gmonthBase1 - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLeapMonth() {
/* 221 */     return this.isLeapMonth;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getChineseMonth() {
/* 231 */     return getChineseMonth(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getChineseMonthName() {
/* 240 */     return getChineseMonth(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getChineseMonth(boolean isTraditional) {
/* 251 */     return ChineseMonth.getChineseMonthName(isLeapMonth(), 
/* 252 */         isLeapMonth() ? (this.month - 1) : this.month, isTraditional);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDay() {
/* 262 */     return this.day;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGregorianDay() {
/* 272 */     return this.gday;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getChineseDay() {
/* 281 */     String[] chineseTen = { "初", "十", "廿", "卅" };
/* 282 */     int n = (this.day % 10 == 0) ? 9 : (this.day % 10 - 1);
/* 283 */     if (this.day > 30) {
/* 284 */       return "";
/*     */     }
/* 286 */     switch (this.day) {
/*     */       case 10:
/* 288 */         return "初十";
/*     */       case 20:
/* 290 */         return "二十";
/*     */       case 30:
/* 292 */         return "三十";
/*     */     } 
/* 294 */     return chineseTen[this.day / 10] + NumberChineseFormatter.format((n + 1), false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getGregorianDate() {
/* 305 */     return DateUtil.date(getGregorianCalendar());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Calendar getGregorianCalendar() {
/* 315 */     Calendar calendar = CalendarUtil.calendar();
/*     */     
/* 317 */     calendar.set(this.gyear, getGregorianMonth(), this.gday, 0, 0, 0);
/* 318 */     return calendar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFestivals() {
/* 327 */     return StrUtil.join(",", LunarFestival.getFestivals(this.year, this.month, this.day));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getChineseZodiac() {
/* 336 */     return Zodiac.getChineseZodiac(this.year);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCyclical() {
/* 346 */     return GanZhi.getGanzhiOfYear(this.year);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCyclicalYMD() {
/* 355 */     if (this.gyear >= 1900 && this.gmonthBase1 > 0 && this.gday > 0) {
/* 356 */       return cyclicalm(this.gyear, this.gmonthBase1, this.gday);
/*     */     }
/* 358 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTerm() {
/* 369 */     return SolarTerms.getTerm(this.gyear, this.gmonthBase1, this.gday);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toStringNormal() {
/* 380 */     return String.format("%04d-%02d-%02d", new Object[] { Integer.valueOf(this.year), 
/* 381 */           Integer.valueOf(isLeapMonth() ? (this.month - 1) : this.month), Integer.valueOf(this.day) });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 386 */     return String.format("%s%s年 %s%s", new Object[] { getCyclical(), getChineseZodiac(), getChineseMonthName(), getChineseDay() });
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
/*     */   private String cyclicalm(int year, int month, int day) {
/* 400 */     return StrUtil.format("{}年{}月{}日", new Object[] {
/* 401 */           GanZhi.getGanzhiOfYear(this.year), 
/* 402 */           GanZhi.getGanzhiOfMonth(year, month, day), 
/* 403 */           GanZhi.getGanzhiOfDay(year, month, day)
/*     */         });
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
/*     */   private DateTime lunar2solar(int chineseYear, int chineseMonth, int chineseDay, boolean isLeapMonth) {
/* 417 */     if ((chineseYear == 2100 && chineseMonth == 12 && chineseDay > 1) || (chineseYear == 1900 && chineseMonth == 1 && chineseDay < 31))
/*     */     {
/* 419 */       return null;
/*     */     }
/* 421 */     int day = LunarInfo.monthDays(chineseYear, chineseMonth);
/* 422 */     int _day = day;
/* 423 */     if (isLeapMonth) {
/* 424 */       _day = LunarInfo.leapDays(chineseYear);
/*     */     }
/*     */     
/* 427 */     if (chineseYear < 1900 || chineseYear > 2100 || chineseDay > _day) {
/* 428 */       return null;
/*     */     }
/*     */     
/* 431 */     int offset = 0;
/* 432 */     for (int i = 1900; i < chineseYear; i++) {
/* 433 */       offset += LunarInfo.yearDays(i);
/*     */     }
/*     */     
/* 436 */     boolean isAdd = false;
/* 437 */     for (int j = 1; j < chineseMonth; j++) {
/* 438 */       int leap = LunarInfo.leapMonth(chineseYear);
/* 439 */       if (false == isAdd && 
/* 440 */         leap <= j && leap > 0) {
/* 441 */         offset += LunarInfo.leapDays(chineseYear);
/* 442 */         isAdd = true;
/*     */       } 
/*     */       
/* 445 */       offset += LunarInfo.monthDays(chineseYear, j);
/*     */     } 
/*     */     
/* 448 */     if (isLeapMonth) {
/* 449 */       offset += day;
/*     */     }
/*     */     
/* 452 */     return DateUtil.date((offset + chineseDay - 31) * 86400000L - 2203804800000L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\ChineseDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */