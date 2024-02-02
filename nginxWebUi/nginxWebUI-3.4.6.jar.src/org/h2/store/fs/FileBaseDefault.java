/*    */ package org.h2.store.fs;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import java.nio.channels.SeekableByteChannel;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class FileBaseDefault
/*    */   extends FileBase
/*    */ {
/* 18 */   private long position = 0L;
/*    */ 
/*    */   
/*    */   public final synchronized long position() throws IOException {
/* 22 */     return this.position;
/*    */   }
/*    */ 
/*    */   
/*    */   public final synchronized FileChannel position(long paramLong) throws IOException {
/* 27 */     if (paramLong < 0L) {
/* 28 */       throw new IllegalArgumentException();
/*    */     }
/* 30 */     this.position = paramLong;
/* 31 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public final synchronized int read(ByteBuffer paramByteBuffer) throws IOException {
/* 36 */     int i = read(paramByteBuffer, this.position);
/* 37 */     if (i > 0) {
/* 38 */       this.position += i;
/*    */     }
/* 40 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public final synchronized int write(ByteBuffer paramByteBuffer) throws IOException {
/* 45 */     int i = write(paramByteBuffer, this.position);
/* 46 */     if (i > 0) {
/* 47 */       this.position += i;
/*    */     }
/* 49 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public final synchronized FileChannel truncate(long paramLong) throws IOException {
/* 54 */     implTruncate(paramLong);
/* 55 */     if (paramLong < this.position) {
/* 56 */       this.position = paramLong;
/*    */     }
/* 58 */     return this;
/*    */   }
/*    */   
/*    */   protected abstract void implTruncate(long paramLong) throws IOException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\FileBaseDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */