/*     */ package cn.hutool.cron.pattern;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.date.CalendarUtil;
/*     */ import cn.hutool.cron.pattern.matcher.PatternMatcher;
/*     */ import cn.hutool.cron.pattern.parser.PatternParser;
/*     */ import java.time.LocalDateTime;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CronPattern
/*     */ {
/*     */   private final String pattern;
/*     */   private final List<PatternMatcher> matchers;
/*     */   
/*     */   public static CronPattern of(String pattern) {
/*  84 */     return new CronPattern(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CronPattern(String pattern) {
/*  93 */     this.pattern = pattern;
/*  94 */     this.matchers = PatternParser.parse(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(long millis, boolean isMatchSecond) {
/* 105 */     return match(TimeZone.getDefault(), millis, isMatchSecond);
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
/*     */   public boolean match(TimeZone timezone, long millis, boolean isMatchSecond) {
/* 117 */     GregorianCalendar calendar = new GregorianCalendar(timezone);
/* 118 */     calendar.setTimeInMillis(millis);
/* 119 */     return match(calendar, isMatchSecond);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean match(Calendar calendar, boolean isMatchSecond) {
/* 130 */     return match(PatternUtil.getFields(calendar, isMatchSecond));
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
/*     */   public boolean match(LocalDateTime dateTime, boolean isMatchSecond) {
/* 142 */     return match(PatternUtil.getFields(dateTime, isMatchSecond));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Calendar nextMatchAfter(Calendar calendar) {
/* 152 */     Calendar next = nextMatchAfter(PatternUtil.getFields(calendar, true), calendar.getTimeZone());
/* 153 */     if (false == match(next, true)) {
/* 154 */       next.set(5, next.get(5) + 1);
/* 155 */       next = CalendarUtil.beginOfDay(next);
/* 156 */       return nextMatchAfter(next);
/*     */     } 
/* 158 */     return next;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 163 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean match(int[] fields) {
/* 173 */     for (PatternMatcher matcher : this.matchers) {
/* 174 */       if (matcher.match(fields)) {
/* 175 */         return true;
/*     */       }
/*     */     } 
/* 178 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Calendar nextMatchAfter(int[] values, TimeZone zone) {
/* 189 */     List<Calendar> nextMatches = new ArrayList<>(this.matchers.size());
/* 190 */     for (PatternMatcher matcher : this.matchers) {
/* 191 */       nextMatches.add(matcher.nextMatchAfter(values, zone));
/*     */     }
/*     */     
/* 194 */     return (Calendar)CollUtil.min(nextMatches);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\CronPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */