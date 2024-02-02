/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BlockingStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private boolean resumed;
/*     */   
/*     */   public BlockingStreamSinkConduit(StreamSinkConduit next) {
/*  33 */     super(next);
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  37 */     if (this.resumed) return this.next.transferFrom(src, position, count); 
/*     */     long res;
/*  39 */     while ((res = this.next.transferFrom(src, position, count)) == 0L) {
/*  40 */       this.next.awaitWritable();
/*     */     }
/*  42 */     return res;
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  46 */     if (this.resumed) return this.next.transferFrom(source, count, throughBuffer); 
/*     */     long res;
/*  48 */     while ((res = this.next.transferFrom(source, count, throughBuffer)) == 0L) {
/*  49 */       this.next.awaitWritable();
/*     */     }
/*  51 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  56 */     return write(src, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  61 */     return write(srcs, offs, len, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*  66 */     return write(src, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  71 */     return write(srcs, offset, length, true);
/*     */   }
/*     */   
/*     */   private int write(ByteBuffer src, boolean writeFinal) throws IOException {
/*  75 */     if (this.resumed) {
/*  76 */       return doWrite(src, writeFinal);
/*     */     }
/*     */     int res;
/*  79 */     while ((res = doWrite(src, writeFinal)) == 0L) {
/*  80 */       this.next.awaitWritable();
/*     */     }
/*  82 */     return res;
/*     */   }
/*     */   
/*     */   private long write(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
/*  86 */     if (this.resumed) return doWrite(srcs, offs, len, writeFinal); 
/*     */     long res;
/*  88 */     while ((res = this.next.write(srcs, offs, len)) == 0L) {
/*  89 */       this.next.awaitWritable();
/*     */     }
/*  91 */     return res;
/*     */   }
/*     */   
/*     */   private int doWrite(ByteBuffer src, boolean writeFinal) throws IOException {
/*  95 */     if (writeFinal) {
/*  96 */       return this.next.writeFinal(src);
/*     */     }
/*  98 */     return this.next.write(src);
/*     */   }
/*     */   
/*     */   private long doWrite(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
/* 102 */     if (writeFinal) {
/* 103 */       return this.next.writeFinal(srcs, offs, len);
/*     */     }
/* 105 */     return this.next.write(srcs, offs, len);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 109 */     if (this.resumed) return this.next.flush(); 
/* 110 */     while (!this.next.flush()) {
/* 111 */       this.next.awaitWritable();
/*     */     }
/* 113 */     return true;
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 117 */     this.resumed = true;
/* 118 */     this.next.resumeWrites();
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 122 */     this.resumed = false;
/* 123 */     this.next.suspendWrites();
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 127 */     this.resumed = true;
/* 128 */     this.next.wakeupWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 132 */     return this.resumed;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\BlockingStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */