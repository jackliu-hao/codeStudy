/*    */ package org.noear.solon.schedule.cron;
/*    */ 
/*    */ import java.text.ParseException;
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CronExpressionPlus
/*    */   extends CronExpression
/*    */ {
/*    */   public CronExpressionPlus(String cronExpression) throws ParseException {
/* 15 */     super(cronExpression);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronExpressionPlus(CronExpression expression) {
/* 25 */     super(expression);
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<Integer> getSeconds() {
/* 30 */     return Collections.unmodifiableSet(this.seconds);
/*    */   }
/*    */   
/*    */   public Set<Integer> getMinutes() {
/* 34 */     return Collections.unmodifiableSet(this.minutes);
/*    */   }
/*    */   
/*    */   public Set<Integer> getHours() {
/* 38 */     return Collections.unmodifiableSet(this.hours);
/*    */   }
/*    */   
/*    */   public Set<Integer> getDaysOfMonth() {
/* 42 */     return Collections.unmodifiableSet(this.daysOfMonth);
/*    */   }
/*    */   
/*    */   public Set<Integer> getMonths() {
/* 46 */     return Collections.unmodifiableSet(this.months);
/*    */   }
/*    */   
/*    */   public Set<Integer> getDaysOfWeek() {
/* 50 */     return Collections.unmodifiableSet(this.daysOfWeek);
/*    */   }
/*    */   
/*    */   public Set<Integer> getYears() {
/* 54 */     return Collections.unmodifiableSet(this.years);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\cron\CronExpressionPlus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */