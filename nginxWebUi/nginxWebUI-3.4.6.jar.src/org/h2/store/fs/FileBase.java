/*    */ package org.h2.store.fs;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.MappedByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import java.nio.channels.FileLock;
/*    */ import java.nio.channels.ReadableByteChannel;
/*    */ import java.nio.channels.WritableByteChannel;
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
/*    */ public abstract class FileBase
/*    */   extends FileChannel
/*    */ {
/*    */   public synchronized int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 24 */     long l = position();
/* 25 */     position(paramLong);
/* 26 */     int i = read(paramByteBuffer);
/* 27 */     position(l);
/* 28 */     return i;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
/* 34 */     long l = position();
/* 35 */     position(paramLong);
/* 36 */     int i = write(paramByteBuffer);
/* 37 */     position(l);
/* 38 */     return i;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void force(boolean paramBoolean) throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void implCloseChannel() throws IOException {}
/*    */ 
/*    */ 
/*    */   
/*    */   public FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 54 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public MappedByteBuffer map(FileChannel.MapMode paramMapMode, long paramLong1, long paramLong2) throws IOException {
/* 60 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
/* 66 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2) throws IOException {
/* 72 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel) throws IOException {
/* 78 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
/* 84 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
/* 90 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\FileBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */