/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import org.xnio.XnioWorker;
/*    */ import org.xnio._private.Messages;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractConduit<D extends Conduit>
/*    */   implements Conduit
/*    */ {
/*    */   protected final D next;
/*    */   
/*    */   protected AbstractConduit(D next) {
/* 43 */     if (next == null) {
/* 44 */       throw Messages.msg.nullParameter("next");
/*    */     }
/* 46 */     this.next = next;
/*    */   }
/*    */   
/*    */   public XnioWorker getWorker() {
/* 50 */     return this.next.getWorker();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */