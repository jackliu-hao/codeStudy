/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.io.Serializable;
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
/*     */ public class DateBetween
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Date begin;
/*     */   private final Date end;
/*     */   
/*     */   public static DateBetween create(Date begin, Date end) {
/*  36 */     return new DateBetween(begin, end);
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
/*     */   public static DateBetween create(Date begin, Date end, boolean isAbs) {
/*  50 */     return new DateBetween(begin, end, isAbs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DateBetween(Date begin, Date end) {
/*  61 */     this(begin, end, true);
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
/*     */   public DateBetween(Date begin, Date end, boolean isAbs) {
/*  74 */     Assert.notNull(begin, "Begin date is null !", new Object[0]);
/*  75 */     Assert.notNull(end, "End date is null !", new Object[0]);
/*     */     
/*  77 */     if (isAbs && begin.after(end)) {
/*     */       
/*  79 */       this.begin = end;
/*  80 */       this.end = begin;
/*     */     } else {
/*  82 */       this.begin = begin;
/*  83 */       this.end = end;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long between(DateUnit unit) {
/*  95 */     long diff = this.end.getTime() - this.begin.getTime();
/*  96 */     return diff / unit.getMillis();
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
/*     */   public long betweenMonth(boolean isReset) {
/* 108 */     Calendar beginCal = DateUtil.calendar(this.begin);
/* 109 */     Calendar endCal = DateUtil.calendar(this.end);
/*     */     
/* 111 */     int betweenYear = endCal.get(1) - beginCal.get(1);
/* 112 */     int betweenMonthOfYear = endCal.get(2) - beginCal.get(2);
/*     */     
/* 114 */     int result = betweenYear * 12 + betweenMonthOfYear;
/* 115 */     if (false == isReset) {
/* 116 */       endCal.set(1, beginCal.get(1));
/* 117 */       endCal.set(2, beginCal.get(2));
/* 118 */       long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
/* 119 */       if (between < 0L) {
/* 120 */         return (result - 1);
/*     */       }
/*     */     } 
/* 123 */     return result;
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
/*     */   public long betweenYear(boolean isReset) {
/* 135 */     Calendar beginCal = DateUtil.calendar(this.begin);
/* 136 */     Calendar endCal = DateUtil.calendar(this.end);
/*     */     
/* 138 */     int result = endCal.get(1) - beginCal.get(1);
/* 139 */     if (false == isReset) {
/*     */       
/* 141 */       if (1 == beginCal.get(2) && 1 == endCal.get(2) && 
/* 142 */         beginCal.get(5) == beginCal.getActualMaximum(5) && endCal
/* 143 */         .get(5) == endCal.getActualMaximum(5)) {
/*     */         
/* 145 */         beginCal.set(5, 1);
/* 146 */         endCal.set(5, 1);
/*     */       } 
/*     */ 
/*     */       
/* 150 */       endCal.set(1, beginCal.get(1));
/* 151 */       long between = endCal.getTimeInMillis() - beginCal.getTimeInMillis();
/* 152 */       if (between < 0L) {
/* 153 */         return (result - 1);
/*     */       }
/*     */     } 
/* 156 */     return result;
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
/*     */   public String toString(DateUnit unit, BetweenFormatter.Level level) {
/* 168 */     return DateUtil.formatBetween(between(unit), level);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(BetweenFormatter.Level level) {
/* 178 */     return toString(DateUnit.MS, level);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 183 */     return toString(BetweenFormatter.Level.MILLISECOND);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateBetween.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */