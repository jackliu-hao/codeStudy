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
/*    */ 
/*    */ public final class SynchronizedMessageSinkConduit
/*    */   extends AbstractSynchronizedSinkConduit<MessageSinkConduit>
/*    */   implements MessageSinkConduit
/*    */ {
/*    */   public SynchronizedMessageSinkConduit(MessageSinkConduit next) {
/* 38 */     super(next);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SynchronizedMessageSinkConduit(MessageSinkConduit next, Object lock) {
/* 48 */     super(next, lock);
/*    */   }
/*    */   
/*    */   public boolean send(ByteBuffer src) throws IOException {
/* 52 */     synchronized (this.lock) {
/* 53 */       return this.next.send(src);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean send(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 58 */     synchronized (this.lock) {
/* 59 */       return this.next.send(srcs, offs, len);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean sendFinal(ByteBuffer src) throws IOException {
/* 64 */     synchronized (this.lock) {
/* 65 */       return this.next.sendFinal(src);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean sendFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 70 */     synchronized (this.lock) {
/* 71 */       return this.next.sendFinal(srcs, offs, len);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\SynchronizedMessageSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */