/*     */ package org.h2.api;
/*     */ 
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.IntervalUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Interval
/*     */ {
/*     */   private final IntervalQualifier qualifier;
/*     */   private final boolean negative;
/*     */   private final long leading;
/*     */   private final long remaining;
/*     */   
/*     */   public static Interval ofYears(long paramLong) {
/*  48 */     return new Interval(IntervalQualifier.YEAR, (paramLong < 0L), Math.abs(paramLong), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Interval ofMonths(long paramLong) {
/*  59 */     return new Interval(IntervalQualifier.MONTH, (paramLong < 0L), Math.abs(paramLong), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Interval ofDays(long paramLong) {
/*  70 */     return new Interval(IntervalQualifier.DAY, (paramLong < 0L), Math.abs(paramLong), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Interval ofHours(long paramLong) {
/*  81 */     return new Interval(IntervalQualifier.HOUR, (paramLong < 0L), Math.abs(paramLong), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Interval ofMinutes(long paramLong) {
/*  92 */     return new Interval(IntervalQualifier.MINUTE, (paramLong < 0L), Math.abs(paramLong), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Interval ofSeconds(long paramLong) {
/* 103 */     return new Interval(IntervalQualifier.SECOND, (paramLong < 0L), Math.abs(paramLong), 0L);
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
/*     */   public static Interval ofSeconds(long paramLong, int paramInt) {
/* 121 */     boolean bool = ((paramLong | paramInt) < 0L) ? true : false;
/* 122 */     if (bool) {
/*     */       
/* 124 */       if (paramLong > 0L || paramInt > 0) {
/* 125 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 128 */       paramLong = -paramLong;
/* 129 */       paramInt = -paramInt;
/*     */     } 
/*     */ 
/*     */     
/* 133 */     return new Interval(IntervalQualifier.SECOND, bool, paramLong, paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Interval ofNanos(long paramLong) {
/* 144 */     boolean bool = (paramLong < 0L) ? true : false;
/* 145 */     if (bool) {
/* 146 */       paramLong = -paramLong;
/* 147 */       if (paramLong < 0L)
/*     */       {
/* 149 */         return new Interval(IntervalQualifier.SECOND, true, 9223372036L, 854775808L);
/*     */       }
/*     */     } 
/* 152 */     return new Interval(IntervalQualifier.SECOND, bool, paramLong / 1000000000L, paramLong % 1000000000L);
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
/*     */   public static Interval ofYearsMonths(long paramLong, int paramInt) {
/* 170 */     boolean bool = ((paramLong | paramInt) < 0L) ? true : false;
/* 171 */     if (bool) {
/*     */       
/* 173 */       if (paramLong > 0L || paramInt > 0) {
/* 174 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 177 */       paramLong = -paramLong;
/* 178 */       paramInt = -paramInt;
/*     */     } 
/*     */ 
/*     */     
/* 182 */     return new Interval(IntervalQualifier.YEAR_TO_MONTH, bool, paramLong, paramInt);
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
/*     */   public static Interval ofDaysHours(long paramLong, int paramInt) {
/* 200 */     boolean bool = ((paramLong | paramInt) < 0L) ? true : false;
/* 201 */     if (bool) {
/*     */       
/* 203 */       if (paramLong > 0L || paramInt > 0) {
/* 204 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 207 */       paramLong = -paramLong;
/* 208 */       paramInt = -paramInt;
/*     */     } 
/*     */ 
/*     */     
/* 212 */     return new Interval(IntervalQualifier.DAY_TO_HOUR, bool, paramLong, paramInt);
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
/*     */   public static Interval ofDaysHoursMinutes(long paramLong, int paramInt1, int paramInt2) {
/* 232 */     boolean bool = ((paramLong | paramInt1 | paramInt2) < 0L) ? true : false;
/* 233 */     if (bool) {
/*     */       
/* 235 */       if (paramLong > 0L || paramInt1 > 0 || paramInt2 > 0) {
/* 236 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 239 */       paramLong = -paramLong;
/* 240 */       paramInt1 = -paramInt1;
/* 241 */       paramInt2 = -paramInt2;
/* 242 */       if ((paramInt1 | paramInt2) < 0)
/*     */       {
/* 244 */         throw new IllegalArgumentException();
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 250 */     if (paramInt2 >= 60) {
/* 251 */       throw new IllegalArgumentException();
/*     */     }
/* 253 */     return new Interval(IntervalQualifier.DAY_TO_MINUTE, bool, paramLong, paramInt1 * 60L + paramInt2);
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
/*     */   public static Interval ofDaysHoursMinutesSeconds(long paramLong, int paramInt1, int paramInt2, int paramInt3) {
/* 274 */     return ofDaysHoursMinutesNanos(paramLong, paramInt1, paramInt2, paramInt3 * 1000000000L);
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
/*     */   public static Interval ofDaysHoursMinutesNanos(long paramLong1, int paramInt1, int paramInt2, long paramLong2) {
/* 296 */     boolean bool = ((paramLong1 | paramInt1 | paramInt2 | paramLong2) < 0L) ? true : false;
/* 297 */     if (bool) {
/*     */       
/* 299 */       if (paramLong1 > 0L || paramInt1 > 0 || paramInt2 > 0 || paramLong2 > 0L) {
/* 300 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 303 */       paramLong1 = -paramLong1;
/* 304 */       paramInt1 = -paramInt1;
/* 305 */       paramInt2 = -paramInt2;
/* 306 */       paramLong2 = -paramLong2;
/* 307 */       if (((paramInt1 | paramInt2) | paramLong2) < 0L)
/*     */       {
/* 309 */         throw new IllegalArgumentException();
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 315 */     if (paramInt2 >= 60 || paramLong2 >= 60000000000L) {
/* 316 */       throw new IllegalArgumentException();
/*     */     }
/* 318 */     return new Interval(IntervalQualifier.DAY_TO_SECOND, bool, paramLong1, (paramInt1 * 60L + paramInt2) * 60000000000L + paramLong2);
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
/*     */   public static Interval ofHoursMinutes(long paramLong, int paramInt) {
/* 337 */     boolean bool = ((paramLong | paramInt) < 0L) ? true : false;
/* 338 */     if (bool) {
/*     */       
/* 340 */       if (paramLong > 0L || paramInt > 0) {
/* 341 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 344 */       paramLong = -paramLong;
/* 345 */       paramInt = -paramInt;
/*     */     } 
/*     */ 
/*     */     
/* 349 */     return new Interval(IntervalQualifier.HOUR_TO_MINUTE, bool, paramLong, paramInt);
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
/*     */   public static Interval ofHoursMinutesSeconds(long paramLong, int paramInt1, int paramInt2) {
/* 368 */     return ofHoursMinutesNanos(paramLong, paramInt1, paramInt2 * 1000000000L);
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
/*     */   public static Interval ofHoursMinutesNanos(long paramLong1, int paramInt, long paramLong2) {
/* 388 */     boolean bool = ((paramLong1 | paramInt | paramLong2) < 0L) ? true : false;
/* 389 */     if (bool) {
/*     */       
/* 391 */       if (paramLong1 > 0L || paramInt > 0 || paramLong2 > 0L) {
/* 392 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 395 */       paramLong1 = -paramLong1;
/* 396 */       paramInt = -paramInt;
/* 397 */       paramLong2 = -paramLong2;
/* 398 */       if ((paramInt | paramLong2) < 0L)
/*     */       {
/* 400 */         throw new IllegalArgumentException();
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 406 */     if (paramLong2 >= 60000000000L) {
/* 407 */       throw new IllegalArgumentException();
/*     */     }
/* 409 */     return new Interval(IntervalQualifier.HOUR_TO_SECOND, bool, paramLong1, paramInt * 60000000000L + paramLong2);
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
/*     */   public static Interval ofMinutesSeconds(long paramLong, int paramInt) {
/* 426 */     return ofMinutesNanos(paramLong, paramInt * 1000000000L);
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
/*     */   public static Interval ofMinutesNanos(long paramLong1, long paramLong2) {
/* 444 */     boolean bool = ((paramLong1 | paramLong2) < 0L) ? true : false;
/* 445 */     if (bool) {
/*     */       
/* 447 */       if (paramLong1 > 0L || paramLong2 > 0L) {
/* 448 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 451 */       paramLong1 = -paramLong1;
/* 452 */       paramLong2 = -paramLong2;
/*     */     } 
/*     */     
/* 455 */     return new Interval(IntervalQualifier.MINUTE_TO_SECOND, bool, paramLong1, paramLong2);
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
/*     */   public Interval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/* 472 */     this.qualifier = paramIntervalQualifier;
/*     */     try {
/* 474 */       this.negative = IntervalUtils.validateInterval(paramIntervalQualifier, paramBoolean, paramLong1, paramLong2);
/* 475 */     } catch (DbException dbException) {
/* 476 */       throw new IllegalArgumentException();
/*     */     } 
/* 478 */     this.leading = paramLong1;
/* 479 */     this.remaining = paramLong2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalQualifier getQualifier() {
/* 488 */     return this.qualifier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNegative() {
/* 497 */     return this.negative;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLeading() {
/* 507 */     return this.leading;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 517 */     return this.remaining;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getYears() {
/* 526 */     return IntervalUtils.yearsFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMonths() {
/* 535 */     return IntervalUtils.monthsFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDays() {
/* 544 */     return IntervalUtils.daysFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getHours() {
/* 553 */     return IntervalUtils.hoursFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMinutes() {
/* 562 */     return IntervalUtils.minutesFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSeconds() {
/* 571 */     if (this.qualifier == IntervalQualifier.SECOND) {
/* 572 */       return this.negative ? -this.leading : this.leading;
/*     */     }
/* 574 */     return getSecondsAndNanos() / 1000000000L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getNanosOfSecond() {
/* 583 */     if (this.qualifier == IntervalQualifier.SECOND) {
/* 584 */       return this.negative ? -this.remaining : this.remaining;
/*     */     }
/* 586 */     return getSecondsAndNanos() % 1000000000L;
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
/*     */   public long getSecondsAndNanos() {
/* 602 */     return IntervalUtils.nanosFromInterval(this.qualifier, this.negative, this.leading, this.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 608 */     int i = 1;
/* 609 */     i = 31 * i + this.qualifier.hashCode();
/* 610 */     i = 31 * i + (this.negative ? 1231 : 1237);
/* 611 */     i = 31 * i + (int)(this.leading ^ this.leading >>> 32L);
/* 612 */     i = 31 * i + (int)(this.remaining ^ this.remaining >>> 32L);
/* 613 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 618 */     if (this == paramObject) {
/* 619 */       return true;
/*     */     }
/* 621 */     if (!(paramObject instanceof Interval)) {
/* 622 */       return false;
/*     */     }
/* 624 */     Interval interval = (Interval)paramObject;
/* 625 */     return (this.qualifier == interval.qualifier && this.negative == interval.negative && this.leading == interval.leading && this.remaining == interval.remaining);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 631 */     return IntervalUtils.appendInterval(new StringBuilder(), getQualifier(), this.negative, this.leading, this.remaining)
/* 632 */       .toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\Interval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */