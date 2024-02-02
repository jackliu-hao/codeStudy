/*    */ package cn.hutool.core.thread.lock;
/*    */ 
/*    */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*    */ import java.util.concurrent.locks.StampedLock;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LockUtil
/*    */ {
/* 14 */   private static final NoLock NO_LOCK = new NoLock();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static StampedLock createStampLock() {
/* 22 */     return new StampedLock();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ReentrantReadWriteLock createReadWriteLock(boolean fair) {
/* 32 */     return new ReentrantReadWriteLock(fair);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static NoLock getNoLock() {
/* 41 */     return NO_LOCK;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\lock\LockUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */