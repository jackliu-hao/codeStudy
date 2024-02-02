/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ public abstract class BoundedArchiveInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private final long end;
/*    */   private ByteBuffer singleByteBuffer;
/*    */   private long loc;
/*    */   
/*    */   public BoundedArchiveInputStream(long start, long remaining) {
/* 42 */     this.end = start + remaining;
/* 43 */     if (this.end < start)
/*    */     {
/* 45 */       throw new IllegalArgumentException("Invalid length of stream at offset=" + start + ", length=" + remaining);
/*    */     }
/* 47 */     this.loc = start;
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized int read() throws IOException {
/* 52 */     if (this.loc >= this.end) {
/* 53 */       return -1;
/*    */     }
/* 55 */     if (this.singleByteBuffer == null) {
/* 56 */       this.singleByteBuffer = ByteBuffer.allocate(1);
/*    */     } else {
/* 58 */       this.singleByteBuffer.rewind();
/*    */     } 
/* 60 */     int read = read(this.loc, this.singleByteBuffer);
/* 61 */     if (read < 1) {
/* 62 */       return -1;
/*    */     }
/* 64 */     this.loc++;
/* 65 */     return this.singleByteBuffer.get() & 0xFF;
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized int read(byte[] b, int off, int len) throws IOException {
/* 70 */     if (this.loc >= this.end) {
/* 71 */       return -1;
/*    */     }
/* 73 */     long maxLen = Math.min(len, this.end - this.loc);
/* 74 */     if (maxLen <= 0L) {
/* 75 */       return 0;
/*    */     }
/* 77 */     if (off < 0 || off > b.length || maxLen > (b.length - off)) {
/* 78 */       throw new IndexOutOfBoundsException("offset or len are out of bounds");
/*    */     }
/*    */     
/* 81 */     ByteBuffer buf = ByteBuffer.wrap(b, off, (int)maxLen);
/* 82 */     int ret = read(this.loc, buf);
/* 83 */     if (ret > 0) {
/* 84 */       this.loc += ret;
/*    */     }
/* 86 */     return ret;
/*    */   }
/*    */   
/*    */   protected abstract int read(long paramLong, ByteBuffer paramByteBuffer) throws IOException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\BoundedArchiveInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */