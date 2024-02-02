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
/*    */ public abstract class AbstractMessageSinkConduit<D extends MessageSinkConduit>
/*    */   extends AbstractSinkConduit<D>
/*    */   implements MessageSinkConduit
/*    */ {
/*    */   protected AbstractMessageSinkConduit(D next) {
/* 37 */     super(next);
/*    */   }
/*    */   
/*    */   public boolean send(ByteBuffer src) throws IOException {
/* 41 */     return ((MessageSinkConduit)this.next).send(src);
/*    */   }
/*    */   
/*    */   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 45 */     return ((MessageSinkConduit)this.next).send(srcs, offs, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 50 */     return ((MessageSinkConduit)this.next).sendFinal(srcs, offs, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean sendFinal(ByteBuffer src) throws IOException {
/* 55 */     return ((MessageSinkConduit)this.next).sendFinal(src);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractMessageSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */