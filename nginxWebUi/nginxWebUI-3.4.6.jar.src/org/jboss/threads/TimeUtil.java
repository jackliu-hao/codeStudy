/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.time.Duration;
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
/*    */ 
/*    */ 
/*    */ final class TimeUtil
/*    */ {
/*    */   private static final long LARGEST_SECONDS = 9223372035L;
/*    */   
/*    */   static long clampedPositiveNanos(Duration duration) {
/* 33 */     long seconds = Math.max(0L, duration.getSeconds());
/* 34 */     return (seconds > 9223372035L) ? Long.MAX_VALUE : Math.max(1L, seconds * 1000000000L + duration.getNano());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\TimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */