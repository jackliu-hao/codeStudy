/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import java.nio.channels.WritableByteChannel;
/*    */ import org.xnio.channels.StreamSinkChannel;
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
/*    */ public final class MessageStreamSourceConduit
/*    */   extends AbstractSourceConduit<MessageSourceConduit>
/*    */   implements StreamSourceConduit
/*    */ {
/*    */   public MessageStreamSourceConduit(MessageSourceConduit next) {
/* 40 */     super(next);
/*    */   }
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 44 */     return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
/*    */   }
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 48 */     return Conduits.transfer(this, count, throughBuffer, (WritableByteChannel)target);
/*    */   }
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 52 */     return this.next.receive(dst);
/*    */   }
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 56 */     return this.next.receive(dsts, offs, len);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\MessageStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */