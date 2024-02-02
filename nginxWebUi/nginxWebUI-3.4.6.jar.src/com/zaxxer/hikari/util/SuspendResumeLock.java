/*    */ package com.zaxxer.hikari.util;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLTransientException;
/*    */ import java.util.concurrent.Semaphore;
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
/*    */ public class SuspendResumeLock
/*    */ {
/* 32 */   public static final SuspendResumeLock FAUX_LOCK = new SuspendResumeLock(false)
/*    */     {
/*    */       public void acquire() {}
/*    */ 
/*    */ 
/*    */       
/*    */       public void release() {}
/*    */ 
/*    */       
/*    */       public void suspend() {}
/*    */ 
/*    */       
/*    */       public void resume() {}
/*    */     };
/*    */ 
/*    */   
/*    */   private static final int MAX_PERMITS = 10000;
/*    */   
/*    */   private final Semaphore acquisitionSemaphore;
/*    */ 
/*    */   
/*    */   public SuspendResumeLock() {
/* 54 */     this(true);
/*    */   }
/*    */ 
/*    */   
/*    */   private SuspendResumeLock(boolean createSemaphore) {
/* 59 */     this.acquisitionSemaphore = createSemaphore ? new Semaphore(10000, true) : null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void acquire() throws SQLException {
/* 64 */     if (this.acquisitionSemaphore.tryAcquire()) {
/*    */       return;
/*    */     }
/* 67 */     if (Boolean.getBoolean("com.zaxxer.hikari.throwIfSuspended")) {
/* 68 */       throw new SQLTransientException("The pool is currently suspended and configured to throw exceptions upon acquisition");
/*    */     }
/*    */     
/* 71 */     this.acquisitionSemaphore.acquireUninterruptibly();
/*    */   }
/*    */ 
/*    */   
/*    */   public void release() {
/* 76 */     this.acquisitionSemaphore.release();
/*    */   }
/*    */ 
/*    */   
/*    */   public void suspend() {
/* 81 */     this.acquisitionSemaphore.acquireUninterruptibly(10000);
/*    */   }
/*    */ 
/*    */   
/*    */   public void resume() {
/* 86 */     this.acquisitionSemaphore.release(10000);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\SuspendResumeLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */