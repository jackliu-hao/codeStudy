/*    */ package cn.hutool.core.thread.lock;
/*    */ 
/*    */ import java.util.concurrent.locks.Lock;
/*    */ import java.util.concurrent.locks.ReadWriteLock;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoReadWriteLock
/*    */   implements ReadWriteLock
/*    */ {
/*    */   public Lock readLock() {
/* 15 */     return NoLock.INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Lock writeLock() {
/* 20 */     return NoLock.INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\lock\NoReadWriteLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */