/*    */ package org.xnio.conduits;
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
/*    */ 
/*    */ public abstract class AbstractSynchronizedConduit<D extends Conduit>
/*    */   extends AbstractConduit<D>
/*    */ {
/*    */   protected final Object lock;
/*    */   
/*    */   protected AbstractSynchronizedConduit(D next) {
/* 36 */     this(next, new Object());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected AbstractSynchronizedConduit(D next, Object lock) {
/* 46 */     super(next);
/* 47 */     this.lock = lock;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractSynchronizedConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */