/*    */ package cn.hutool.core.thread.lock;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.locks.Condition;
/*    */ import java.util.concurrent.locks.Lock;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoLock
/*    */   implements Lock
/*    */ {
/* 15 */   public static NoLock INSTANCE = new NoLock();
/*    */ 
/*    */ 
/*    */   
/*    */   public void lock() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void lockInterruptibly() {}
/*    */ 
/*    */   
/*    */   public boolean tryLock() {
/* 27 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean tryLock(long time, TimeUnit unit) {
/* 33 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void unlock() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public Condition newCondition() {
/* 43 */     throw new UnsupportedOperationException("NoLock`s newCondition method is unsupported");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\lock\NoLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */