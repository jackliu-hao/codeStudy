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
/*    */ public final class BlockingStreamSourceConduit
/*    */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*    */ {
/*    */   private boolean resumed;
/*    */   
/*    */   public BlockingStreamSourceConduit(StreamSourceConduit next) {
/* 40 */     super(next);
/*    */   }
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 44 */     if (this.resumed) return this.next.transferTo(position, count, target); 
/*    */     long res;
/* 46 */     while ((res = this.next.transferTo(position, count, target)) == 0L) {
/* 47 */       this.next.awaitReadable();
/*    */     }
/* 49 */     return res;
/*    */   }
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 53 */     if (this.resumed) return this.next.transferTo(count, throughBuffer, target); 
/*    */     long res;
/* 55 */     while ((res = this.next.transferTo(count, throughBuffer, target)) == 0L && !throughBuffer.hasRemaining()) {
/* 56 */       this.next.awaitReadable();
/*    */     }
/* 58 */     return res;
/*    */   }
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 62 */     if (this.resumed) return this.next.read(dst); 
/*    */     int res;
/* 64 */     while ((res = this.next.read(dst)) == 0) {
/* 65 */       this.next.awaitReadable();
/*    */     }
/* 67 */     return res;
/*    */   }
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 71 */     if (this.resumed) return this.next.read(dsts, offs, len); 
/*    */     long res;
/* 73 */     while ((res = this.next.read(dsts, offs, len)) == 0L) {
/* 74 */       this.next.awaitReadable();
/*    */     }
/* 76 */     return res;
/*    */   }
/*    */   
/*    */   public void resumeReads() {
/* 80 */     this.resumed = true;
/* 81 */     this.next.resumeReads();
/*    */   }
/*    */   
/*    */   public void wakeupReads() {
/* 85 */     this.resumed = true;
/* 86 */     this.next.wakeupReads();
/*    */   }
/*    */   
/*    */   public void suspendReads() {
/* 90 */     this.resumed = false;
/* 91 */     this.next.suspendReads();
/*    */   }
/*    */   
/*    */   public boolean isReadResumed() {
/* 95 */     return this.resumed;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\BlockingStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */