/*    */ package ch.qos.logback.core.net;
/*    */ 
/*    */ import java.util.concurrent.LinkedBlockingDeque;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class QueueFactory
/*    */ {
/*    */   public <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
/* 36 */     int actualCapacity = (capacity < 1) ? 1 : capacity;
/* 37 */     return new LinkedBlockingDeque<E>(actualCapacity);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\QueueFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */