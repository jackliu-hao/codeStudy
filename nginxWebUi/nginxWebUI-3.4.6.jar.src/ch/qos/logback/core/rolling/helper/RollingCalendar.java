/*     */ package ch.qos.logback.core.rolling.helper;
/*     */ 
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
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
/*     */ public class RollingCalendar
/*     */   extends GregorianCalendar
/*     */ {
/*     */   private static final long serialVersionUID = -5937537740925066161L;
/*  44 */   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
/*     */   
/*  46 */   PeriodicityType periodicityType = PeriodicityType.ERRONEOUS;
/*     */   
/*     */   String datePattern;
/*     */   
/*     */   public RollingCalendar(String datePattern) {
/*  51 */     this.datePattern = datePattern;
/*  52 */     this.periodicityType = computePeriodicityType();
/*     */   }
/*     */   
/*     */   public RollingCalendar(String datePattern, TimeZone tz, Locale locale) {
/*  56 */     super(tz, locale);
/*  57 */     this.datePattern = datePattern;
/*  58 */     this.periodicityType = computePeriodicityType();
/*     */   }
/*     */   
/*     */   public PeriodicityType getPeriodicityType() {
/*  62 */     return this.periodicityType;
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
/*     */   public PeriodicityType computePeriodicityType() {
/*  75 */     GregorianCalendar calendar = new GregorianCalendar(GMT_TIMEZONE, Locale.getDefault());
/*     */ 
/*     */     
/*  78 */     Date epoch = new Date(0L);
/*     */     
/*  80 */     if (this.datePattern != null) {
/*  81 */       for (PeriodicityType i : PeriodicityType.VALID_ORDERED_LIST) {
/*  82 */         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
/*  83 */         simpleDateFormat.setTimeZone(GMT_TIMEZONE);
/*     */         
/*  85 */         String r0 = simpleDateFormat.format(epoch);
/*     */         
/*  87 */         Date next = innerGetEndOfThisPeriod(calendar, i, epoch);
/*  88 */         String r1 = simpleDateFormat.format(next);
/*     */ 
/*     */         
/*  91 */         if (r0 != null && r1 != null && !r0.equals(r1)) {
/*  92 */           return i;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*  97 */     return PeriodicityType.ERRONEOUS;
/*     */   }
/*     */   
/*     */   public boolean isCollisionFree() {
/* 101 */     switch (this.periodicityType) {
/*     */       
/*     */       case TOP_OF_HOUR:
/* 104 */         return !collision(43200000L);
/*     */ 
/*     */       
/*     */       case TOP_OF_DAY:
/* 108 */         if (collision(604800000L)) {
/* 109 */           return false;
/*     */         }
/* 111 */         if (collision(2678400000L)) {
/* 112 */           return false;
/*     */         }
/* 114 */         if (collision(31536000000L))
/* 115 */           return false; 
/* 116 */         return true;
/*     */       
/*     */       case TOP_OF_WEEK:
/* 119 */         if (collision(2937600000L)) {
/* 120 */           return false;
/*     */         }
/* 122 */         if (collision(31622400000L))
/* 123 */           return false; 
/* 124 */         return true;
/*     */     } 
/* 126 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean collision(long delta) {
/* 131 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
/* 132 */     simpleDateFormat.setTimeZone(GMT_TIMEZONE);
/* 133 */     Date epoch0 = new Date(0L);
/* 134 */     String r0 = simpleDateFormat.format(epoch0);
/* 135 */     Date epoch12 = new Date(delta);
/* 136 */     String r12 = simpleDateFormat.format(epoch12);
/*     */     
/* 138 */     return r0.equals(r12);
/*     */   }
/*     */   
/*     */   public void printPeriodicity(ContextAwareBase cab) {
/* 142 */     switch (this.periodicityType) {
/*     */       case TOP_OF_MILLISECOND:
/* 144 */         cab.addInfo("Roll-over every millisecond.");
/*     */         return;
/*     */       
/*     */       case TOP_OF_SECOND:
/* 148 */         cab.addInfo("Roll-over every second.");
/*     */         return;
/*     */       
/*     */       case TOP_OF_MINUTE:
/* 152 */         cab.addInfo("Roll-over every minute.");
/*     */         return;
/*     */       
/*     */       case TOP_OF_HOUR:
/* 156 */         cab.addInfo("Roll-over at the top of every hour.");
/*     */         return;
/*     */       
/*     */       case HALF_DAY:
/* 160 */         cab.addInfo("Roll-over at midday and midnight.");
/*     */         return;
/*     */       
/*     */       case TOP_OF_DAY:
/* 164 */         cab.addInfo("Roll-over at midnight.");
/*     */         return;
/*     */       
/*     */       case TOP_OF_WEEK:
/* 168 */         cab.addInfo("Rollover at the start of week.");
/*     */         return;
/*     */       
/*     */       case TOP_OF_MONTH:
/* 172 */         cab.addInfo("Rollover at start of every month.");
/*     */         return;
/*     */     } 
/*     */     
/* 176 */     cab.addInfo("Unknown periodicity.");
/*     */   }
/*     */ 
/*     */   
/*     */   public long periodBarriersCrossed(long start, long end) {
/* 181 */     if (start > end) {
/* 182 */       throw new IllegalArgumentException("Start cannot come before end");
/*     */     }
/* 184 */     long startFloored = getStartOfCurrentPeriodWithGMTOffsetCorrection(start, getTimeZone());
/* 185 */     long endFloored = getStartOfCurrentPeriodWithGMTOffsetCorrection(end, getTimeZone());
/*     */     
/* 187 */     long diff = endFloored - startFloored;
/*     */     
/* 189 */     switch (this.periodicityType) {
/*     */       
/*     */       case TOP_OF_MILLISECOND:
/* 192 */         return diff;
/*     */       case TOP_OF_SECOND:
/* 194 */         return diff / 1000L;
/*     */       case TOP_OF_MINUTE:
/* 196 */         return diff / 60000L;
/*     */       
/*     */       case TOP_OF_HOUR:
/* 199 */         return (int)diff / 3600000L;
/*     */       case TOP_OF_DAY:
/* 201 */         return diff / 86400000L;
/*     */       case TOP_OF_WEEK:
/* 203 */         return diff / 604800000L;
/*     */       case TOP_OF_MONTH:
/* 205 */         return diffInMonths(start, end);
/*     */     } 
/* 207 */     throw new IllegalStateException("Unknown periodicity type.");
/*     */   }
/*     */ 
/*     */   
/*     */   public static int diffInMonths(long startTime, long endTime) {
/* 212 */     if (startTime > endTime)
/* 213 */       throw new IllegalArgumentException("startTime cannot be larger than endTime"); 
/* 214 */     Calendar startCal = Calendar.getInstance();
/* 215 */     startCal.setTimeInMillis(startTime);
/* 216 */     Calendar endCal = Calendar.getInstance();
/* 217 */     endCal.setTimeInMillis(endTime);
/* 218 */     int yearDiff = endCal.get(1) - startCal.get(1);
/* 219 */     int monthDiff = endCal.get(2) - startCal.get(2);
/* 220 */     return yearDiff * 12 + monthDiff;
/*     */   }
/*     */   
/*     */   private static Date innerGetEndOfThisPeriod(Calendar cal, PeriodicityType periodicityType, Date now) {
/* 224 */     return innerGetEndOfNextNthPeriod(cal, periodicityType, now, 1);
/*     */   }
/*     */   
/*     */   private static Date innerGetEndOfNextNthPeriod(Calendar cal, PeriodicityType periodicityType, Date now, int numPeriods) {
/* 228 */     cal.setTime(now);
/* 229 */     switch (periodicityType) {
/*     */       case TOP_OF_MILLISECOND:
/* 231 */         cal.add(14, numPeriods);
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
/* 282 */         return cal.getTime();case TOP_OF_SECOND: cal.set(14, 0); cal.add(13, numPeriods); return cal.getTime();case TOP_OF_MINUTE: cal.set(13, 0); cal.set(14, 0); cal.add(12, numPeriods); return cal.getTime();case TOP_OF_HOUR: cal.set(12, 0); cal.set(13, 0); cal.set(14, 0); cal.add(11, numPeriods); return cal.getTime();case TOP_OF_DAY: cal.set(11, 0); cal.set(12, 0); cal.set(13, 0); cal.set(14, 0); cal.add(5, numPeriods); return cal.getTime();case TOP_OF_WEEK: cal.set(7, cal.getFirstDayOfWeek()); cal.set(11, 0); cal.set(12, 0); cal.set(13, 0); cal.set(14, 0); cal.add(3, numPeriods); return cal.getTime();case TOP_OF_MONTH: cal.set(5, 1); cal.set(11, 0); cal.set(12, 0); cal.set(13, 0); cal.set(14, 0); cal.add(2, numPeriods); return cal.getTime();
/*     */     } 
/*     */     throw new IllegalStateException("Unknown periodicity type.");
/*     */   } public Date getEndOfNextNthPeriod(Date now, int periods) {
/* 286 */     return innerGetEndOfNextNthPeriod(this, this.periodicityType, now, periods);
/*     */   }
/*     */   
/*     */   public Date getNextTriggeringDate(Date now) {
/* 290 */     return getEndOfNextNthPeriod(now, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getStartOfCurrentPeriodWithGMTOffsetCorrection(long now, TimeZone timezone) {
/* 299 */     Calendar aCal = Calendar.getInstance(timezone);
/* 300 */     aCal.setTimeInMillis(now);
/* 301 */     Date toppedDate = getEndOfNextNthPeriod(aCal.getTime(), 0);
/*     */     
/* 303 */     Calendar secondCalendar = Calendar.getInstance(timezone);
/* 304 */     secondCalendar.setTimeInMillis(toppedDate.getTime());
/* 305 */     long gmtOffset = (secondCalendar.get(15) + secondCalendar.get(16));
/* 306 */     return toppedDate.getTime() + gmtOffset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\RollingCalendar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */