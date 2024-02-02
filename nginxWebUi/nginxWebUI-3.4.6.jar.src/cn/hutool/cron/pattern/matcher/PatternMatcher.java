/*     */ package cn.hutool.cron.pattern.matcher;
/*     */ 
/*     */ import cn.hutool.cron.pattern.Part;
/*     */ import java.time.Year;
/*     */ import java.util.Calendar;
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
/*     */ public class PatternMatcher
/*     */ {
/*     */   private final PartMatcher[] matchers;
/*     */   
/*     */   public PatternMatcher(PartMatcher secondMatcher, PartMatcher minuteMatcher, PartMatcher hourMatcher, PartMatcher dayOfMonthMatcher, PartMatcher monthMatcher, PartMatcher dayOfWeekMatcher, PartMatcher yearMatcher) {
/*  42 */     this.matchers = new PartMatcher[] { secondMatcher, minuteMatcher, hourMatcher, dayOfMonthMatcher, monthMatcher, dayOfWeekMatcher, yearMatcher };
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
/*     */   public PartMatcher get(Part part) {
/*  60 */     return this.matchers[part.ordinal()];
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
/*     */   public boolean match(int[] fields) {
/*  72 */     return match(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matchWeek(int dayOfWeekValue) {
/*  83 */     return this.matchers[5].match(Integer.valueOf(dayOfWeekValue));
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
/*     */   private boolean match(int second, int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year) {
/*  99 */     return ((second < 0 || this.matchers[0].match(Integer.valueOf(second))) && this.matchers[1]
/* 100 */       .match(Integer.valueOf(minute)) && this.matchers[2]
/* 101 */       .match(Integer.valueOf(hour)) && 
/* 102 */       matchDayOfMonth(this.matchers[3], dayOfMonth, month, Year.isLeap(year)) && this.matchers[4]
/* 103 */       .match(Integer.valueOf(month)) && this.matchers[5]
/* 104 */       .match(Integer.valueOf(dayOfWeek)) && this.matchers[6]
/* 105 */       .match(Integer.valueOf(year)));
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
/*     */   private static boolean matchDayOfMonth(PartMatcher matcher, int dayOfMonth, int month, boolean isLeapYear) {
/* 118 */     return (matcher instanceof DayOfMonthMatcher) ? ((DayOfMonthMatcher)matcher)
/* 119 */       .match(dayOfMonth, month, isLeapYear) : matcher
/* 120 */       .match(Integer.valueOf(dayOfMonth));
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
/*     */   public Calendar nextMatchAfter(int[] values, TimeZone zone) {
/* 145 */     Calendar calendar = Calendar.getInstance(zone);
/* 146 */     calendar.set(14, 0);
/*     */     
/* 148 */     int[] newValues = nextMatchValuesAfter(values);
/* 149 */     for (int i = 0; i < newValues.length; i++) {
/*     */       
/* 151 */       if (i != Part.DAY_OF_WEEK.ordinal()) {
/* 152 */         setValue(calendar, Part.of(i), newValues[i]);
/*     */       }
/*     */     } 
/*     */     
/* 156 */     return calendar;
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
/*     */   private int[] nextMatchValuesAfter(int[] values) {
/* 177 */     int[] newValues = (int[])values.clone();
/*     */     
/* 179 */     int i = Part.YEAR.ordinal();
/*     */     
/* 181 */     int nextValue = 0;
/* 182 */     while (i >= 0) {
/* 183 */       if (i == Part.DAY_OF_WEEK.ordinal()) {
/*     */         
/* 185 */         i--;
/*     */         continue;
/*     */       } 
/* 188 */       nextValue = this.matchers[i].nextAfter(values[i]);
/* 189 */       if (nextValue > values[i]) {
/*     */         
/* 191 */         newValues[i] = nextValue;
/* 192 */         i--; break;
/*     */       } 
/* 194 */       if (nextValue < values[i]) {
/*     */         
/* 196 */         i++;
/* 197 */         nextValue = -1;
/*     */         
/*     */         break;
/*     */       } 
/* 201 */       i--;
/*     */     } 
/*     */ 
/*     */     
/* 205 */     if (-1 == nextValue) {
/* 206 */       while (i <= Part.YEAR.ordinal()) {
/* 207 */         if (i == Part.DAY_OF_WEEK.ordinal()) {
/*     */           
/* 209 */           i++;
/*     */           continue;
/*     */         } 
/* 212 */         nextValue = this.matchers[i].nextAfter(values[i] + 1);
/* 213 */         if (nextValue > values[i]) {
/* 214 */           newValues[i] = nextValue;
/* 215 */           i--;
/*     */           break;
/*     */         } 
/* 218 */         i++;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 223 */     setToMin(newValues, i);
/* 224 */     return newValues;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setToMin(int[] values, int toPart) {
/* 235 */     for (int i = 0; i <= toPart; i++) {
/* 236 */       Part part = Part.of(i);
/* 237 */       values[i] = getMin(part);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getMin(Part part) {
/*     */     int min;
/* 248 */     PartMatcher matcher = get(part);
/*     */ 
/*     */     
/* 251 */     if (matcher instanceof AlwaysTrueMatcher) {
/* 252 */       min = part.getMin();
/* 253 */     } else if (matcher instanceof BoolArrayMatcher) {
/* 254 */       min = ((BoolArrayMatcher)matcher).getMinValue();
/*     */     } else {
/* 256 */       throw new IllegalArgumentException("Invalid matcher: " + matcher.getClass().getName());
/*     */     } 
/* 258 */     return min;
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
/*     */   private Calendar setValue(Calendar calendar, Part part, int value) {
/* 275 */     switch (part) {
/*     */       case MONTH:
/* 277 */         value--;
/*     */         break;
/*     */       case DAY_OF_WEEK:
/* 280 */         value++;
/*     */         break;
/*     */     } 
/*     */     
/* 284 */     calendar.set(part.getCalendarField(), value);
/*     */     
/* 286 */     return calendar;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\matcher\PatternMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */