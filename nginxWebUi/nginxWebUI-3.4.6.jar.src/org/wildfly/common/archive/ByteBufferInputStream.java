/*    */ package org.wildfly.common.archive;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ final class ByteBufferInputStream extends InputStream {
/*    */   private final ByteBuffer[] bufs;
/*    */   private final long offset;
/*    */   private final long size;
/*    */   long pos;
/*    */   long mark;
/*    */   
/*    */   ByteBufferInputStream(ByteBuffer[] bufs, long offset, long size) {
/* 14 */     this.bufs = bufs;
/* 15 */     this.offset = offset;
/* 16 */     this.size = size;
/*    */   }
/*    */   
/*    */   public int read() {
/* 20 */     return (this.pos < this.size) ? Archive.getByte(this.bufs, this.offset + this.pos++) : -1;
/*    */   }
/*    */   
/*    */   public int read(byte[] b) {
/* 24 */     return read(b, 0, b.length);
/*    */   }
/*    */   
/*    */   public int read(byte[] b, int off, int len) {
/* 28 */     long rem = this.size - this.pos;
/* 29 */     if (rem == 0L) return -1; 
/* 30 */     int realLen = (int)Math.min(len, rem);
/* 31 */     if (realLen > 0) {
/* 32 */       Archive.readBytes(this.bufs, this.offset + this.pos, b, off, realLen);
/* 33 */       return realLen;
/*    */     } 
/* 35 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public long skip(long n) {
/* 40 */     long rem = this.size - this.pos;
/* 41 */     long cnt = Math.min(rem, n);
/* 42 */     if (cnt > 0L) {
/* 43 */       this.pos += cnt;
/* 44 */       return cnt;
/*    */     } 
/* 46 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int available() {
/* 51 */     return (int)Math.min(2147483647L, this.size - this.pos);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {}
/*    */   
/*    */   public void mark(int readLimit) {
/* 58 */     this.mark = this.pos;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 62 */     this.pos = this.mark;
/*    */   }
/*    */   
/*    */   public boolean markSupported() {
/* 66 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\archive\ByteBufferInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */