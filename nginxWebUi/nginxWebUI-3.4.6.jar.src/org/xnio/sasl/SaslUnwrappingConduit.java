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
/*    */ public final class SaslUnwrappingConduit
/*    */   extends AbstractMessageSinkConduit<MessageSinkConduit>
/*    */   implements MessageSinkConduit
/*    */ {
/*    */   private final SaslWrapper wrapper;
/*    */   private ByteBuffer buffer;
/*    */   
/*    */   public SaslUnwrappingConduit(MessageSinkConduit next, SaslWrapper wrapper) {
/* 36 */     super(next);
/* 37 */     this.wrapper = wrapper;
/*    */   }
/*    */   
/*    */   public boolean send(ByteBuffer src) throws IOException {
/* 41 */     if (!doSend()) {
/* 42 */       return false;
/*    */     }
/* 44 */     ByteBuffer wrapped = ByteBuffer.wrap(this.wrapper.unwrap(src));
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
/* 55 */     byte[] bytes = Buffers.take(srcs, offs, len);
/* 56 */     ByteBuffer wrapped = ByteBuffer.wrap(this.wrapper.unwrap(bytes));
/* 57 */     if (!((MessageSinkConduit)this.next).send(wrapped)) {
/* 58 */       this.buffer = wrapped;
/*    */     }
/* 60 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean sendFinal(ByteBuffer src) throws IOException {
/* 65 */     return Conduits.sendFinalBasic(this, src);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 70 */     return Conduits.sendFinalBasic(this, srcs, offs, len);
/*    */   }
/*    */   
/*    */   private boolean doSend() throws IOException {
/* 74 */     ByteBuffer buffer = this.buffer;
/* 75 */     if (buffer != null && ((MessageSinkConduit)this.next).send(buffer)) {
/* 76 */       this.buffer = null;
/* 77 */       return true;
/*    */     } 
/* 79 */     return false;
/*    */   }
/*    */   
/*    */   public boolean flush() throws IOException {
/* 83 */     return (doSend() && ((MessageSinkConduit)this.next).flush());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\sasl\SaslUnwrappingConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */