/*     */ package cn.hutool.cron.pattern;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.date.DateUnit;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CronPatternUtil
/*     */ {
/*     */   public static Date nextDateAfter(CronPattern pattern, Date start, boolean isMatchSecond) {
/*  30 */     List<Date> matchedDates = matchedDates(pattern, start.getTime(), DateUtil.endOfYear(start).getTime(), 1, isMatchSecond);
/*  31 */     if (CollUtil.isNotEmpty(matchedDates)) {
/*  32 */       return matchedDates.get(0);
/*     */     }
/*  34 */     return null;
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
/*     */   public static List<Date> matchedDates(String patternStr, Date start, int count, boolean isMatchSecond) {
/*  47 */     return matchedDates(patternStr, start, (Date)DateUtil.endOfYear(start), count, isMatchSecond);
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
/*     */   public static List<Date> matchedDates(String patternStr, Date start, Date end, int count, boolean isMatchSecond) {
/*  61 */     return matchedDates(patternStr, start.getTime(), end.getTime(), count, isMatchSecond);
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
/*     */   public static List<Date> matchedDates(String patternStr, long start, long end, int count, boolean isMatchSecond) {
/*  75 */     return matchedDates(new CronPattern(patternStr), start, end, count, isMatchSecond);
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
/*     */   public static List<Date> matchedDates(CronPattern pattern, long start, long end, int count, boolean isMatchSecond) {
/*  89 */     Assert.isTrue((start < end), "Start date is later than end !", new Object[0]);
/*     */     
/*  91 */     List<Date> result = new ArrayList<>(count);
/*  92 */     long step = isMatchSecond ? DateUnit.SECOND.getMillis() : DateUnit.MINUTE.getMillis(); long i;
/*  93 */     for (i = start; i < end; i += step) {
/*  94 */       if (pattern.match(i, isMatchSecond)) {
/*  95 */         result.add(DateUtil.date(i));
/*  96 */         if (result.size() >= count) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/* 101 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\CronPatternUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */