/*    */ package ch.qos.logback.core.util;
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
/*    */ 
/*    */ 
/*    */ public class FixedDelay
/*    */   implements DelayStrategy
/*    */ {
/*    */   private final long subsequentDelay;
/*    */   private long nextDelay;
/*    */   
/*    */   public FixedDelay(long initialDelay, long subsequentDelay) {
/* 35 */     this.nextDelay = initialDelay;
/* 36 */     this.subsequentDelay = subsequentDelay;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FixedDelay(int delay) {
/* 46 */     this(delay, delay);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long nextDelay() {
/* 53 */     long delay = this.nextDelay;
/* 54 */     this.nextDelay = this.subsequentDelay;
/* 55 */     return delay;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\FixedDelay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */