/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.time.temporal.ChronoUnit;
/*    */ import java.time.temporal.TemporalUnit;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.wildfly.common.Assert;
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
/*    */ 
/*    */ 
/*    */ final class JDKSpecific
/*    */ {
/*    */   static TemporalUnit timeToTemporal(TimeUnit timeUnit) {
/* 32 */     switch (timeUnit) { case NANOSECONDS:
/* 33 */         return ChronoUnit.NANOS;
/* 34 */       case MICROSECONDS: return ChronoUnit.MICROS;
/* 35 */       case MILLISECONDS: return ChronoUnit.MILLIS;
/* 36 */       case SECONDS: return ChronoUnit.SECONDS;
/* 37 */       case MINUTES: return ChronoUnit.MINUTES;
/* 38 */       case HOURS: return ChronoUnit.HOURS;
/* 39 */       case DAYS: return ChronoUnit.DAYS; }
/* 40 */      throw Assert.impossibleSwitchCase(timeUnit);
/*    */   }
/*    */   
/*    */   static void onSpinWait() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\JDKSpecific.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */