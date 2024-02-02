/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import java.time.temporal.ChronoUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum DateUnit
/*     */ {
/*  14 */   MS(1L),
/*     */ 
/*     */ 
/*     */   
/*  18 */   SECOND(1000L),
/*     */ 
/*     */ 
/*     */   
/*  22 */   MINUTE(SECOND.getMillis() * 60L),
/*     */ 
/*     */ 
/*     */   
/*  26 */   HOUR(MINUTE.getMillis() * 60L),
/*     */ 
/*     */ 
/*     */   
/*  30 */   DAY(HOUR.getMillis() * 24L),
/*     */ 
/*     */ 
/*     */   
/*  34 */   WEEK(DAY.getMillis() * 7L);
/*     */   
/*     */   private final long millis;
/*     */   
/*     */   DateUnit(long millis) {
/*  39 */     this.millis = millis;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMillis() {
/*  46 */     return this.millis;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChronoUnit toChronoUnit() {
/*  56 */     return toChronoUnit(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DateUnit of(ChronoUnit unit) {
/*  67 */     switch (unit) {
/*     */       case MS:
/*  69 */         return MS;
/*     */       case SECOND:
/*  71 */         return SECOND;
/*     */       case MINUTE:
/*  73 */         return MINUTE;
/*     */       case HOUR:
/*  75 */         return HOUR;
/*     */       case DAY:
/*  77 */         return DAY;
/*     */       case WEEK:
/*  79 */         return WEEK;
/*     */     } 
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ChronoUnit toChronoUnit(DateUnit unit) {
/*  92 */     switch (unit) {
/*     */       case MS:
/*  94 */         return ChronoUnit.MICROS;
/*     */       case SECOND:
/*  96 */         return ChronoUnit.SECONDS;
/*     */       case MINUTE:
/*  98 */         return ChronoUnit.MINUTES;
/*     */       case HOUR:
/* 100 */         return ChronoUnit.HOURS;
/*     */       case DAY:
/* 102 */         return ChronoUnit.DAYS;
/*     */       case WEEK:
/* 104 */         return ChronoUnit.WEEKS;
/*     */     } 
/* 106 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */