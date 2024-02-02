/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
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
/*    */ public abstract class AbstractMessageSourceConduit<D extends MessageSourceConduit>
/*    */   extends AbstractSourceConduit<D>
/*    */   implements MessageSourceConduit
/*    */ {
/*    */   protected AbstractMessageSourceConduit(D next) {
/* 37 */     super(next);
/*    */   }
/*    */   
/*    */   public int receive(ByteBuffer dst) throws IOException {
/* 41 */     return ((MessageSourceConduit)this.next).receive(dst);
/*    */   }
/*    */   
/*    */   public long receive(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 45 */     return ((MessageSourceConduit)this.next).receive(dsts, offs, len);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractMessageSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */