/*    */ package org.wildfly.common.lock;
/*    */ 
/*    */ import java.util.concurrent.locks.ReentrantLock;
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
/*    */ class ExtendedReentrantLock
/*    */   extends ReentrantLock
/*    */   implements ExtendedLock
/*    */ {
/*    */   ExtendedReentrantLock(boolean fair) {
/* 28 */     super(fair);
/*    */   }
/*    */   
/*    */   ExtendedReentrantLock() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\lock\ExtendedReentrantLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */