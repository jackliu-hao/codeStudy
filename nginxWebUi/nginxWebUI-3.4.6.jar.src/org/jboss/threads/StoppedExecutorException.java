/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.util.concurrent.RejectedExecutionException;
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
/*    */ 
/*    */ 
/*    */ public class StoppedExecutorException
/*    */   extends RejectedExecutionException
/*    */ {
/*    */   private static final long serialVersionUID = 4815103522815471074L;
/*    */   
/*    */   public StoppedExecutorException() {}
/*    */   
/*    */   public StoppedExecutorException(String msg) {
/* 43 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StoppedExecutorException(Throwable cause) {
/* 54 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StoppedExecutorException(String msg, Throwable cause) {
/* 64 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\StoppedExecutorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */