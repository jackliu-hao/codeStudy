/*     */ package com.zaxxer.hikari.util;
/*     */ 
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
/*     */ public interface ClockSource
/*     */ {
/*  37 */   public static final ClockSource CLOCK = Factory.create();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long currentTime() {
/*  45 */     return CLOCK.currentTime0();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long currentTime0();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long toMillis(long time) {
/*  58 */     return CLOCK.toMillis0(time);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long toMillis0(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long toNanos(long time) {
/*  71 */     return CLOCK.toNanos0(time);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long toNanos0(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long elapsedMillis(long startTime) {
/*  84 */     return CLOCK.elapsedMillis0(startTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long elapsedMillis0(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long elapsedMillis(long startTime, long endTime) {
/*  98 */     return CLOCK.elapsedMillis0(startTime, endTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long elapsedMillis0(long paramLong1, long paramLong2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long elapsedNanos(long startTime) {
/* 111 */     return CLOCK.elapsedNanos0(startTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long elapsedNanos0(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long elapsedNanos(long startTime, long endTime) {
/* 125 */     return CLOCK.elapsedNanos0(startTime, endTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long elapsedNanos0(long paramLong1, long paramLong2);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static long plusMillis(long time, long millis) {
/* 138 */     return CLOCK.plusMillis0(time, millis);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   long plusMillis0(long paramLong1, long paramLong2);
/*     */ 
/*     */ 
/*     */   
/*     */   static TimeUnit getSourceTimeUnit() {
/* 148 */     return CLOCK.getSourceTimeUnit0();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TimeUnit getSourceTimeUnit0();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String elapsedDisplayString(long startTime, long endTime) {
/* 161 */     return CLOCK.elapsedDisplayString0(startTime, endTime);
/*     */   }
/*     */   
/*     */   default String elapsedDisplayString0(long startTime, long endTime) {
/* 165 */     long elapsedNanos = elapsedNanos0(startTime, endTime);
/*     */     
/* 167 */     StringBuilder sb = new StringBuilder((elapsedNanos < 0L) ? "-" : "");
/* 168 */     elapsedNanos = Math.abs(elapsedNanos);
/*     */     
/* 170 */     for (TimeUnit unit : TIMEUNITS_DESCENDING) {
/* 171 */       long converted = unit.convert(elapsedNanos, TimeUnit.NANOSECONDS);
/* 172 */       if (converted > 0L) {
/* 173 */         sb.append(converted).append(TIMEUNIT_DISPLAY_VALUES[unit.ordinal()]);
/* 174 */         elapsedNanos -= TimeUnit.NANOSECONDS.convert(converted, unit);
/*     */       } 
/*     */     } 
/*     */     
/* 178 */     return sb.toString();
/*     */   }
/*     */   
/* 181 */   public static final TimeUnit[] TIMEUNITS_DESCENDING = new TimeUnit[] { TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS };
/*     */   
/* 183 */   public static final String[] TIMEUNIT_DISPLAY_VALUES = new String[] { "ns", "µs", "ms", "s", "m", "h", "d" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Factory
/*     */   {
/*     */     private static ClockSource create() {
/* 191 */       String os = System.getProperty("os.name");
/* 192 */       if ("Mac OS X".equals(os)) {
/* 193 */         return new ClockSource.MillisecondClockSource();
/*     */       }
/*     */       
/* 196 */       return new ClockSource.NanosecondClockSource();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class MillisecondClockSource
/*     */     implements ClockSource
/*     */   {
/*     */     public long currentTime0() {
/* 205 */       return System.currentTimeMillis();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedMillis0(long startTime) {
/* 211 */       return System.currentTimeMillis() - startTime;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedMillis0(long startTime, long endTime) {
/* 217 */       return endTime - startTime;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedNanos0(long startTime) {
/* 223 */       return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - startTime);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedNanos0(long startTime, long endTime) {
/* 229 */       return TimeUnit.MILLISECONDS.toNanos(endTime - startTime);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long toMillis0(long time) {
/* 235 */       return time;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long toNanos0(long time) {
/* 241 */       return TimeUnit.MILLISECONDS.toNanos(time);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long plusMillis0(long time, long millis) {
/* 247 */       return time + millis;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TimeUnit getSourceTimeUnit0() {
/* 253 */       return TimeUnit.MILLISECONDS;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class NanosecondClockSource
/*     */     implements ClockSource
/*     */   {
/*     */     public long currentTime0() {
/* 262 */       return System.nanoTime();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long toMillis0(long time) {
/* 268 */       return TimeUnit.NANOSECONDS.toMillis(time);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long toNanos0(long time) {
/* 274 */       return time;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedMillis0(long startTime) {
/* 280 */       return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedMillis0(long startTime, long endTime) {
/* 286 */       return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedNanos0(long startTime) {
/* 292 */       return System.nanoTime() - startTime;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long elapsedNanos0(long startTime, long endTime) {
/* 298 */       return endTime - startTime;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long plusMillis0(long time, long millis) {
/* 304 */       return time + TimeUnit.MILLISECONDS.toNanos(millis);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TimeUnit getSourceTimeUnit0() {
/* 310 */       return TimeUnit.NANOSECONDS;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\ClockSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */