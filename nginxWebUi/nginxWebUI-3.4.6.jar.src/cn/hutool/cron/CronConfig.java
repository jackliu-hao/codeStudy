/*    */ package cn.hutool.cron;
/*    */ 
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CronConfig
/*    */ {
/* 16 */   protected TimeZone timezone = TimeZone.getDefault();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean matchSecond;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronConfig setTimeZone(TimeZone timezone) {
/* 32 */     this.timezone = timezone;
/* 33 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TimeZone getTimeZone() {
/* 42 */     return this.timezone;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isMatchSecond() {
/* 51 */     return this.matchSecond;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronConfig setMatchSecond(boolean isMatchSecond) {
/* 61 */     this.matchSecond = isMatchSecond;
/* 62 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\CronConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */