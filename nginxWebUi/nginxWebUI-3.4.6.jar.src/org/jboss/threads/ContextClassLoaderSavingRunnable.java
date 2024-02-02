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
/*    */ class ContextClassLoaderSavingRunnable
/*    */   implements Runnable
/*    */ {
/*    */   private final ClassLoader loader;
/*    */   private final Runnable delegate;
/*    */   
/*    */   ContextClassLoaderSavingRunnable(ClassLoader loader, Runnable delegate) {
/* 27 */     this.loader = loader;
/* 28 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   public void run() {
/* 32 */     Thread currentThread = Thread.currentThread();
/* 33 */     ClassLoader old = JBossExecutors.getAndSetContextClassLoader(currentThread, this.loader);
/*    */     try {
/* 35 */       this.delegate.run();
/*    */     } finally {
/* 37 */       JBossExecutors.setContextClassLoader(currentThread, old);
/*    */     } 
/*    */   }
/*    */   
/*    */   public String toString() {
/* 42 */     return "Context class loader saving " + this.delegate.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\ContextClassLoaderSavingRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */