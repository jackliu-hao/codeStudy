/*    */ package org.jboss.threads;
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
/*    */ final class NullRunnable
/*    */   implements Runnable
/*    */ {
/* 23 */   private static final NullRunnable INSTANCE = new NullRunnable();
/*    */   
/*    */   static NullRunnable getInstance() {
/* 26 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public void run() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\NullRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */