/*    */ package org.apache.commons.compress.archivers.sevenz;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.SeekableByteChannel;
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
/*    */ class BoundedSeekableByteChannelInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private static final int MAX_BUF_LEN = 8192;
/*    */   private final ByteBuffer buffer;
/*    */   private final SeekableByteChannel channel;
/*    */   private long bytesRemaining;
/*    */   
/*    */   public BoundedSeekableByteChannelInputStream(SeekableByteChannel channel, long size) {
/* 33 */     this.channel = channel;
/* 34 */     this.bytesRemaining = size;
/* 35 */     if (size < 8192L && size > 0L) {
/* 36 */       this.buffer = ByteBuffer.allocate((int)size);
/*    */     } else {
/* 38 */       this.buffer = ByteBuffer.allocate(8192);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 44 */     if (this.bytesRemaining > 0L) {
/* 45 */       this.bytesRemaining--;
/* 46 */       int read = read(1);
/* 47 */       if (read < 0) {
/* 48 */         return read;
/*    */       }
/* 50 */       return this.buffer.get() & 0xFF;
/*    */     } 
/* 52 */     return -1;
/*    */   }
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
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/*    */     int bytesRead;
/*    */     ByteBuffer buf;
/* 68 */     if (len == 0) {
/* 69 */       return 0;
/*    */     }
/* 71 */     if (this.bytesRemaining <= 0L) {
/* 72 */       return -1;
/*    */     }
/* 74 */     int bytesToRead = len;
/* 75 */     if (bytesToRead > this.bytesRemaining) {
/* 76 */       bytesToRead = (int)this.bytesRemaining;
/*    */     }
/*    */ 
/*    */     
/* 80 */     if (bytesToRead <= this.buffer.capacity()) {
/* 81 */       buf = this.buffer;
/* 82 */       bytesRead = read(bytesToRead);
/*    */     } else {
/* 84 */       buf = ByteBuffer.allocate(bytesToRead);
/* 85 */       bytesRead = this.channel.read(buf);
/* 86 */       buf.flip();
/*    */     } 
/* 88 */     if (bytesRead >= 0) {
/* 89 */       buf.get(b, off, bytesRead);
/* 90 */       this.bytesRemaining -= bytesRead;
/*    */     } 
/* 92 */     return bytesRead;
/*    */   }
/*    */   
/*    */   private int read(int len) throws IOException {
/* 96 */     this.buffer.rewind().limit(len);
/* 97 */     int read = this.channel.read(this.buffer);
/* 98 */     this.buffer.flip();
/* 99 */     return read;
/*    */   }
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\BoundedSeekableByteChannelInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */