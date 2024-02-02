/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
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
/*    */ 
/*    */ 
/*    */ public final class SynchronizedStreamSourceConduit
/*    */   extends AbstractSynchronizedSourceConduit<StreamSourceConduit>
/*    */   implements StreamSourceConduit
/*    */ {
/*    */   public SynchronizedStreamSourceConduit(StreamSourceConduit next) {
/* 41 */     super(next);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SynchronizedStreamSourceConduit(StreamSourceConduit next, Object lock) {
/* 51 */     super(next, lock);
/*    */   }
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 55 */     synchronized (this.lock) {
/* 56 */       return this.next.transferTo(position, count, target);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 61 */     synchronized (this.lock) {
/* 62 */       return this.next.transferTo(count, throughBuffer, target);
/*    */     } 
/*    */   }
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 67 */     synchronized (this.lock) {
/* 68 */       return this.next.read(dst);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 73 */     synchronized (this.lock) {
/* 74 */       return this.next.read(dsts, offs, len);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\SynchronizedStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */