/*    */ package org.wildfly.common.lock;
/*    */ 
/*    */ import org.wildfly.common.annotation.NotNull;
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
/*    */ public final class Locks
/*    */ {
/*    */   @NotNull
/*    */   public static ExtendedLock reentrantLock() {
/* 35 */     return new ExtendedReentrantLock();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public static ExtendedLock reentrantLock(boolean fair) {
/* 45 */     return new ExtendedReentrantLock(fair);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public static ExtendedLock spinLock() {
/* 55 */     return new SpinLock();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\lock\Locks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */