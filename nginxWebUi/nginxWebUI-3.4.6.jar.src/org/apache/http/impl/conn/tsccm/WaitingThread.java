/*     */ package org.apache.http.impl.conn.tsccm;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class WaitingThread
/*     */ {
/*     */   private final Condition cond;
/*     */   private final RouteSpecificPool pool;
/*     */   private Thread waiter;
/*     */   private boolean aborted;
/*     */   
/*     */   public WaitingThread(Condition cond, RouteSpecificPool pool) {
/*  78 */     Args.notNull(cond, "Condition");
/*     */     
/*  80 */     this.cond = cond;
/*  81 */     this.pool = pool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Condition getCondition() {
/*  92 */     return this.cond;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final RouteSpecificPool getPool() {
/* 104 */     return this.pool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Thread getThread() {
/* 115 */     return this.waiter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean await(Date deadline) throws InterruptedException {
/* 144 */     if (this.waiter != null) {
/* 145 */       throw new IllegalStateException("A thread is already waiting on this object.\ncaller: " + Thread.currentThread() + "\nwaiter: " + this.waiter);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 151 */     if (this.aborted) {
/* 152 */       throw new InterruptedException("Operation interrupted");
/*     */     }
/*     */     
/* 155 */     this.waiter = Thread.currentThread();
/*     */     
/* 157 */     boolean success = false;
/*     */     try {
/* 159 */       if (deadline != null) {
/* 160 */         success = this.cond.awaitUntil(deadline);
/*     */       } else {
/* 162 */         this.cond.await();
/* 163 */         success = true;
/*     */       } 
/* 165 */       if (this.aborted) {
/* 166 */         throw new InterruptedException("Operation interrupted");
/*     */       }
/*     */     } finally {
/* 169 */       this.waiter = null;
/*     */     } 
/* 171 */     return success;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void wakeup() {
/* 186 */     if (this.waiter == null) {
/* 187 */       throw new IllegalStateException("Nobody waiting on this object.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 193 */     this.cond.signalAll();
/*     */   }
/*     */   
/*     */   public void interrupt() {
/* 197 */     this.aborted = true;
/* 198 */     this.cond.signalAll();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\tsccm\WaitingThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */