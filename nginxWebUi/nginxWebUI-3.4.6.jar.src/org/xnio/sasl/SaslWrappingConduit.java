/*    */ package org.xnio.sasl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.xnio.Buffers;
/*    */ import org.xnio.conduits.AbstractMessageSinkConduit;
/*    */ import org.xnio.conduits.Conduits;
/*    */ import org.xnio.conduits.MessageSinkConduit;
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
/*    */ public final class SaslWrappingConduit
/*    */   extends AbstractMessageSinkConduit<MessageSinkConduit>
/*    */   implements MessageSinkConduit
/*    */ {
/*    */   private final SaslWrapper wrapper;
/*    */   private ByteBuffer buffer;
/*    */   
/*    */   public SaslWrappingConduit(MessageSinkConduit next, SaslWrapper wrapper) {
/* 36 */     super(next);
/* 37 */     this.wrapper = wrapper;
/*    */   }
/*    */   
/*    */   public boolean send(ByteBuffer src) throws IOException {
/* 41 */     if (!doSend()) {
/* 42 */       return false;
/*    */     }
/* 44 */     ByteBuffer wrapped = ByteBuffer.wrap(this.wrapper.wrap(src));
/* 45 */     if (!((MessageSinkConduit)this.next).send(wrapped)) {
/* 46 */       this.buffer = wrapped;
/*    */     }
/* 48 */     return true;
/*    */   }
/*    */   
/*    */   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 52 */     if (!doSend()) {
/* 53 */       return false;
/*    */     }
/* 55 */     ByteBuffer wrapped = ByteBuffer.wrap(this.wrapper.wrap(Buffers.take(srcs, offs, len)));
/* 56 */     if (!((MessageSinkConduit)this.next).send(wrapped)) {
/* 57 */       this.buffer = wrapped;
/*    */     }
/* 59 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean sendFinal(ByteBuffer src) throws IOException {
/* 64 */     return Conduits.sendFinalBasic(this, src);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 69 */     return Conduits.sendFinalBasic(this, srcs, offs, len);
/*    */   }
/*    */   
/*    */   private boolean doSend() throws IOException {
/* 73 */     ByteBuffer buffer = this.buffer;
/* 74 */     if (buffer != null && ((MessageSinkConduit)this.next).send(buffer)) {
/* 75 */       this.buffer = null;
/* 76 */       return true;
/*    */     } 
/* 78 */     return false;
/*    */   }
/*    */   
/*    */   public boolean flush() throws IOException {
/* 82 */     return (doSend() && ((MessageSinkConduit)this.next).flush());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\sasl\SaslWrappingConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */