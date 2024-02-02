/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import org.xnio.channels.StreamSourceChannel;
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
/*    */ public final class SynchronizedStreamSinkConduit
/*    */   extends AbstractSynchronizedSinkConduit<StreamSinkConduit>
/*    */   implements StreamSinkConduit
/*    */ {
/*    */   public SynchronizedStreamSinkConduit(StreamSinkConduit next) {
/* 41 */     super(next);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SynchronizedStreamSinkConduit(StreamSinkConduit next, Object lock) {
/* 51 */     super(next, lock);
/*    */   }
/*    */   
/*    */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 55 */     synchronized (this.lock) {
/* 56 */       return this.next.transferFrom(src, position, count);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 61 */     synchronized (this.lock) {
/* 62 */       return this.next.transferFrom(source, count, throughBuffer);
/*    */     } 
/*    */   }
/*    */   
/*    */   public int write(ByteBuffer src) throws IOException {
/* 67 */     synchronized (this.lock) {
/* 68 */       return this.next.write(src);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 73 */     synchronized (this.lock) {
/* 74 */       return this.next.write(srcs, offs, len);
/*    */     } 
/*    */   }
/*    */   
/*    */   public int writeFinal(ByteBuffer src) throws IOException {
/* 79 */     synchronized (this.lock) {
/* 80 */       return this.next.writeFinal(src);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 85 */     synchronized (this.lock) {
/* 86 */       return this.next.writeFinal(srcs, offset, length);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\SynchronizedStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */