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
/*    */ public final class SynchronizedMessageSourceConduit
/*    */   extends AbstractSynchronizedSourceConduit<MessageSourceConduit>
/*    */   implements MessageSourceConduit
/*    */ {
/*    */   public SynchronizedMessageSourceConduit(MessageSourceConduit next) {
/* 38 */     super(next);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SynchronizedMessageSourceConduit(MessageSourceConduit next, Object lock) {
/* 48 */     super(next, lock);
/*    */   }
/*    */   
/*    */   public int receive(ByteBuffer dst) throws IOException {
/* 52 */     synchronized (this.lock) {
/* 53 */       return this.next.receive(dst);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long receive(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 58 */     synchronized (this.lock) {
/* 59 */       return this.next.receive(dsts, offs, len);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\SynchronizedMessageSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */