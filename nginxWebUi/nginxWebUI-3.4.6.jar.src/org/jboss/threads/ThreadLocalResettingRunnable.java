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
/*    */ final class ThreadLocalResettingRunnable
/*    */   extends DelegatingRunnable
/*    */ {
/*    */   ThreadLocalResettingRunnable(Runnable delegate) {
/* 24 */     super(delegate);
/*    */   }
/*    */   
/*    */   public void run() {
/*    */     try {
/* 29 */       super.run();
/*    */     } finally {
/* 31 */       Resetter.run();
/*    */     } 
/*    */   }
/*    */   
/*    */   public String toString() {
/* 36 */     return "Thread-local resetting Runnable";
/*    */   }
/*    */   
/*    */   static final class Resetter {
/*    */     private static final long threadLocalMapOffs;
/*    */     private static final long inheritableThreadLocalMapOffs;
/*    */     
/*    */     static {
/*    */       try {
/* 45 */         threadLocalMapOffs = JBossExecutors.unsafe.objectFieldOffset(Thread.class.getDeclaredField("threadLocals"));
/* 46 */         inheritableThreadLocalMapOffs = JBossExecutors.unsafe.objectFieldOffset(Thread.class.getDeclaredField("inheritableThreadLocals"));
/* 47 */       } catch (NoSuchFieldException e) {
/* 48 */         throw new NoSuchFieldError(e.getMessage());
/*    */       } 
/*    */     }
/*    */     
/*    */     static void run() {
/* 53 */       Thread thread = Thread.currentThread();
/* 54 */       JBossExecutors.unsafe.putObject(thread, threadLocalMapOffs, null);
/* 55 */       JBossExecutors.unsafe.putObject(thread, inheritableThreadLocalMapOffs, null);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\ThreadLocalResettingRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */