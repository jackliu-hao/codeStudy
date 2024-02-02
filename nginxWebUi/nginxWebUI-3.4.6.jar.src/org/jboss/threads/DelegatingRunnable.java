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
/*    */ class DelegatingRunnable
/*    */   implements Runnable
/*    */ {
/*    */   private final Runnable delegate;
/*    */   
/*    */   DelegatingRunnable(Runnable delegate) {
/* 25 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   public void run() {
/* 29 */     this.delegate.run();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 33 */     return String.format("%s -> %s", new Object[] { super.toString(), this.delegate });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\DelegatingRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */