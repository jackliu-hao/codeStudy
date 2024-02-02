/*    */ package META-INF.versions.9.org.jboss.threads;
/*    */ 
/*    */ import java.time.temporal.TemporalUnit;
/*    */ import java.util.concurrent.TimeUnit;
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
/* 29 */     return timeUnit.toChronoUnit();
/*    */   }
/*    */   
/*    */   static void onSpinWait() {
/* 33 */     Thread.onSpinWait();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\META-INF\versions\9\org\jboss\threads\JDKSpecific.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */