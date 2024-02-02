/*    */ package ch.qos.logback.core.hook;
/*    */ 
/*    */ import ch.qos.logback.core.util.Duration;
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
/*    */ public class DelayingShutdownHook
/*    */   extends ShutdownHookBase
/*    */ {
/* 28 */   public static final Duration DEFAULT_DELAY = Duration.buildByMilliseconds(0.0D);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 33 */   private Duration delay = DEFAULT_DELAY;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Duration getDelay() {
/* 39 */     return this.delay;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDelay(Duration delay) {
/* 48 */     this.delay = delay;
/*    */   }
/*    */   
/*    */   public void run() {
/* 52 */     addInfo("Sleeping for " + this.delay);
/*    */     try {
/* 54 */       Thread.sleep(this.delay.getMilliseconds());
/* 55 */     } catch (InterruptedException interruptedException) {}
/*    */     
/* 57 */     stop();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\hook\DelayingShutdownHook.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */