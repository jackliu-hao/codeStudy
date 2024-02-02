/*    */ package org.noear.solon.schedule.cron;
/*    */ 
/*    */ import java.text.ParseException;
/*    */ import java.time.ZoneOffset;
/*    */ import java.util.Date;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CronUtils
/*    */ {
/* 17 */   private static Map<String, CronExpressionPlus> cached = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CronExpressionPlus get(String cron) throws ParseException {
/* 25 */     CronExpressionPlus expr = cached.get(cron);
/*    */     
/* 27 */     if (expr == null) {
/* 28 */       synchronized (cron.intern()) {
/* 29 */         expr = cached.get(cron);
/*    */         
/* 31 */         if (expr == null) {
/* 32 */           int tzIdx = cron.indexOf("+");
/* 33 */           if (tzIdx < 0) {
/* 34 */             tzIdx = cron.indexOf("-");
/*    */           }
/*    */           
/* 37 */           if (tzIdx > 0) {
/* 38 */             String tz = cron.substring(tzIdx);
/* 39 */             ZoneOffset tz2 = ZoneOffset.of(tz);
/* 40 */             cron = cron.substring(0, tzIdx - 1);
/*    */             
/* 42 */             expr = new CronExpressionPlus(cron);
/* 43 */             expr.setTimeZone(TimeZone.getTimeZone(tz2));
/*    */           } else {
/* 45 */             expr = new CronExpressionPlus(cron);
/*    */           } 
/*    */           
/* 48 */           cached.put(cron, expr);
/*    */         } 
/*    */       } 
/*    */     }
/*    */     
/* 53 */     return expr;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Date getNextTime(String cron, Date baseTime) throws ParseException {
/* 60 */     return get(cron).getNextValidTimeAfter(baseTime);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isValid(String cron) {
/*    */     try {
/* 68 */       return (get(cron) != null);
/* 69 */     } catch (ParseException e) {
/* 70 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\cron\CronUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */