/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import java.time.DayOfWeek;
/*     */ import java.time.Duration;
/*     */ import java.time.temporal.ChronoUnit;
/*     */ import java.time.temporal.Temporal;
/*     */ import java.time.temporal.TemporalAdjusters;
/*     */ import java.time.temporal.TemporalUnit;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class TemporalUtil
/*     */ {
/*     */   public static Duration between(Temporal startTimeInclude, Temporal endTimeExclude) {
/*  29 */     return Duration.between(startTimeInclude, endTimeExclude);
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
/*     */   public static long between(Temporal startTimeInclude, Temporal endTimeExclude, ChronoUnit unit) {
/*  43 */     return unit.between(startTimeInclude, endTimeExclude);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ChronoUnit toChronoUnit(TimeUnit unit) throws IllegalArgumentException {
/*  54 */     if (null == unit) {
/*  55 */       return null;
/*     */     }
/*  57 */     switch (unit) {
/*     */       case NANOS:
/*  59 */         return ChronoUnit.NANOS;
/*     */       case MICROS:
/*  61 */         return ChronoUnit.MICROS;
/*     */       case MILLIS:
/*  63 */         return ChronoUnit.MILLIS;
/*     */       case SECONDS:
/*  65 */         return ChronoUnit.SECONDS;
/*     */       case MINUTES:
/*  67 */         return ChronoUnit.MINUTES;
/*     */       case HOURS:
/*  69 */         return ChronoUnit.HOURS;
/*     */       case DAYS:
/*  71 */         return ChronoUnit.DAYS;
/*     */     } 
/*  73 */     throw new IllegalArgumentException("Unknown TimeUnit constant");
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
/*     */   public static TimeUnit toTimeUnit(ChronoUnit unit) throws IllegalArgumentException {
/*  86 */     if (null == unit) {
/*  87 */       return null;
/*     */     }
/*  89 */     switch (unit) {
/*     */       case NANOS:
/*  91 */         return TimeUnit.NANOSECONDS;
/*     */       case MICROS:
/*  93 */         return TimeUnit.MICROSECONDS;
/*     */       case MILLIS:
/*  95 */         return TimeUnit.MILLISECONDS;
/*     */       case SECONDS:
/*  97 */         return TimeUnit.SECONDS;
/*     */       case MINUTES:
/*  99 */         return TimeUnit.MINUTES;
/*     */       case HOURS:
/* 101 */         return TimeUnit.HOURS;
/*     */       case DAYS:
/* 103 */         return TimeUnit.DAYS;
/*     */     } 
/* 105 */     throw new IllegalArgumentException("ChronoUnit cannot be converted to TimeUnit: " + unit);
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
/*     */   public static <T extends Temporal> T offset(T time, long number, TemporalUnit field) {
/* 120 */     if (null == time) {
/* 121 */       return null;
/*     */     }
/*     */     
/* 124 */     return (T)time.plus(number, field);
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
/*     */   public <T extends Temporal> T offset(T temporal, DayOfWeek dayOfWeek, boolean isPrevious) {
/* 139 */     return (T)temporal.with(isPrevious ? TemporalAdjusters.previous(dayOfWeek) : TemporalAdjusters.next(dayOfWeek));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\TemporalUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */